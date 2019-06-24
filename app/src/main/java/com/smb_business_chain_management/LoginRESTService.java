package com.smb_business_chain_management;

import com.smb_business_chain_management.models.LoginToken;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginRESTService{
    @POST("connect/token")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @FormUrlEncoded
    Call<LoginToken> login(@FieldMap Map<String,String> params);
}
