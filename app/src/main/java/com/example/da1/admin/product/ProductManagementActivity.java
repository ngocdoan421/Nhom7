package com.example.da1.admin.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.admin.AdminDashboardActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FloatingActionButton fabAddProduct;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        initViews();
        setupRecyclerView();
        loadProducts();
        setupListeners();
    }

    private void initViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onEditClick(Product product) {
                openEditProductDialog(product);
            }

            @Override
            public void onDeleteClick(Product product) {
                deleteProduct(product);
            }
        });

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        // TODO: Load products from API/database
        productList.clear();
        productAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        fabAddProduct.setOnClickListener(v -> openAddProductDialog());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProductManagementActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openAddProductDialog() {
        // TODO: Open dialog to add new product
        Toast.makeText(this, "Thêm sản phẩm mới", Toast.LENGTH_SHORT).show();
    }

    private void openEditProductDialog(Product product) {
        // TODO: Open dialog to edit product
        Toast.makeText(this, "Chỉnh sửa sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    private void deleteProduct(Product product) {
        // TODO: Delete product from API/database
        Toast.makeText(this, "Xóa sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show();
    }
}

