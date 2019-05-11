package com.smb_business_chain_management;

import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BusinessChainRESTService {
    @GET("api/shops")
    Call<List<Store>> getAllStores();
    @POST("api/shops")
    Call<Store> createStore(@Body Store store);
    @PUT("api/shops/{storeId}")
    Call<Store> updateStore(@Path(value = "storeId", encoded = true) int storeId, @Body Store store);
    @DELETE("api/shops/{storeId}")
    Call<Store> deleteStore(@Path(value = "storeId", encoded = true) int storeId);
    @GET("api/cities")
    Call<List<City>> getCities();

    @GET("api/shops/{storeId}")
    Call<List<User>> getAllUsersOfStore(@Path(value = "storeId", encoded = true) int storeId);

    @POST("api/users")
    Call<User> createUser(@Body User user);
    @PUT("api/users/{userId}")
    Call<User> updateUser(@Path(value = "userId" , encoded = true) int userId, @Body User user);
}
