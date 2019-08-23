package com.smb_business_chain_management.func_products;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.DataUtils;
import com.smb_business_chain_management.Utils.ScreenManager;
import com.smb_business_chain_management.base.BaseCustomerScreen;
import com.smb_business_chain_management.func_login.SaveSharedPreference;
import com.smb_business_chain_management.func_main.MainActivity;
import com.smb_business_chain_management.func_selling.SellingActivity;
import com.smb_business_chain_management.func_settings.listener_interface.ShopListenerInterface;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.Role;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;
import com.smb_business_chain_management.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ShopListenerInterface {

    private static final String TAG = ProductActivity.class.getSimpleName();
    protected BusinessChainRESTService businessChainRESTService;

    private List<Product> mProductList = new ArrayList<>(0);
    public static DataUtils mDataUtils;

    private RecyclerView productsRecyclerView;
    private RecyclerView.Adapter productRecyclerViewAdapter;
    private TextView emptyView;
//    private FloatingActionButton createButton;

    private DialogFragment createDialog;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;

    private BaseCustomerScreen customerScreen;
    private Display[] displays;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataUtils = new DataUtils(getApplicationContext());
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_products_name));
        businessChainRESTService = BusinessChainRESTClient.getClient(getApplicationContext()).create(BusinessChainRESTService.class);
        setContentView(R.layout.activity_product);
        invalidateOptionsMenu();
        handleIntent(getIntent());

        productsRecyclerView = findViewById(R.id.productRV);
        emptyView = findViewById(R.id.emptyList);
        if (mProductList.isEmpty()) {
            productsRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            productsRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        productsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        productsRecyclerView.setLayoutManager(rvLayoutManager);

        productRecyclerViewAdapter = new ProductRecyclerViewAdapter(this, mProductList, getFragmentManager());
        productsRecyclerView.setAdapter(productRecyclerViewAdapter);

//        createButton = findViewById(R.id.createButton);
//        createButton.setOnClickListener(createButtonClicked);

        getAllProducts();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(productsRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        productsRecyclerView.addItemDecoration(dividerItemDecoration);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.getMenu().getItem(BaseActivity.NAV_MENU_PRODUCT_ID).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        setupDisplays();
    }
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            try {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.activity_products_name));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_product_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Assumes current activity is the searchable activity
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

//        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
//            super.setSearchActionBar();
            return true;
        }
        else if (id == R.id.action_get_all){
            getAllProducts();
        }
        else if (id == android.R.id.home) {
            if (createDialog != null && createDialog.getView() != null) {
                dismissDialogAndGoUp();
                return true;
            }
        }
        else if (id == R.id.confirm){
            if (createDialog != null){
                dismissDialogAndGoUp();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.navSelling) {
            finish();
            intent = new Intent(ProductActivity.this, SellingActivity.class);
            intent.putExtra("isParentRoot", isTaskRoot());
            startActivityForResult(intent, MainActivity.SAVE_ORDER_CODE);
        } else if (id == R.id.navMain) {
            finish();
//            intent = new Intent(ProductActivity.this, MainActivity.class);
//            startActivity(intent);
        } else if (id == R.id.navReturn) {
            finish();
        } else if (id == R.id.navProduct) {
        } /*else if (id == R.id.navSettings) {
            finish();
            intent = new Intent(ProductActivity.this, SettingsActivity.class);
            startActivity(intent);
        }*/
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (customerScreen != null) {
            customerScreen.show();
        }
    }

    private void setupDisplays() {
        ScreenManager screenManager = ScreenManager.getInstance();
        screenManager.init(this);
        displays = screenManager.getDisplays();
        customerScreen = new BaseCustomerScreen(this, displays[1]); // small screen
    }
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchProduct(query);
        }
    }
    private void getAllProducts(){
        Call<List<Product>> call = businessChainRESTService.GetAllProductsApi(Integer.parseInt(SaveSharedPreference.getChainId(getApplicationContext())), Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())));

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200){
                    mProductList.clear();
                    mProductList.addAll(response.body());
                    if (!mProductList.isEmpty()) {
                        productsRecyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                    productRecyclerViewAdapter.notifyDataSetChanged();
                }
                else{
                    Log.e(TAG, Integer.toString(response.code()));
                    Log.e(TAG, response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                try {
                    Log.e(TAG, throwable.getMessage());
                    Log.e(TAG, throwable.toString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                throwable.printStackTrace();
                Snackbar.make(productsRecyclerView, R.string.REST_fail, Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void searchProduct(String query) {
        Call<List<Product>> call = businessChainRESTService.GetProductsByNameApi(Integer.parseInt(SaveSharedPreference.getChainId(getApplicationContext())), Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())), query);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200){
                    Log.d(TAG, response.body().toString());
                    mProductList.clear();
                    mProductList.addAll(response.body());
                    if (!mProductList.isEmpty()) {
                        productsRecyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                    else{
                        productsRecyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    productRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Snackbar.make(productsRecyclerView, R.string.REST_fail, Snackbar.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }
//    View.OnClickListener createButtonClicked = view -> {
//        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.addStoreMenu));
//        FragmentManager fragmentManager = getFragmentManager();
//        createDialog = new CreateProductDialogFragment();
//
//        Bundle fragmentArgument = new Bundle();
//
//        fragmentArgument.putSparseParcelableArray(ProductDetailFragment.ARG_CATEGORY, mDataUtils.mCategoryMap);
//        fragmentArgument.putSparseParcelableArray(ProductDetailFragment.ARG_BRAND, mDataUtils.mBranTheWheelyWheelyLegsNoFreely);
//        fragmentArgument.putSparseParcelableArray(ProductDetailFragment.ARG_MEASUREMENT, mDataUtils.mMeasurementMap);
//
//        createDialog.setArguments(fragmentArgument);
//
//        // The device is smaller, so show the fragment fullscreen
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        // For a little polish, specify a transition animation
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        // To make it fullscreen, use the 'content' root view as the container
//        // for the fragment, which is always the root view for the activity
//        transaction.replace(android.R.id.content, createDialog)
//                .addToBackStack(null).commit();
//        invalidateOptionsMenu();
//        if (getSupportActionBar() != null) {
//            toggle.onDrawerOpened(mDrawerLayout);
//        }
//
//    };
    protected void dismissDialogAndGoUp(){
        dialogDismissAnimate(createDialog.getView());
        createDialog.dismiss();
        createDialog = null;
//        FloatingActionMenu fabMenu = findViewById(R.id.AddButton);
//        fabMenu.close(true);
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.activity_products_name));
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

    @Override
    public void RESTAddNewStore(Store store) {

    }
    @Override
    public void RESTEditStore(int id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, int cityId, int districtId, int wardId) {

    }
    @Override
    public void RESTDeleteStore(int id, int position) {

    }
    @Override
    public void RESTAddNewUser(User user) {

    }
    @Override
    public void RESTEditUser(User user, int storeId, Fragment currentFragment) {

    }
    @Override
    public void RESTAddNewProduct(Product product) {
        Call<Product> call = businessChainRESTService.createProduct(product);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200){
                    Product newProduct = response.body();
                    addProduct(response.body());
                }
                else {
                    Log.e(TAG, String.valueOf(response.code()));
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                Snackbar.make(productsRecyclerView, R.string.REST_fail, Snackbar.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }
    private void addProduct(Product product) {
        mProductList.add(product);
        productRecyclerViewAdapter.notifyDataSetChanged();
    }
    @Override
    public List<Store> getAllStores() {
        return null;
    }
    @Override
    public List<City> getAllCities() {
        return null;
    }
    @Override
    public List<Role> getAllRoles() {
        return null;
    }
    @Override
    public Store findStoreByName(String storeName) {
        return null;
    }
    @Override
    public Store findStoreById(int id) {
        return null;
    }
    @Override
    public Store getSelectedStore() {
        return null;
    }
    @Override
    public void addOneStaffMember(Store store) {

    }
}
