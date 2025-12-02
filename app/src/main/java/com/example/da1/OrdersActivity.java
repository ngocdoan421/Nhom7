package com.example.da1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.adapters.OrderUserAdapter;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.admin.order.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {
    private static final String TAG = "OrdersActivity";
    private ImageButton btnBack;
    private RecyclerView recyclerViewOrders;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private OrderUserAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "onCreate started");
            setContentView(R.layout.activity_orders);
            Log.d(TAG, "Layout inflated successfully");

            // Khởi tạo ApiClient
            ApiClient.init(this);
            Log.d(TAG, "ApiClient initialized");

            initViews();
            Log.d(TAG, "Views initialized");
            
            setupRecyclerView();
            Log.d(TAG, "RecyclerView setup completed");
            
            loadOrders();
            Log.d(TAG, "Load orders called");
        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR in onCreate", e);
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Không finish() để có thể xem log
        }
    }

    private void initViews() {
        try {
            Log.d(TAG, "initViews started");
            btnBack = findViewById(R.id.btnBack);
            Log.d(TAG, "btnBack: " + (btnBack != null ? "found" : "NULL"));
            
            recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
            Log.d(TAG, "recyclerViewOrders: " + (recyclerViewOrders != null ? "found" : "NULL"));
            
            progressBar = findViewById(R.id.progressBar);
            Log.d(TAG, "progressBar: " + (progressBar != null ? "found" : "NULL"));
            
            tvEmpty = findViewById(R.id.tvEmpty);
            Log.d(TAG, "tvEmpty: " + (tvEmpty != null ? "found" : "NULL"));

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    try {
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in btnBack click", e);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in initViews", e);
            throw new RuntimeException("Failed to initialize views", e);
        }
    }

    private void setupRecyclerView() {
        try {
            Log.d(TAG, "setupRecyclerView started");
            
            if (recyclerViewOrders == null) {
                Log.e(TAG, "recyclerViewOrders is NULL, cannot setup");
                Toast.makeText(this, "Lỗi: Không tìm thấy RecyclerView", Toast.LENGTH_LONG).show();
                return;
            }
            
            orderList = new ArrayList<>();
            Log.d(TAG, "orderList created");
            
            orderAdapter = new OrderUserAdapter(orderList, order -> {
                try {
                    Log.d(TAG, "Order clicked: " + (order != null ? order.getId() : "null"));
                    viewOrderDetails(order);
                } catch (Exception e) {
                    Log.e(TAG, "Error viewing order details", e);
                    Toast.makeText(OrdersActivity.this, "Lỗi xem chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            Log.d(TAG, "orderAdapter created");

            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "LayoutManager set");
            
            recyclerViewOrders.setAdapter(orderAdapter);
            Log.d(TAG, "Adapter set to RecyclerView");
        } catch (Exception e) {
            Log.e(TAG, "Error in setupRecyclerView", e);
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadOrders() {
        try {
            Log.d(TAG, "loadOrders started");
            
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.GONE);
            }

            // Gọi API để lấy danh sách đơn hàng của user hiện tại
            Call<ApiResponse<List<Order>>> call = ApiService.getOrderApiService().getAllOrders(
                null, // status - null để lấy tất cả
                1,    // page
                50    // limit
            );
            
            Log.d(TAG, "API call created");

            call.enqueue(new Callback<ApiResponse<List<Order>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Order>>> call, 
                                     Response<ApiResponse<List<Order>>> response) {
                    try {
                        Log.d(TAG, "onResponse: code=" + response.code());
                        
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Order>> apiResponse = response.body();
                            Log.d(TAG, "API Response - Success: " + apiResponse.isSuccess());
                            Log.d(TAG, "API Response - Message: " + apiResponse.getMessage());
                            
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                List<Order> orders = apiResponse.getData();
                                Log.d(TAG, "Received " + orders.size() + " orders from API");
                                
                                orderList.clear();
                                orderList.addAll(orders);
                                orderAdapter.notifyDataSetChanged();

                                // Hiển thị thông báo nếu không có đơn hàng
                                if (orderList.isEmpty()) {
                                    Log.d(TAG, "No orders found");
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tvEmpty.setText("Chưa có đơn hàng nào");
                                    }
                                } else {
                                    Log.d(TAG, "Displaying " + orderList.size() + " orders");
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                String errorMsg = apiResponse.getMessage() != null 
                                    ? apiResponse.getMessage() 
                                    : "Không thể tải danh sách đơn hàng";
                                Log.e(TAG, "API returned error: " + errorMsg);
                                Toast.makeText(OrdersActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                if (tvEmpty != null) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    tvEmpty.setText(errorMsg);
                                }
                            }
                        } else {
                            // Log chi tiết lỗi
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error body: " + errorBody);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            
                            String errorMsg = "Lỗi tải đơn hàng (Code: " + response.code() + ")";
                            if (response.code() == 401) {
                                errorMsg = "Vui lòng đăng nhập để xem đơn hàng";
                                navigateToLogin();
                            } else if (response.code() == 403) {
                                errorMsg = "Phiên đăng nhập đã hết hạn";
                                clearTokenAndNavigateToLogin();
                            }
                            Log.e(TAG, errorMsg);
                            Toast.makeText(OrdersActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            if (tvEmpty != null) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                tvEmpty.setText(errorMsg);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in onResponse", e);
                        e.printStackTrace();
                        Toast.makeText(OrdersActivity.this, "Lỗi xử lý response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                    try {
                        Log.e(TAG, "onFailure", t);
                        t.printStackTrace();
                        
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        String errorMsg = "Lỗi kết nối: " + t.getMessage();
                        Toast.makeText(OrdersActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        if (tvEmpty != null) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvEmpty.setText(errorMsg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in onFailure handler", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in loadOrders", e);
            e.printStackTrace();
            Toast.makeText(this, "Lỗi gọi API: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void viewOrderDetails(Order order) {
        try {
            // TODO: Mở màn hình chi tiết đơn hàng
            Toast.makeText(this, "Xem chi tiết đơn hàng: " + (order != null ? order.getId() : "null"), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error in viewOrderDetails", e);
        }
    }

    private void navigateToLogin() {
        try {
            Intent intent = new Intent(this, com.example.da1.auth.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to login", e);
        }
    }

    private void clearTokenAndNavigateToLogin() {
        try {
            com.example.da1.utils.SharedPreferencesHelper.clearToken(this);
            ApiClient.resetClient();
            navigateToLogin();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing token", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Reload đơn hàng khi quay lại màn hình
            loadOrders();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume", e);
        }
    }
}
