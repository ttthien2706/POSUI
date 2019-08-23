package com.smb_business_chain_management;

import android.preference.PreferenceManager;

import com.smb_business_chain_management.models.Brand;
import com.smb_business_chain_management.models.Category;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.LoginToken;
import com.smb_business_chain_management.models.Measurement;
import com.smb_business_chain_management.models.Order;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

    @GET("Shops/GetShopById")
    Call<Store> getStoreDetails(@Query("id") int storeId);
    @GET("api/shops/{storeId}")
    Call<List<User>> getAllUsersOfStore(@Path(value = "storeId", encoded = true) int storeId);

    @POST("api/users")
    Call<User> createUser(@Body User user);
    @PUT("api/users/{userId}")
    Call<User> updateUser(@Path(value = "userId" , encoded = true) int userId, @Body User user);

    @GET("products/GetAllProductsApi")
    Call<List<Product>> GetAllProductsApi(@Query("chainid") int chainId, @Query("shopId") int shopId);

    @GET("products/GetProductsByNameApi")
    Call<List<Product>> GetProductsByNameApi(@Query("chainid") int chainId, @Query("shopId") int shopId, @Query("name") String name);
    @GET("products/GetProductByIdApi")
    Call<Product> GetProductByIdApi(@Query("chainid") int chainId, @Query("shopId") int shopId, @Query("id") int productId);
    @GET("products/GetProductsByCategoryId")
    Call<List<Product>> GetProductByCategoryId(@Query("categoryId") int categoryId, @Query("chainId") int chainId, @Query("shopId") int shopId);

    @GET("SaleReport/GetCategoryCountByShopId")
    Call<List<Category>> GetSortedCategories(@Query("shopId") int shopId);

    @POST("api/products")
    Call<Product> createProduct(@Body Product product);

    @POST("salereceipts/CreateReceipt")
    Call<Order> CreateReceipt(@Body Order order);

    @POST("connect/token")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @FormUrlEncoded
    Call<LoginToken> login(@FieldMap Map<String,String> params);
}
