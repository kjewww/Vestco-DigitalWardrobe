package com.example.wardrobedigital.api;
import com.example.wardrobedigital.BuildConfig;
import android.os.Build;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OllamaApiClient {
private static final String BASE_URL = BuildConfig.SENOPATI_BASE_URL;

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