package com.example.da1.models;

public class CartItemResponse {
    @com.google.gson.annotations.SerializedName("_id")
    private String id;
    @com.google.gson.annotations.SerializedName("billid")
    private String billid;
    
    // productid có thể là string (ObjectId) hoặc object (sau khi populate)
    // Sử dụng Object để Gson tự parse, sau đó convert trong getProductid()
    private Object productid;
    private int quantity;
    private String size;
    private String color;
    private double price;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    // Helper method để lấy ProductData từ productid
    public ProductData getProductid() {
        if (productid == null) {
            android.util.Log.w("CartItemResponse", "productid is null");
            return null;
        }
        
        android.util.Log.d("CartItemResponse", "productid type: " + productid.getClass().getName());
        
        // Nếu là ProductData object thì cast trực tiếp (không bao giờ xảy ra vì Gson parse thành Map)
        if (productid instanceof ProductData) {
            return (ProductData) productid;
        }
        
        // Nếu là string (ObjectId), tạo ProductData với id
        if (productid instanceof String) {
            ProductData data = new ProductData();
            data.setId((String) productid);
            android.util.Log.d("CartItemResponse", "productid is String: " + productid);
            return data;
        }
        
        // Nếu là Map/JsonObject (Gson parse object thành LinkedTreeMap), convert sang ProductData
        try {
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .setLenient() // Cho phép parse linh hoạt hơn
                .create();
            String json = gson.toJson(productid);
            android.util.Log.d("CartItemResponse", "Converting productid from Map to ProductData: " + json);
            ProductData result = gson.fromJson(json, ProductData.class);
            
            // Log chi tiết để debug
            android.util.Log.d("CartItemResponse", "Converted ProductData - ID: " + result.getId() + ", Name: " + result.getName() + ", Price: " + result.getPrice());
            if (result.getName() == null || result.getName().isEmpty()) {
                android.util.Log.w("CartItemResponse", "ProductData name is null or empty after conversion!");
            }
            
            return result;
        } catch (com.google.gson.JsonSyntaxException e) {
            android.util.Log.e("CartItemResponse", "JsonSyntaxException converting productid to ProductData", e);
            android.util.Log.e("CartItemResponse", "Error message: " + e.getMessage());
            // Thử parse thủ công từ Map
            try {
                if (productid instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) productid;
                    ProductData data = new ProductData();
                    if (map.containsKey("_id")) {
                        data.setId(String.valueOf(map.get("_id")));
                    }
                    if (map.containsKey("name")) {
                        data.setName(String.valueOf(map.get("name")));
                    }
                    if (map.containsKey("price")) {
                        Object priceObj = map.get("price");
                        if (priceObj instanceof Number) {
                            data.setPrice(((Number) priceObj).doubleValue());
                        }
                    }
                    if (map.containsKey("image")) {
                        Object imageObj = map.get("image");
                        if (imageObj instanceof java.util.List) {
                            @SuppressWarnings("unchecked")
                            java.util.List<String> imageList = (java.util.List<String>) imageObj;
                            data.setImage(imageList);
                        } else if (imageObj instanceof String) {
                            java.util.List<String> imageList = new java.util.ArrayList<>();
                            imageList.add((String) imageObj);
                            data.setImage(imageList);
                        }
                    }
                    android.util.Log.d("CartItemResponse", "Manually parsed ProductData - ID: " + data.getId() + ", Name: " + data.getName());
                    return data;
                }
            } catch (Exception e2) {
                android.util.Log.e("CartItemResponse", "Error in manual parsing", e2);
            }
            // Nếu không convert được, tạo ProductData với id từ toString()
            ProductData data = new ProductData();
            data.setId(productid.toString());
            data.setName("Sản phẩm");
            return data;
        } catch (Exception e) {
            android.util.Log.e("CartItemResponse", "Error converting productid to ProductData", e);
            // Nếu không convert được, tạo ProductData với id từ toString()
            ProductData data = new ProductData();
            data.setId(productid.toString());
            data.setName("Sản phẩm");
            return data;
        }
    }

    public void setProductid(Object productid) {
        this.productid = productid;
    }
    
    // Getter để lấy raw productid (có thể là string hoặc object)
    public Object getProductidRaw() {
        return productid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class ProductData {
        @com.google.gson.annotations.SerializedName("_id")
        private String id;
        private String name;
        private double price;
        @com.google.gson.annotations.SerializedName("image")
        private java.util.List<String> image;
        @com.google.gson.annotations.SerializedName("categoryid")
        private Object categoryid;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getImage() {
            // Lấy ảnh đầu tiên từ mảng image
            if (image != null && !image.isEmpty()) {
                return image.get(0);
            }
            return null;
        }

        public void setImage(java.util.List<String> image) {
            this.image = image;
        }

        public java.util.List<String> getImageList() {
            return image;
        }
        
        public Object getCategoryid() {
            return categoryid;
        }
        
        public void setCategoryid(Object categoryid) {
            this.categoryid = categoryid;
        }
    }
}

