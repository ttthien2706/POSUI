package com.smb_business_chain_management;

import com.smb_business_chain_management.model.Store;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusinessChainRESTService {
    @GET("api/shop")
    Call<List<Store>> getAllShops();
}
