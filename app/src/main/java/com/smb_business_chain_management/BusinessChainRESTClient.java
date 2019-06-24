package com.smb_business_chain_management;

import android.content.Context;

import com.smb_business_chain_management.func_login.SaveSharedPreference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusinessChainRESTClient {
    public static final String  BASE_URL = "http://192.168.20.197:5000/";

    public static Retrofit getClient(Context applicationContext) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getDefaultHeader(applicationContext))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    private static OkHttpClient getDefaultHeader(Context applicationContext){
        OkHttpClient okHttpClient;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder();
        okHttpClient = httpClientBuilder.addInterceptor(interceptor)
                .addNetworkInterceptor(chain -> {
                    Request request;
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .addHeader("Authorization", "Bearer " + SaveSharedPreference.getTokenString(applicationContext));
                    request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .build();

        return okHttpClient;
    }
}
