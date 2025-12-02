package com.example.da1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.da1.R;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import android.util.Log;
import com.example.da1.models.AddToCartRequest;
import com.example.da1.models.CartItemResponse;
import com.example.da1.models.ProductItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvProductDescription;
    private TextView tvProductPrice;
    private TextView tvRating;
    private TextView tvReviewCount;
    private Button btnSizeS, btnSizeM, btnSizeL, btnSizeXL, btnSize2XL;
    private Button btnQuantityMinus, btnQuantityPlus;
    private TextView tvQuantity;
    private Button btnAddToCart;
    private View viewColorBlack, viewColorGrey, viewColorWhite, viewColorLightGrey;

    private int quantity = 1;
    private String selectedSize = "";
    private String selectedColor = "";
    private ProductItem currentProduct;
    private ProgressBar progressBar;
    private List<Button> sizeButtons = new ArrayList<>();
    private List<View> colorViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo ApiClient
        ApiClient.init(this);

        initViews();
        loadProductData();
        setupListeners();
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvRating = findViewById(R.id.tvRating);
        tvReviewCount = findViewById(R.id.tvReviewCount);
        
        btnSizeS = findViewById(R.id.btnSizeS);
        btnSizeM = findViewById(R.id.btnSizeM);
        btnSizeL = findViewById(R.id.btnSizeL);
        btnSizeXL = findViewById(R.id.btnSizeXL);
        btnSize2XL = findViewById(R.id.btnSize2XL);
        
        btnQuantityMinus = findViewById(R.id.btnQuantityMinus);
        btnQuantityPlus = findViewById(R.id.btnQuantityPlus);
        tvQuantity = findViewById(R.id.tvQuantity);
        
        btnAddToCart = findViewById(R.id.btnAddToCart);
        
        viewColorBlack = findViewById(R.id.viewColorBlack);
        viewColorGrey = findViewById(R.id.viewColorGrey);
        viewColorWhite = findViewById(R.id.viewColorWhite);
        viewColorLightGrey = findViewById(R.id.viewColorLightGrey);
        
        progressBar = findViewById(R.id.progressBar);
        
        // Thêm size buttons vào list
        if (btnSizeS != null) sizeButtons.add(btnSizeS);
        if (btnSizeM != null) sizeButtons.add(btnSizeM);
        if (btnSizeL != null) sizeButtons.add(btnSizeL);
        if (btnSizeXL != null) sizeButtons.add(btnSizeXL);
        if (btnSize2XL != null) sizeButtons.add(btnSize2XL);
        
        // Thêm color views vào list
        if (viewColorBlack != null) colorViews.add(viewColorBlack);
        if (viewColorGrey != null) colorViews.add(viewColorGrey);
        if (viewColorWhite != null) colorViews.add(viewColorWhite);
        if (viewColorLightGrey != null) colorViews.add(viewColorLightGrey);
        
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadProductData() {
        String productId = getIntent().getStringExtra("product_id");
        
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        showLoading(true);
        
        Call<ApiResponse<ProductItem>> call = ApiService.getProductApiService().getProductById(productId);
        
        call.enqueue(new Callback<ApiResponse<ProductItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductItem>> call, Response<ApiResponse<ProductItem>> response) {
                showLoading(false);
                
                Log.d("ProductDetail", "Response code: " + response.code());
                Log.d("ProductDetail", "Response isSuccessful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductItem> apiResponse = response.body();
                    Log.d("ProductDetail", "API Response success: " + apiResponse.isSuccess());
                    Log.d("ProductDetail", "API Response message: " + apiResponse.getMessage());
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        currentProduct = apiResponse.getData();
                        Log.d("ProductDetail", "Product loaded: " + currentProduct.getName());
                        displayProduct(currentProduct);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Không tìm thấy sản phẩm";
                        Log.e("ProductDetail", "API returned error: " + errorMsg);
                        Toast.makeText(ProductDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // Log chi tiết lỗi để debug
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e("ProductDetail", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("ProductDetail", "Error reading error body", e);
                    }
                    
                    String errorMsg = "Lỗi tải sản phẩm (Code: " + response.code() + ")";
                    Toast.makeText(ProductDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("ProductDetail", "HTTP Error: " + response.code());
                    if (!errorBody.isEmpty()) {
                        Log.e("ProductDetail", "Error response: " + errorBody);
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductItem>> call, Throwable t) {
                showLoading(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(ProductDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("ProductDetail", "Network error", t);
                t.printStackTrace();
            }
        });
    }
    
    private void displayProduct(ProductItem product) {
        // Hiển thị thông tin sản phẩm
        tvProductName.setText(product.getName());
        
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvProductDescription.setText(product.getDescription());
        }
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvProductPrice.setText(currencyFormat.format(product.getPrice()));
        
        tvRating.setText(String.valueOf(product.getRating()));
        tvReviewCount.setText("(" + product.getReviewCount() + " đánh giá)");
        
        // Load ảnh - API trả về image là mảng, lấy ảnh đầu tiên
        String imageUrl = product.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            // Nếu imageUrl là null, có thể API trả về image là mảng
            // ProductItem sẽ cần xử lý để lấy ảnh đầu tiên từ mảng
        }
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Picasso.get().load(imageUrl).into(ivProductImage);
            } else if (ivProductImage != null) {
                // Có thể là local resource
            }
        }
        
        // Hiển thị sizes
        if (product.getSizes() != null && !product.getSizes().isEmpty()) {
            setupSizeButtons(product.getSizes());
        } else {
            // Default sizes
            List<String> defaultSizes = new ArrayList<>();
            defaultSizes.add("S");
            defaultSizes.add("M");
            defaultSizes.add("L");
            defaultSizes.add("XL");
            defaultSizes.add("2XL");
            setupSizeButtons(defaultSizes);
        }
        
        // Hiển thị colors
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            setupColorViews(product.getColors());
        }
        
        // Set default selection
        if (!sizeButtons.isEmpty() && selectedSize.isEmpty() && sizeButtons.get(0).getVisibility() == View.VISIBLE) {
            selectedSize = sizeButtons.get(0).getText().toString();
            updateSizeSelection(sizeButtons.get(0));
        }
        
        // Set initial quantity display
        tvQuantity.setText(String.valueOf(quantity));
    }
    
    private void setupSizeButtons(List<String> sizes) {
        // Ẩn tất cả buttons
        for (Button btn : sizeButtons) {
            btn.setVisibility(View.GONE);
        }
        
        // Hiển thị và set text cho các buttons tương ứng
        for (int i = 0; i < sizes.size() && i < sizeButtons.size(); i++) {
            final int index = i; // Tạo biến final để dùng trong lambda
            final String size = sizes.get(i); // Tạo biến final cho size
            Button btn = sizeButtons.get(i);
            btn.setText(size);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(v -> {
                selectedSize = size;
                updateSizeSelection(btn);
            });
        }
    }
    
    private void setupColorViews(List<String> colors) {
        // Map colors to views (có thể mở rộng)
        // Hiện tại chỉ hiển thị các màu có sẵn trong layout
    }

    private void setupListeners() {
        // Size buttons được setup trong setupSizeButtons() sau khi load product
        
        // Quantity buttons
        btnQuantityMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });
        btnQuantityPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        // Color selection
        viewColorBlack.setOnClickListener(v -> {
            selectedColor = "black";
            updateColorSelection(viewColorBlack);
        });
        viewColorGrey.setOnClickListener(v -> {
            selectedColor = "grey";
            updateColorSelection(viewColorGrey);
        });
        viewColorWhite.setOnClickListener(v -> {
            selectedColor = "white";
            updateColorSelection(viewColorWhite);
        });
        viewColorLightGrey.setOnClickListener(v -> {
            selectedColor = "lightgrey";
            updateColorSelection(viewColorLightGrey);
        });

        // Add to cart
        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct == null) {
                Toast.makeText(this, "Sản phẩm chưa được tải", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedSize == null || selectedSize.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn size", Toast.LENGTH_SHORT).show();
                return;
            }
            
            addToCart();
        });
    }

    private void updateSizeSelection(Button selectedButton) {
        btnSizeS.setSelected(false);
        btnSizeM.setSelected(false);
        btnSizeL.setSelected(false);
        btnSizeXL.setSelected(false);
        btnSize2XL.setSelected(false);
        
        selectedButton.setSelected(true);
    }

    private void updateColorSelection(View selectedView) {
        viewColorBlack.setSelected(false);
        viewColorGrey.setSelected(false);
        viewColorWhite.setSelected(false);
        viewColorLightGrey.setSelected(false);
        
        selectedView.setSelected(true);
    }
    
    private void addToCart() {
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm chưa được tải", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String productId = currentProduct.getId();
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d("ProductDetail", "Adding to cart - Product ID: " + productId);
        Log.d("ProductDetail", "Quantity: " + quantity);
        Log.d("ProductDetail", "Size: " + selectedSize);
        Log.d("ProductDetail", "Color: " + selectedColor);
        
        showLoading(true);
        btnAddToCart.setEnabled(false);
        
        AddToCartRequest request = new AddToCartRequest(
            productId,
            quantity,
            selectedSize,
            selectedColor.isEmpty() ? null : selectedColor
        );
        
        Log.d("ProductDetail", "Request: " + request.toString());
        
        Call<ApiResponse<CartItemResponse>> call = ApiService.getCartApiService().addToCart(request);
        
        call.enqueue(new Callback<ApiResponse<CartItemResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartItemResponse>> call, 
                                 Response<ApiResponse<CartItemResponse>> response) {
                showLoading(false);
                btnAddToCart.setEnabled(true);
                
                Log.d("ProductDetail", "Add to cart response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartItemResponse> apiResponse = response.body();
                    Log.d("ProductDetail", "API Response success: " + apiResponse.isSuccess());
                    Log.d("ProductDetail", "API Response message: " + apiResponse.getMessage());
                    
                    if (apiResponse.isSuccess()) {
                        String message = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Đã thêm vào giỏ hàng";
                        Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        
                        Log.d("ProductDetail", "Add to cart successful: " + message);
                        
                        // Có thể chuyển đến CartActivity hoặc ở lại
                        // Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                        // startActivity(intent);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Thêm vào giỏ hàng thất bại";
                        Toast.makeText(ProductDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("ProductDetail", "Add to cart failed: " + errorMsg);
                    }
                } else {
                    // Log chi tiết lỗi
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e("ProductDetail", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("ProductDetail", "Error reading error body", e);
                    }
                    
                    String errorMsg = "Lỗi thêm vào giỏ hàng (Code: " + response.code() + ")";
                    if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập để thêm vào giỏ hàng";
                    } else if (response.code() == 400) {
                        errorMsg = "Dữ liệu không hợp lệ";
                    }
                    Toast.makeText(ProductDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("ProductDetail", "HTTP Error: " + response.code());
                    if (!errorBody.isEmpty()) {
                        Log.e("ProductDetail", "Error response: " + errorBody);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartItemResponse>> call, Throwable t) {
                showLoading(false);
                btnAddToCart.setEnabled(true);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(ProductDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("ProductDetail", "Network error when adding to cart", t);
                t.printStackTrace();
            }
        });
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}

