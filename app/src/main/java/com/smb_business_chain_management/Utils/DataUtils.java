package com.smb_business_chain_management.Utils;

import android.util.Log;
import android.util.SparseArray;

import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.models.Brand;
import com.smb_business_chain_management.models.Category;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Measurement;
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
    public SparseArray<Category> mCategoryMap = new SparseArray<>();
    public SparseArray<Brand> mBranTheWheelyWheelyLegsNoFreely = new SparseArray<>();
    public SparseArray<Measurement> mMeasurementMap = new SparseArray<>();

    private BusinessChainRESTService businessChainRESTService;

    public DataUtils() {
        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        initAndGetCities();
        initAndGetRoles();
        initAndGetCategories();
        initAndGetBrands();
        initAndGetMeasurements();
    }

    private void initAndGetMeasurements() {
        Call<List<Measurement>> call = businessChainRESTService.getMeasurements();

        call.enqueue(new Callback<List<Measurement>>() {
            @Override
            public void onResponse(Call<List<Measurement>> call, Response<List<Measurement>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    response.body().forEach(item -> mMeasurementMap.put(item.getId(), item));
                } else {
                    Log.e(TAG, response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Measurement>> call, Throwable t) {

            }
        });
    }

    private void initAndGetCities() {
        Call<List<City>> call = businessChainRESTService.getCities();

        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.code() == 200) {
                    List<City> responseList = response.body();
                    mCityList.clear();
                    mCityList.addAll(responseList);
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    private void initAndGetRoles() {
        String[] roleNames = {"Owner", "Manager", "Cashier", "Salesman"};

        for (int i = 0; i < 4; ++i) {
            mRoleList.add(new Role(i + 1, roleNames[i]));
        }
    }

    private void initAndGetCategories(){
        Call<List<Category>> call = businessChainRESTService.getCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200){
                    assert response.body() != null;
                    response.body().forEach(item -> mCategoryMap.put(item.getId(), item));
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void initAndGetBrands(){
        Call<List<Brand>> call = businessChainRESTService.getBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    response.body().forEach(item -> mBranTheWheelyWheelyLegsNoFreely.put(item.getId(), item));
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {

            }
        });
    }
}
