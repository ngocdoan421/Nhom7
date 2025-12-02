package com.example.da1.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApiService {
    @GET("search")
    Call<ApiResponse<ProductListResponse>> searchProducts(
        @Query("keyword") String keyword,
        @Query("page") Integer page,
        @Query("limit") Integer limit
    );
}

