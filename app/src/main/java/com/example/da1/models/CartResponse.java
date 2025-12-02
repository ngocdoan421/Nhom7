package com.example.da1.models;

import java.util.List;

public class CartResponse {
    private CartBill bill;
    private List<CartItemResponse> items; // API trả về CartItemResponse, không phải CartItem
    private double totalAmount;

    public CartBill getBill() {
        return bill;
    }

    public void setBill(CartBill bill) {
        this.bill = bill;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public static class CartBill {
        @com.google.gson.annotations.SerializedName("_id")
        private String id;
        @com.google.gson.annotations.SerializedName("accountid")
        private String accountid;
        private String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAccountid() {
            return accountid;
        }

        public void setAccountid(String accountid) {
            this.accountid = accountid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

