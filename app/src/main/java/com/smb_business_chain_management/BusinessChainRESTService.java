package com.smb_business_chain_management;

import android.preference.PreferenceManager;

import com.smb_business_chain_management.models.Brand;
import com.smb_business_chain_management.models.Category;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Measurement;
import com.smb_business_chain_management.models.Order;
import com.smb_business_chain_management.models.Product;
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
import retrofit2.http.Query;

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
    @GET("api/categories")
    Call<List<Category>> getCategories();
    @GET("api/brands")
    Call<List<Brand>> getBrands();
    @GET("api/measurements")
    Call<List<Measurement>> getMeasurements();

    @GET("api/shops/{storeId}")
    Call<List<User>> getAllUsersOfStore(@Path(value = "storeId", encoded = true) int storeId);

    @POST("api/users")
    Call<User> createUser(@Body User user);
    @PUT("api/users/{userId}")
    Call<User> updateUser(@Path(value = "userId" , encoded = true) int userId, @Body User user);

    @GET("api/products")
    Call<List<Product>> getAllProducts();

    @GET("api/products/search")
    Call<List<Product>> searchProducts(@Query("name") String name);
    @GET("api/products/searchid")
    Call<Product> getProductDetails(@Query("id") int productId);

    @POST("api/products")
    Call<Product> createProduct(@Body Product product);

    @POST("api/salereceipts")
    Call<Order> submitOrder(@Body Order order);
}
