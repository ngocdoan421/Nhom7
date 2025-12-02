package com.example.da1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.models.LoginResponse;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    
    private ImageButton btnBack;
    private ImageView ivAvatar;
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private Button btnSave;
    private Button btnChangePassword;
    private ProgressBar progressBar;
    
    private LoginResponse.UserData currentUser;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Khởi tạo ApiClient
        ApiClient.init(this);

        initViews();
        setupListeners();
        loadUserInfo();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivAvatar = findViewById(R.id.ivAvatar);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);
        
        // Disable edit mode ban đầu
        setEditMode(false);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnSave.setOnClickListener(v -> {
            if (isEditMode) {
                saveUserInfo();
            } else {
                setEditMode(true);
            }
        });
        
        btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });
        
        ivAvatar.setOnClickListener(v -> {
            // TODO: Cho phép chọn ảnh từ gallery
            Toast.makeText(this, "Chức năng đổi ảnh đại diện sẽ được thêm sau", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserInfo() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        Call<ApiResponse<LoginResponse.UserData>> call = ApiService.getUserApiService().getCurrentUser();
        
        call.enqueue(new Callback<ApiResponse<LoginResponse.UserData>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse.UserData>> call, 
                                 Response<ApiResponse<LoginResponse.UserData>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse.UserData> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        currentUser = apiResponse.getData();
                        displayUserInfo(currentUser);
                        Log.d(TAG, "User info loaded successfully");
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Không thể tải thông tin tài khoản";
                        Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi tải thông tin (Code: " + response.code() + ")";
                    if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập lại";
                        navigateToLogin();
                    } else if (response.code() == 403) {
                        errorMsg = "Phiên đăng nhập đã hết hạn";
                        clearTokenAndNavigateToLogin();
                    }
                    Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse.UserData>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading user info", t);
            }
        });
    }

    private void displayUserInfo(LoginResponse.UserData user) {
        if (user == null) return;
        
        // Hiển thị tên
        if (etName != null) {
            etName.setText(user.getName() != null ? user.getName() : "");
        }
        
        // Hiển thị email
        if (etEmail != null) {
            etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        }
        
        // Hiển thị số điện thoại
        if (etPhone != null) {
            etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        }
        
        // Hiển thị avatar
        if (ivAvatar != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Picasso.get()
                .load(user.getAvatar())
                .placeholder(android.R.drawable.ic_menu_camera)
                .error(android.R.drawable.ic_menu_camera)
                .into(ivAvatar);
        }
    }

    private void setEditMode(boolean edit) {
        isEditMode = edit;
        
        if (etName != null) {
            etName.setEnabled(edit);
        }
        if (etEmail != null) {
            etEmail.setEnabled(false); // Email không được đổi
        }
        if (etPhone != null) {
            etPhone.setEnabled(edit);
        }
        
        if (btnSave != null) {
            btnSave.setText(edit ? "Lưu" : "Chỉnh sửa");
        }
    }

    private void saveUserInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "Không có thông tin để cập nhật", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String name = etName != null ? etName.getText().toString().trim() : "";
        String phone = etPhone != null ? etPhone.getText().toString().trim() : "";
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        // Tạo user data để update
        LoginResponse.UserData updateUser = new LoginResponse.UserData();
        updateUser.setId(currentUser.getId());
        updateUser.setEmail(currentUser.getEmail()); // Email không đổi
        updateUser.setName(name);
        updateUser.setPhone(phone);
        updateUser.setAvatar(currentUser.getAvatar()); // Avatar giữ nguyên (chưa implement upload)
        
        Call<ApiResponse<LoginResponse.UserData>> call = ApiService.getUserApiService().updateCurrentUser(updateUser);
        
        call.enqueue(new Callback<ApiResponse<LoginResponse.UserData>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse.UserData>> call, 
                                 Response<ApiResponse<LoginResponse.UserData>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse.UserData> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        currentUser = apiResponse.getData();
                        displayUserInfo(currentUser);
                        setEditMode(false);
                        Toast.makeText(AccountActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Không thể cập nhật thông tin";
                        Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi cập nhật (Code: " + response.code() + ")";
                    if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập lại";
                        navigateToLogin();
                    } else if (response.code() == 403) {
                        errorMsg = "Phiên đăng nhập đã hết hạn";
                        clearTokenAndNavigateToLogin();
                    }
                    Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse.UserData>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error updating user info", t);
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, com.example.da1.auth.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearTokenAndNavigateToLogin() {
        com.example.da1.utils.SharedPreferencesHelper.clearToken(this);
        ApiClient.resetClient();
        navigateToLogin();
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        
        com.google.android.material.textfield.TextInputEditText etOldPassword = 
            dialogView.findViewById(R.id.etOldPassword);
        com.google.android.material.textfield.TextInputEditText etNewPassword = 
            dialogView.findViewById(R.id.etNewPassword);
        com.google.android.material.textfield.TextInputEditText etConfirmPassword = 
            dialogView.findViewById(R.id.etConfirmPassword);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        
        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSave.setOnClickListener(v -> {
            String oldPassword = etOldPassword != null ? etOldPassword.getText().toString().trim() : "";
            String newPassword = etNewPassword != null ? etNewPassword.getText().toString().trim() : "";
            String confirmPassword = etConfirmPassword != null ? etConfirmPassword.getText().toString().trim() : "";
            
            // Validation
            if (oldPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu cũ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Gọi API đổi mật khẩu
            changePassword(oldPassword, newPassword, dialog);
        });
        
        dialog.show();
    }

    private void changePassword(String oldPassword, String newPassword, androidx.appcompat.app.AlertDialog dialog) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        com.example.da1.models.ChangePasswordRequest request = 
            new com.example.da1.models.ChangePasswordRequest(oldPassword, newPassword);
        
        Call<ApiResponse<Void>> call = ApiService.getAuthApiService().changePassword(request);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(AccountActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Không thể đổi mật khẩu";
                        Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi đổi mật khẩu (Code: " + response.code() + ")";
                    
                    // Log chi tiết lỗi
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    
                    if (response.code() == 400) {
                        errorMsg = "Mật khẩu cũ không đúng hoặc mật khẩu mới không hợp lệ";
                    } else if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập lại";
                        navigateToLogin();
                    } else if (response.code() == 403) {
                        errorMsg = "Phiên đăng nhập đã hết hạn";
                        clearTokenAndNavigateToLogin();
                    }
                    Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(AccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error changing password", t);
            }
        });
    }
}

