package com.smb_business_chain_management.func_settings;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.model.LatLng;
import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.DataUtils;
import com.smb_business_chain_management.func_main.MainActivity;
import com.smb_business_chain_management.func_products.ProductActivity;
import com.smb_business_chain_management.func_selling.SellingActivity;
import com.smb_business_chain_management.func_settings.fragments.CreateStoreDialogFragment;
import com.smb_business_chain_management.func_settings.fragments.CreateUserDialogFragment;
import com.smb_business_chain_management.func_settings.listener_interface.ShopListenerInterface;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.Role;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;
import com.smb_business_chain_management.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ShopListenerInterface {
    public static final int RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING = 64;
    protected static final AppUtils utils = new AppUtils();
    protected static DataUtils dataUtils;
    private static final String TAG = SettingsActivity.class.getSimpleName();
    public List<Store> storeList = new ArrayList<>(0);
    BusinessChainRESTService businessChainRESTService;
    private RecyclerView storesRecyclerView;
    private RecyclerView.Adapter rvAdapter;
    private DialogFragment createDialog;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataUtils = new DataUtils(getApplicationContext());
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_settings_name));
        invalidateOptionsMenu();
        setContentView(R.layout.activity_settings);
        businessChainRESTService = BusinessChainRESTClient.getClient(getApplicationContext()).create(BusinessChainRESTService.class);

        viewLookup();
        setupViews();
        fetchAllStores();
    }

    private void setupViews() {
        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        storesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storesRecyclerView.setLayoutManager(rvLayoutManager);
        storesRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(RECYCLER_VIEW_ITEM_HORIZONTAL_PADDING));

        FloatingActionButton createStoreFab = findViewById(R.id.ButtonAddStore);
        FloatingActionButton createUserFab = findViewById(R.id.ButtonAddEmployee);
        createStoreFab.setOnClickListener(view -> createShopButtonClicked());
        createUserFab.setOnClickListener(view -> createUserButtonClicked());

        rvAdapter = new StoresRecyclerViewAdapter(getApplicationContext(), storeList, dataUtils.mCityList, dataUtils.mRoleList, getFragmentManager());
        storesRecyclerView.setAdapter(rvAdapter);


        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.getMenu().getItem(BaseActivity.NAV_MENU_SETTINGS_ID).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void viewLookup() {
        storesRecyclerView = findViewById(R.id.storeRV);
        mDrawerLayout = findViewById(R.id.drawerLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (createDialog != null && createDialog.getView() != null) {
                    dismissDialogAndGoUp();
                    return true;
                }
            case R.id.confirm:
                if (createDialog != null){
                    dismissDialogAndGoUp();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (createDialog != null && createDialog.getView() != null) {
            dismissDialogAndGoUp();
        }
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    protected void dismissDialogAndGoUp(){
        dialogDismissAnimate(createDialog.getView());
        createDialog.dismiss();
        createDialog = null;
        FloatingActionMenu fabMenu = findViewById(R.id.AddButton);
        fabMenu.close(true);
        try {
//            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_menu_24px);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.activity_settings_name));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        toggle.onDrawerClosed(mDrawerLayout);
    }
    public void dialogDismissAnimate(View view) {
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(200);
        view.setAnimation(fadeOut);
        view.startAnimation(fadeOut);
    }
    public void addAStore(Store newStore) {
        storeList.add(newStore);
        rvAdapter.notifyDataSetChanged();
    }
    public void editAStore(Store modifiedStore) {
        Store store = findStoreById(modifiedStore.getId());
        storeList.stream().filter(store::equals).findFirst().ifPresent(storeToUpdate -> {
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
        storeList.remove(position);
        storesRecyclerView.removeViewAt(position);
        rvAdapter.notifyItemRemoved(position);
        rvAdapter.notifyItemRangeChanged(position, storeList.size());
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
//            ActionBar actionBar = getSupportActionBar();
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            toggle.onDrawerOpened(mDrawerLayout);
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
//            ActionBar actionBar = getSupportActionBar();
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            toggle.onDrawerOpened(mDrawerLayout);
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
                throwable.printStackTrace();
            }
        });
    }
    @Override
    public void RESTEditStore(int id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, int cityId, int districtId, int wardId) {
        Store modifiedStore = new Store(id, name, phoneNumber, address, staffNumber, isActive, cityId, districtId, wardId);
        String fullAddress = modifiedStore.getAddress() + dataUtils.getAddressString(cityId, districtId, wardId);
        LatLng storeLatLng = utils.getLocationFromAddress(getApplicationContext(), fullAddress);
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
//                Log.d(TAG, "Number of stores received: " + storeList.size());
            }

            @Override
            public void onFailure(Call<Store> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                throwable.printStackTrace();
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
                throwable.printStackTrace();
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
                throwable.printStackTrace();
            }
        });
    }
    @Override
    public void RESTEditUser(User user, int storeId, Fragment currentFragment) {

    }
    @Override
    public List<Store> getAllStores() {
        return storeList;
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
        storeList.stream().filter(store::equals).findFirst().ifPresent(storeToUpdate -> {
            storeToUpdate.setAmountUser(storeToUpdate.getAmountUser() + 1);
        });
        rvAdapter.notifyDataSetChanged();
    }
    @Override
    public Store findStoreById(int id) {
        return storeList.stream().filter(store -> id == store.getId()).findFirst().orElse(null);
    }
    @Override
    public Store findStoreByName(final String storeName) {
        return storeList.stream().filter(store -> storeName.equals(store.getName())).findFirst().orElse(null);
    }
    @Override
    public void RESTAddNewProduct(Product product) {

    }
    public void fetchAllStores() {
        Call<List<Store>> call = businessChainRESTService.getAllStores();

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                if (response.code() == 200) {
                    List<Store> storeList = response.body();
                    SettingsActivity.this.storeList.clear();
                    SettingsActivity.this.storeList.addAll(storeList);
                    rvAdapter.notifyDataSetChanged();
                } else Log.e(TAG, response.message());
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                throwable.printStackTrace();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.navSelling) {
            finish();
            intent = new Intent(SettingsActivity.this, SellingActivity.class);
            intent.putExtra("isParentRoot", isTaskRoot());
            startActivityForResult(intent, MainActivity.SAVE_ORDER_CODE);
        } else if (id == R.id.navReturn) {
            finish();
        } else if (id == R.id.navProduct) {
            finish();
            intent = new Intent(SettingsActivity.this, ProductActivity.class);
            startActivity(intent);
        }/* else if (id == R.id.navSettings) {
        }*/
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
