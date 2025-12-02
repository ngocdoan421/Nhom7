package com.example.da1.api;

import android.content.Context;
import com.example.da1.utils.SharedPreferencesHelper;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(context);
        String token = prefs.getUserToken();
        
        if (token != null && !token.isEmpty()) {
            Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer " + token);
            Request request = requestBuilder.build();
            
            // Log để debug
            android.util.Log.d("AuthInterceptor", "Adding token to request: " + 
                original.url() + " | Token: " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
            
            Response response = chain.proceed(request);
            
            // Nếu nhận được 403, log để debug
            if (response.code() == 403) {
                android.util.Log.w("AuthInterceptor", "Received 403 Forbidden. Token may be invalid or expired.");
            }
            
            return response;
        } else {
            android.util.Log.w("AuthInterceptor", "No token found for request: " + original.url());
        }
        
        return chain.proceed(original);
    }
}


