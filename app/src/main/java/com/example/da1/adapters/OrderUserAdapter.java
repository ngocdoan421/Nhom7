package com.example.da1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.admin.order.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderUserAdapter extends RecyclerView.Adapter<OrderUserAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderUserAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.onOrderClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.util.Log.d("OrderUserAdapter", "onCreateViewHolder called");
        try {
            android.util.Log.d("OrderUserAdapter", "Inflating layout: item_order_user");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_user, parent, false);
            android.util.Log.d("OrderUserAdapter", "Layout inflated successfully");
            
            if (view == null) {
                android.util.Log.e("OrderUserAdapter", "Failed to inflate item_order_user layout - view is null");
                // Tạo một view tạm thời để tránh crash
                view = new android.widget.TextView(parent.getContext());
                ((android.widget.TextView) view).setText("Error loading order item");
            }
            
            OrderViewHolder holder = new OrderViewHolder(view);
            android.util.Log.d("OrderUserAdapter", "ViewHolder created successfully");
            return holder;
        } catch (android.content.res.Resources.NotFoundException e) {
            android.util.Log.e("OrderUserAdapter", "Layout resource not found", e);
            // Tạo một view tạm thời để tránh crash
            android.widget.TextView errorView = new android.widget.TextView(parent.getContext());
            errorView.setText("Layout not found: " + e.getMessage());
            errorView.setPadding(16, 16, 16, 16);
            return new OrderViewHolder(errorView);
        } catch (Exception e) {
            android.util.Log.e("OrderUserAdapter", "Error creating ViewHolder", e);
            e.printStackTrace();
            // Tạo một view tạm thời để tránh crash
            android.widget.TextView errorView = new android.widget.TextView(parent.getContext());
            errorView.setText("Error: " + e.getClass().getSimpleName());
            errorView.setPadding(16, 16, 16, 16);
            return new OrderViewHolder(errorView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        try {
            if (position < 0 || position >= orderList.size()) {
                android.util.Log.e("OrderUserAdapter", "Invalid position: " + position + ", list size: " + orderList.size());
                return;
            }
            Order order = orderList.get(position);
            if (order == null) {
                android.util.Log.e("OrderUserAdapter", "Order at position " + position + " is null");
                return;
            }
            holder.bind(order);
        } catch (Exception e) {
            android.util.Log.e("OrderUserAdapter", "Error in onBindViewHolder at position " + position, e);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvOrderDate;
        private TextView tvTotalAmount;
        private TextView tvOrderStatus;
        private TextView tvPaymentMethod;
        private TextView btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                // Chỉ tìm view nếu itemView không phải là TextView (error view)
                if (!(itemView instanceof android.widget.TextView)) {
                    tvOrderId = itemView.findViewById(R.id.tvOrderId);
                    tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                    tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
                    tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
                    tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
                    btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
                    
                    // Log nếu không tìm thấy view
                    if (tvOrderId == null) android.util.Log.w("OrderUserAdapter", "tvOrderId not found");
                    if (tvOrderDate == null) android.util.Log.w("OrderUserAdapter", "tvOrderDate not found");
                    if (tvTotalAmount == null) android.util.Log.w("OrderUserAdapter", "tvTotalAmount not found");
                    if (tvOrderStatus == null) android.util.Log.w("OrderUserAdapter", "tvOrderStatus not found");
                    if (tvPaymentMethod == null) android.util.Log.w("OrderUserAdapter", "tvPaymentMethod not found");
                    if (btnViewDetails == null) android.util.Log.w("OrderUserAdapter", "btnViewDetails not found");
                }
            } catch (Exception e) {
                android.util.Log.e("OrderUserAdapter", "Error in ViewHolder constructor", e);
            }
        }

        public void bind(Order order) {
            try {
                if (order == null) {
                    android.util.Log.e("OrderUserAdapter", "Order is null");
                    return;
                }

                // Hiển thị mã đơn hàng (rút ngắn nếu quá dài)
                String orderId = order.getId();
                if (orderId != null && orderId.length() > 12) {
                    orderId = orderId.substring(0, 12) + "...";
                }
                if (tvOrderId != null) {
                    tvOrderId.setText("Mã đơn: " + (orderId != null ? orderId : "N/A"));
                }

                // Hiển thị ngày đặt hàng
                if (tvOrderDate != null) {
                    try {
                        Date orderDate = order.getOrderDate();
                        if (orderDate != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            tvOrderDate.setText("Ngày đặt: " + sdf.format(orderDate));
                        } else {
                            tvOrderDate.setText("Ngày đặt: N/A");
                        }
                    } catch (Exception e) {
                        android.util.Log.e("OrderUserAdapter", "Error formatting date", e);
                        tvOrderDate.setText("Ngày đặt: N/A");
                    }
                }

                // Hiển thị tổng tiền
                if (tvTotalAmount != null) {
                    try {
                        double totalAmount = order.getTotalAmount();
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                        String formattedPrice = numberFormat.format((long)totalAmount) + "₫";
                        tvTotalAmount.setText("Tổng tiền: " + formattedPrice);
                    } catch (Exception e) {
                        android.util.Log.e("OrderUserAdapter", "Error formatting price", e);
                        tvTotalAmount.setText("Tổng tiền: N/A");
                    }
                }

                // Hiển thị trạng thái
                if (tvOrderStatus != null) {
                    try {
                        String statusText = getStatusText(order.getStatus());
                        tvOrderStatus.setText("Trạng thái: " + statusText);
                        
                        // Đặt màu cho trạng thái
                        int statusColor = getStatusColor(order.getStatus());
                        tvOrderStatus.setTextColor(statusColor);
                    } catch (Exception e) {
                        android.util.Log.e("OrderUserAdapter", "Error setting status", e);
                        tvOrderStatus.setText("Trạng thái: N/A");
                    }
                }

                // Hiển thị phương thức thanh toán
                if (tvPaymentMethod != null) {
                    try {
                        String paymentMethod = order.getPaymentMethod();
                        if (paymentMethod != null && !paymentMethod.isEmpty()) {
                            tvPaymentMethod.setText("Thanh toán: " + getPaymentMethodText(paymentMethod));
                        } else {
                            tvPaymentMethod.setText("Thanh toán: Chưa xác định");
                        }
                    } catch (Exception e) {
                        android.util.Log.e("OrderUserAdapter", "Error setting payment method", e);
                        tvPaymentMethod.setText("Thanh toán: N/A");
                    }
                }

                // Xử lý click vào item
                itemView.setOnClickListener(v -> {
                    try {
                        if (onOrderClickListener != null) {
                            onOrderClickListener.onOrderClick(order);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("OrderUserAdapter", "Error on item click", e);
                    }
                });

                // Xử lý click vào nút chi tiết
                if (btnViewDetails != null) {
                    btnViewDetails.setOnClickListener(v -> {
                        try {
                            if (onOrderClickListener != null) {
                                onOrderClickListener.onOrderClick(order);
                            }
                        } catch (Exception e) {
                            android.util.Log.e("OrderUserAdapter", "Error on details click", e);
                        }
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("OrderUserAdapter", "Error binding order", e);
            }
        }

        private String getStatusText(String status) {
            if (status == null) return "Không xác định";
            
            switch (status.toLowerCase()) {
                case "pending":
                    return "Chờ xử lý";
                case "processing":
                    return "Đang xử lý";
                case "shipped":
                    return "Đang giao hàng";
                case "delivered":
                    return "Đã giao hàng";
                case "cancelled":
                    return "Đã hủy";
                default:
                    return status;
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return 0xFF757575; // Gray
            
            switch (status.toLowerCase()) {
                case "pending":
                    return 0xFFFF9800; // Orange
                case "processing":
                    return 0xFF2196F3; // Blue
                case "shipped":
                    return 0xFF9C27B0; // Purple
                case "delivered":
                    return 0xFF4CAF50; // Green
                case "cancelled":
                    return 0xFFF44336; // Red
                default:
                    return 0xFF757575; // Gray
            }
        }

        private String getPaymentMethodText(String method) {
            if (method == null) return "Chưa xác định";
            
            switch (method.toLowerCase()) {
                case "card":
                    return "Thẻ tín dụng";
                case "bank_transfer":
                    return "Chuyển khoản";
                case "buy_now_pay_later":
                    return "Mua trả sau";
                default:
                    return method;
            }
        }
    }
}

