package com.smb_business_chain_management;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.smb_business_chain_management.Utils.DataUtils;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.views.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = ProductActivity.class.getSimpleName();
    protected BusinessChainRESTService businessChainRESTService;

    private List<Product> mProductList = new ArrayList<>(0);
    public final DataUtils mDataUtils = new DataUtils();

    private RecyclerView productsRecyclerView;
    private RecyclerView.Adapter productRecyclerViewAdapter;
    private TextView emptyView;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_products_name));
        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
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

        getAllProducts();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(productsRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        productsRecyclerView.addItemDecoration(dividerItemDecoration);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchProduct(query);
        }
    }

    private void getAllProducts(){
        Call<List<Product>> call = businessChainRESTService.getAllProducts();

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
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Log.e(TAG, t.toString());
            }
        });
    }

    private void searchProduct(String query) {
        Call<List<Product>> call = businessChainRESTService.searchProducts(query);
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
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

        super.onCreateOptionsMenu(menu);
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
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
