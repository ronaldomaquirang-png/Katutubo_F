package com.example.katutubo_f;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TwilioService {
    @FormUrlEncoded
    @POST("2010-04-01/Accounts/{AccountSid}/Messages.json")
    Call<Object> sendSms(
        @Path("AccountSid") String accountSid,
        @Header("Authorization") String auth,
        @Field("To") String to,
        @Field("From") String from,
        @Field("Body") String body
    );
}