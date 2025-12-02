package com.example.da1.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.auth.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthHelper {
    
    /**
     * Đăng xuất user
     * @param context Context của activity
     * @param onLogoutSuccess Callback khi đăng xuất thành công
     */
    public static void logout(Context context, Runnable onLogoutSuccess) {
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(context);
        String token = prefs.getUserToken();
        
        if (token == null || token.isEmpty()) {
            // Không có token, chỉ xóa local data
            performLocalLogout(context, prefs);
            if (onLogoutSuccess != null) {
                onLogoutSuccess.run();
            }
            return;
        }
        
        // Gọi API logout
        Call<ApiResponse<Void>> call = ApiService.getAuthApiService().logout("Bearer " + token);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Dù API thành công hay thất bại, vẫn xóa local data
                performLocalLogout(context, prefs);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                }
                
                if (onLogoutSuccess != null) {
                    onLogoutSuccess.run();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Dù API lỗi, vẫn xóa local data
                performLocalLogout(context, prefs);
                Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                
                if (onLogoutSuccess != null) {
                    onLogoutSuccess.run();
                }
            }
        });
    }
    
    /**
     * Xóa dữ liệu local và chuyển về màn hình đăng nhập
     */
    private static void performLocalLogout(Context context, SharedPreferencesHelper prefs) {
        // Xóa tất cả dữ liệu đã lưu
        prefs.clearAll();
        
        // Reset ApiClient để xóa token
        ApiClient.resetClient();
    }
    
    /**
     * Đăng xuất và chuyển về màn hình đăng nhập
     */
    public static void logoutAndNavigateToLogin(Context context) {
        logout(context, () -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }
}

