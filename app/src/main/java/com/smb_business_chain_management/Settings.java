package com.smb_business_chain_management;

import android.app.AlertDialog;
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
import com.smb_business_chain_management.model.City;
import com.smb_business_chain_management.model.Store;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends FragmentActivity implements CreateStoreDialogFragment.CreateShopDialogListener, CreateUserDialogFragment.CreateUserDialogListener, EditStoreDialogFragment.EditStoreDialogListener {
    private static final String TAG = Settings.class.getSimpleName();
    protected  static final AppUtils utils = new AppUtils();

    public static final int RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING = 64;
    public static final String  BASE_URL = "https://192.168.20.197:5001";
    BusinessChainRESTService businessChainRESTService;

    private RecyclerView storesRecyclerView;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    public List<Store> testData = new ArrayList<Store>(0);
    public Store newStore = null;

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
        storesRecyclerView = (RecyclerView) findViewById(R.id.storeRV);

        fetchAllStores();
//        fetchAllAdministrativeUnits();

        storesRecyclerView.setHasFixedSize(true);
        rvLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storesRecyclerView.setLayoutManager(rvLayoutManager);
        storesRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING));

        Store temp = new Store("Cửa hàng Nguyễn Huệ là anh của Quang Trung, the man the myth the legend", "(454) 604-2556", "Nguyễn Huệ, p.2, Q.1", 1, true, Long.valueOf(0), Long.valueOf(0) ,Long.valueOf(0));
//        Store temp2 = new Store("Cửa hàng Đồng Khởi", "(454) 314-8116", "Đồng Khởi, p.1, Q.1", 4, true, wardId);
//        Store temp3 = new Store("Cửa hàng Hai Bà Trưng", "(454) 573-7138", "Hai Bà Trưng, p.1, Q.1", 0, false, wardId);
//        Store temp4 = new Store("Cửa hàng Điện Biên Phủ", "(454) 573-7138", "231, Điện Biên Phủ, phường 6 , Q.3", 0, false, wardId);
//
        testData.add(temp);
//        testData.add(temp2);
//        testData.add(temp4);
//        testData.add(temp3);
//        testData.add(temp2);

        FloatingActionButton createStoreFab = (FloatingActionButton) findViewById(R.id.ButtonAddStore);
        FloatingActionButton createUserFab = (FloatingActionButton) findViewById(R.id.ButtonAddEmployee);
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

    public void createShopButtonClicked(){
        CreateStoreDialogFragment createDialog = new CreateStoreDialogFragment();
        createDialog.show(getFragmentManager(), "createDialog");
    }
    public void createUserButtonClicked(){
        CreateUserDialogFragment createDialog = new CreateUserDialogFragment();
        createDialog.show(getFragmentManager(), "createDialog");
    }

    @Override
    public void addNewStore(String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId) {
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
                    addAStore(newStore);
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
    public void editShop(String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId) {

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
                    Log.d(TAG, response.body().toString());
                    List<Store> storeList = response.body();
                    testData.clear();
                    testData.addAll(storeList);
                    rvAdapter.notifyDataSetChanged();
//                Log.d(TAG, "Number of stores received: " + testData.size());
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
