package com.example.wardrobedigital.api;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SenopatiApiService {

    @Headers("Content-Type: application/json")
    @POST("api/v1/chat")
    Call<JsonObject> sendChat(@Body JsonObject payload);
}
