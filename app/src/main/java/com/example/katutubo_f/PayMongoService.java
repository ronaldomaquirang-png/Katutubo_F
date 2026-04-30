package com.example.katutubo_f;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PayMongoService {
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json"
    })
    @POST("payment_intents")
    Call<Object> createPaymentIntent(
        @Header("Authorization") String auth,
        @Body Object body
    );
}