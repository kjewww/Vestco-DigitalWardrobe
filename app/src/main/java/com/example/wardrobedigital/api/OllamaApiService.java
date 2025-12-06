package com.example.wardrobedigital.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface OllamaApiService {

    @POST("chat")
    Call<JsonObject> sendChat(@Body JsonObject request);

    @POST("generate")
    Call<JsonObject> generate(@Body JsonObject request);

    @GET("models")
    Call<JsonObject> getModels();

    @GET("health")
    Call<JsonObject> checkHealth();
}