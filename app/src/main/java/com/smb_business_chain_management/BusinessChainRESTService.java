package com.smb_business_chain_management;

import com.smb_business_chain_management.model.City;
import com.smb_business_chain_management.model.Store;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface BusinessChainRESTService {
    @GET("api/shops")
    Call<List<Store>> getAllStores();
    @POST("api/shops")
    Call<Store> createStore(@Body Store store);
    @PUT("api/shops")
    Call<Store> updateStore(@Body Store store);
    @GET("api/cities")
    Call<List<City>> getCities();
}
