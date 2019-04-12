package com.smb_business_chain_management;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreActivity extends FragmentActivity{
    private static final String TAG = StoreActivity.class.getSimpleName();

    public static final int RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING = 64;
    public static final String  BASE_URL = "http://192.168.20.100:44372/pos/";
    private static Retrofit retrofit = null;

    private RecyclerView storesRecyclerView;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    List<Store> testData = new ArrayList<Store>(1);

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
        setContentView(R.layout.activity_store);
        storesRecyclerView = (RecyclerView) findViewById(R.id.storeRV);

//        connectAndGetData();

        storesRecyclerView.setHasFixedSize(true);
        rvLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storesRecyclerView.setLayoutManager(rvLayoutManager);
        storesRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING));

        Store temp = new Store(1, "Cửa hàng Nguyễn Huệ là anh của Quang Trung, the man the myth the legend", "(454) 604-2556", "Nguyễn Huệ, Q.1", 1, true);
        Store temp2 = new Store(2, "Cửa hàng Đồng Khởi", "(454) 314-8116", "Đồng Khởi, Q.1", 4, true);
        Store temp3 = new Store(3, "Cửa hàng Hai Bà Trưng", "(454) 573-7138", "Hai Bà Trưng, Q.1", 0, false);
        Store temp4 = new Store(4, "Cửa hàng Điện Biên Phủ", "(454) 573-7138", "231, Điện Biên Phủ, phường 6 , Q.3", 0, false);

        testData.add(temp);
        testData.add(temp2);
        testData.add(temp4);
        testData.add(temp3);
        testData.add(temp2);

        FloatingActionButton createStoreFab = (FloatingActionButton) findViewById(R.id.ButtonAddStore);
        createStoreFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createButtonClicked();
            }
        });

        rvAdapter = new storesRecyclerViewAdapter(getApplicationContext(), testData);
        storesRecyclerView.setAdapter(rvAdapter);
    }

    public void createButtonClicked(){
        CreateDialogFragment createDialog = new CreateDialogFragment();
        createDialog.show(getFragmentManager(), "createDialog");
    }

    public void connectAndGetData(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        BusinessChainRESTService businessChainRESTService = retrofit.create(BusinessChainRESTService.class);
        Call<StoreResponse> call = businessChainRESTService.getAllShops();

        call.enqueue(new Callback<StoreResponse>(){
            @Override
            public void onResponse(Call<StoreResponse> call, Response<StoreResponse> response) {
                testData = response.body().getResults();
                Log.d(TAG, "Number of movies received: " + testData.size());
            }
            @Override
            public void onFailure(Call<StoreResponse> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }
}
