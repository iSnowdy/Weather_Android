package com.example.weebther.Database.Repository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String baseURL) {
        // Only build the retrofit instance if it's null or the base URL given has not yet been
        // used or implemented
        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseURL)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
