package com.smb_business_chain_management.Utils;

import android.util.Log;

import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Role;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataUtils {
    private static final String TAG = DataUtils.class.getSimpleName();

    public List<City> mCityList = new ArrayList<>(0);
    public List<Role> mRoleList = new ArrayList<>(0);

    public DataUtils(){
        BusinessChainRESTService businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        Call<List<City>> call = businessChainRESTService.getCities();

        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.code() == 200) {
                    List<City> responseList = response.body();
                    mCityList.clear();
                    mCityList.addAll(responseList);
                }
            }
            @Override
            public void onFailure(Call<List<City>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });

        String[] roleNames = {"Owner", "Manager", "Cashier", "Salesman"};

        for (int i = 0; i < 4; ++i){
            mRoleList.add(new Role(i+1, roleNames[i]));
        }

    }
}
