package com.smb_business_chain_management;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.model.LatLng;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.DataUtils;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Role;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;
import com.smb_business_chain_management.views.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseActivity implements ShopListenerInterface {
    public static final int RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING = 64;
    protected static final AppUtils utils = new AppUtils();
    protected static final DataUtils dataUtils = new DataUtils();
    private static final String TAG = SettingsActivity.class.getSimpleName();
    public List<Store> testData = new ArrayList<>(0);
    BusinessChainRESTService businessChainRESTService;
    private RecyclerView storesRecyclerView;
    private RecyclerView.Adapter rvAdapter;
    private DialogFragment createDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_settings_name));
        setContentView(R.layout.activity_settings);
        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);

        storesRecyclerView = findViewById(R.id.storeRV);

        fetchAllStores();

        storesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storesRecyclerView.setLayoutManager(rvLayoutManager);
        storesRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING));

        FloatingActionButton createStoreFab = findViewById(R.id.ButtonAddStore);
        FloatingActionButton createUserFab = findViewById(R.id.ButtonAddEmployee);
        createStoreFab.setOnClickListener(view -> createShopButtonClicked());
        createUserFab.setOnClickListener(view -> createUserButtonClicked());

        rvAdapter = new StoresRecyclerViewAdapter(getApplicationContext(), testData, dataUtils.mCityList, dataUtils.mRoleList, getFragmentManager());
        storesRecyclerView.setAdapter(rvAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        handleActionBarBehaviour(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (createDialog != null && createDialog.getView() != null) {
            dismissDialogAndGoUp();
        }
        super.onBackPressed();
    }

    protected void dismissDialogAndGoUp(){
        dialogDismissAnimate(createDialog.getView());
        createDialog.dismiss();
        FloatingActionMenu fabMenu = findViewById(R.id.AddButton);
        fabMenu.close(true);
        if(isTaskRootOrIsParentTaskRoot()) Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_menu_24px);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.activity_settings_name));
    }

    protected void handleActionBarBehaviour(int id) {
        switch (id) {
            case android.R.id.home:
                if (createDialog != null && createDialog.getView() != null) {
                    dismissDialogAndGoUp();
                }
                break;
            case R.id.confirm:
                if (createDialog != null){
                    dismissDialogAndGoUp();
                }
        }
    }

    protected boolean isTaskRootOrIsParentTaskRoot() {
        if (getParent() == null) return isTaskRoot();
        else return getParent().isTaskRoot();
    }

    public void dialogDismissAnimate(View view) {
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(200);
        view.setAnimation(fadeOut);
        view.startAnimation(fadeOut);
    }

    public void addAStore(Store newStore) {
        testData.add(newStore);
        rvAdapter.notifyDataSetChanged();
    }

    public void editAStore(Store modifiedStore) {
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

    public void createShopButtonClicked() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.addStoreMenu));
        FragmentManager fragmentManager = getFragmentManager();
        createDialog = new CreateStoreDialogFragment();

        Bundle fragmentArgument = new Bundle();
        fragmentArgument.putParcelableArrayList("cities", (ArrayList<? extends Parcelable >)dataUtils.mCityList);

        createDialog.setArguments(fragmentArgument);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, createDialog)
                .addToBackStack(null).commit();


        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24px);
        } else {
        }
    }

    public void createUserButtonClicked() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.addEmployeeMenu));
        FragmentManager fragmentManager = getFragmentManager();
        createDialog = new CreateUserDialogFragment();

        Bundle fragmentArgument = new Bundle();
        fragmentArgument.putParcelableArrayList("cities", (ArrayList<? extends Parcelable >)dataUtils.mCityList);
        fragmentArgument.putParcelableArrayList("roles", (ArrayList<? extends Parcelable >)dataUtils.mRoleList);

        createDialog.setArguments(fragmentArgument);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, createDialog)
                .addToBackStack(null).commit();
        invalidateOptionsMenu();

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24px);
        } else {
        }
    }

    @Override
    public void RESTAddNewStore(Store newStore) {
        LatLng storeLatLng = utils.getLocationFromAddress(getApplicationContext(), newStore.getFullAddress());
        newStore.setLatitude(storeLatLng.latitude);
        newStore.setLongitude(storeLatLng.longitude);

        Call<Store> call = businessChainRESTService.createStore(newStore);

        call.enqueue(new Callback<Store>() {
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
    public void RESTEditStore(int id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, int cityId, int districtId, int wardId) {
        Store modifiedStore = new Store(id, name, phoneNumber, address, staffNumber, isActive, cityId, districtId, wardId);

        LatLng storeLatLng = utils.getLocationFromAddress(getApplicationContext(), modifiedStore.getFullAddress());
        modifiedStore.setLatitude(storeLatLng.latitude);
        modifiedStore.setLongitude(storeLatLng.longitude);


        Call<Store> call = businessChainRESTService.updateStore(id, modifiedStore);

        call.enqueue(new Callback<Store>() {
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
        Call<Store> call = businessChainRESTService.deleteStore(id);

        call.enqueue(new Callback<Store>() {
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
    public void RESTAddNewUser(User user) {
        Call<User> call = businessChainRESTService.createUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    addOneStaffMember(findStoreById(user.getShopId()));
                } else {
                    Log.d(TAG, Integer.toString(response.code()));
                    Log.d(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public void RESTEditUser(User user, int storeId, Fragment currentFragment) {

    }

    @Override
    public List<Store> getAllStores() {
        return testData;
    }

    @Override
    public List<City> getAllCities() {
        return dataUtils.mCityList;
    }

    @Override
    public List<Role> getAllRoles() {
        return dataUtils.mRoleList;
    }

    @Override
    public Store getSelectedStore() {
        return null;
    }

    @Override
    public void addOneStaffMember(Store store) {
        testData.stream().filter(store::equals).findFirst().ifPresent(storeToUpdate -> {
            storeToUpdate.setAmountUser(storeToUpdate.getAmountUser() + 1);
        });
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

    public void fetchAllStores() {
        Call<List<Store>> call = businessChainRESTService.getAllStores();

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                if (response.code() == 200) {
                    List<Store> storeList = response.body();
                    testData.clear();
                    testData.addAll(storeList);
                    rvAdapter.notifyDataSetChanged();
                } else Log.e(TAG, response.message());
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int horizontalWidthSpace;

        public HorizontalSpaceItemDecoration(int verticalHeightSpace) {
            this.horizontalWidthSpace = verticalHeightSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = horizontalWidthSpace / 2;
            outRect.left = horizontalWidthSpace / 2;
            outRect.bottom = horizontalWidthSpace / 2;
            outRect.top = horizontalWidthSpace / 2;
        }
    }
}
