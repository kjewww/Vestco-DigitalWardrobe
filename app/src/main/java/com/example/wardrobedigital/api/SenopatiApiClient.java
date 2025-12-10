package com.example.wardrobedigital.api;

import android.os.Build;

import com.example.wardrobedigital.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SenopatiApiClient {
    private static final String BASE_URL = BuildConfig.SENOPATI_BASE_URL_AKA;
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SenopatiApiService getService() {
        return getClient().create(SenopatiApiService.class);
    }
}
