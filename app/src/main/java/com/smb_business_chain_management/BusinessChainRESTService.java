package com.smb_business_chain_management;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusinessChainRESTService {
    @GET("settings/cities")
    Call<StoreResponse> getAllShops();
}
