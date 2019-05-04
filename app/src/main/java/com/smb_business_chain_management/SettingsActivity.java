package com.smb_business_chain_management;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.model.LatLng;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.model.Store;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends FragmentActivity implements ShopListenerInterface {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    protected  static final AppUtils utils = new AppUtils();

    public static final int RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING = 64;
    BusinessChainRESTService businessChainRESTService;

    private RecyclerView storesRecyclerView;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    public List<Store> testData = new ArrayList<>(0);

    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration{
        private final int horizontalWidthSpace;

        public HorizontalSpaceItemDecoration(int verticalHeightSpace){
            this.horizontalWidthSpace = verticalHeightSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
            outRect.right = horizontalWidthSpace/2;
            outRect.left = horizontalWidthSpace/2;
            outRect.bottom = horizontalWidthSpace/2;
            outRect.top = horizontalWidthSpace/2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storesRecyclerView = findViewById(R.id.storeRV);

        fetchAllStores();

        storesRecyclerView.setHasFixedSize(true);
        rvLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storesRecyclerView.setLayoutManager(rvLayoutManager);
        storesRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING));

        FloatingActionButton createStoreFab = findViewById(R.id.ButtonAddStore);
        FloatingActionButton createUserFab = findViewById(R.id.ButtonAddEmployee);
        createStoreFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createShopButtonClicked();
            }
        });
        createUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserButtonClicked();
            }
        });

        rvAdapter = new StoresRecyclerViewAdapter(getApplicationContext(), testData, getFragmentManager());
        storesRecyclerView.setAdapter(rvAdapter);
    }

    public void addAStore(Store newStore){
        testData.add(newStore);
        rvAdapter.notifyDataSetChanged();
    }
    public void editAStore(Store modifiedStore){
        Store store = findStoreById(modifiedStore.getId());
        testData.stream().filter(store::equals).findFirst().ifPresent(storeToUpdate -> {
            storeToUpdate.setName(modifiedStore.getName());
            storeToUpdate.setPhone(modifiedStore.getPhone());
            storeToUpdate.setAddress(modifiedStore.getAddress());
            storeToUpdate.setFullAddress(modifiedStore.getFullAddress());
            storeToUpdate.setIsActive(modifiedStore.isActive());
            storeToUpdate.setCityId(modifiedStore.getCityId());
            storeToUpdate.setDistrictId(modifiedStore.getDistrictId());
            storeToUpdate.setWardId(modifiedStore.getWardId());
        });
        rvAdapter.notifyDataSetChanged();
    }
    private void deleteAStore(int position) {
        testData.remove(position);
        storesRecyclerView.removeViewAt(position);
        rvAdapter.notifyItemRemoved(position);
        rvAdapter.notifyItemRangeChanged(position, testData.size());
    }

    public void createShopButtonClicked(){
        CreateStoreDialogFragment createDialog = new CreateStoreDialogFragment();
        createDialog.show(getFragmentManager(), "createDialog");
    }
    public void createUserButtonClicked(){
        CreateUserDialogFragment createDialog = new CreateUserDialogFragment();
        createDialog.show(getFragmentManager(), "createDialog");
    }

    @Override
    public void RESTAddNewStore(String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId) {
        Store newStore = new Store(name, phoneNumber, address, staffNumber, isActive, cityId, districtId, wardId);

        LatLng storeLatLng  = utils.getLocationFromAddress(getApplicationContext(), newStore.getAddress());
        newStore.setLatitude(storeLatLng.latitude);
        newStore.setLongitude(storeLatLng.longitude);

        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        Call<Store> call = businessChainRESTService.createStore(newStore);

        call.enqueue(new Callback<Store>(){
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.code() == 200) {
                    Store retStore = response.body();
                    addAStore(retStore);
                } else {
                    Log.d(TAG, Integer.toString(response.code()));
                    Log.d(TAG, response.message());
                }
            }
            @Override
            public void onFailure(Call<Store> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public void RESTEditStore(int id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId) {
        Store modifiedStore = new Store(id, name, phoneNumber, address, staffNumber, isActive, cityId, districtId, wardId);

        LatLng storeLatLng  = utils.getLocationFromAddress(getApplicationContext(), modifiedStore.getAddress());
        modifiedStore.setLatitude(storeLatLng.latitude);
        modifiedStore.setLongitude(storeLatLng.longitude);

        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);

        Call<Store> call = businessChainRESTService.updateStore(id, modifiedStore);

        call.enqueue(new Callback<Store>(){
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.code() == 200) {
                    Store modifiedStore = response.body();
                    editAStore(modifiedStore);
                } else {
                    Log.d(TAG, Integer.toString(response.code()));
                    Log.d(TAG, response.message());
                }
//                Log.d(TAG, "Number of stores received: " + testData.size());
            }
            @Override
            public void onFailure(Call<Store> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public void RESTDeleteStore(int id, int position) {
        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);

        Call<Store> call = businessChainRESTService.deleteStore(id);

        call.enqueue(new Callback<Store>(){
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.code() == 200) {
                    deleteAStore(position);
                } else {
                    Log.d(TAG, Integer.toString(response.code()));
                    Log.d(TAG, response.message());
                }
            }
            @Override
            public void onFailure(Call<Store> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public List<Store> getAllStores() {
        return testData;
    }

    @Override
    public void addOneStaffMember(Store store) {
        testData.stream().filter(store::equals).findFirst().ifPresent(storeToUpdate -> storeToUpdate.setAmountUser(storeToUpdate.getAmountUser() + 1));
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public Store findStoreById(int id) {
        return testData.stream().filter(store -> id == store.getId()).findFirst().orElse(null);
    }

    @Override
    public Store findStoreByName(final String storeName) {
        return testData.stream().filter(store -> storeName.equals(store.getName())).findFirst().orElse(null);
    }

    public void fetchAllStores(){
        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        Call<List<Store>> call = businessChainRESTService.getAllStores();

        call.enqueue(new Callback<List<Store>>(){
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                if (response.code() == 200) {
                    List<Store> storeList = response.body();
                    testData.clear();
                    testData.addAll(storeList);
                    rvAdapter.notifyDataSetChanged();
                }
                else Log.e(TAG, response.message());
            }
            @Override
            public void onFailure(Call<List<Store>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }
}
