package com.example.wardrobedigital.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OllamaApiClient {
    private static final String BASE_URL = "https://senopati.its.ac.id/senopati-lokal-dev/";
//    private static final String BASE_URL = "https:/senopati-api.vercel.app/";


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

    public static OllamaApiService getService() {
        return getClient().create(OllamaApiService.class);
    }
}