package com.smb_business_chain_management.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;
import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.District;
import com.smb_business_chain_management.models.Role;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();

    public void fetchAllAdministrativeUnits(List<City> allCities, ArrayAdapter<City> cityAdapter){
        BusinessChainRESTService businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        Call<List<City>> call = businessChainRESTService.getCities();

        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.code() == 200) {
                    List<City> responseList = response.body();
                    allCities.clear();
                    allCities.addAll(responseList);
                    cityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String sAddress){
        Geocoder coder = new Geocoder(context);
        List<Address> addresses;
        LatLng returnLatLng = null;
        if (sAddress.length() < 1){
            return null;
        }
        else{
            try{
                addresses = coder.getFromLocationName(sAddress, 5);
                if (addresses == null || addresses.size() == 0){
                    return null;
                }

                Address location = addresses.get(0);
                returnLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            }
            catch (IOException io){
                io.printStackTrace();
            }
        }
        return returnLatLng;
    }
}
