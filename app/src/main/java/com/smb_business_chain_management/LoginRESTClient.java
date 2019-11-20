package com.smb_business_chain_management;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRESTClient {
    public static final String  BASE_URL = "http://192.168.43.80:5000/";

    public static Retrofit getClient(Context applicationContext) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}