package com.smb_business_chain_management;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Role;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;
import com.smb_business_chain_management.views.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Users. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity implements ShopListenerInterface{
    private static final String TAG = UserListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Intent mIntent;

    private static List<Store> mStoreList;
    private static List<City> mCityList;
    private static List<Role> mRoleList;

    private RecyclerView.Adapter userRecyclerViewAdapter;

    List<User> userList = new ArrayList<>(0);

    BusinessChainRESTService businessChainRESTService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        setContentView(R.layout.activity_user_list);
        RecyclerView userRecyclerView = findViewById(R.id.user_list);
        assert userRecyclerView != null;
        mIntent = getIntent();
        Store selectedStore = mIntent.getParcelableExtra("selectedStore");
        mStoreList = mIntent.getParcelableArrayListExtra("storeList");
        mCityList = mIntent.getParcelableArrayListExtra("cityList");
        mRoleList = mIntent.getParcelableArrayListExtra("roleList");

        fetchAllUsersOfSelectedStore(selectedStore.getId());

        RecyclerView.LayoutManager userRecyclerViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userRecyclerView.setLayoutManager(userRecyclerViewLayoutManager);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.user_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        userRecyclerViewAdapter = new UserItemRecyclerViewAdapter(this, userList, mTwoPane);
        userRecyclerView.setAdapter(userRecyclerViewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void fetchAllUsersOfSelectedStore(int storeId){
        Call<List<User>> call = businessChainRESTService.getAllUsersOfStore(storeId);

        call.enqueue(new Callback<List<User>>(){
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200) {
                    List<User> responseUserList = response.body();
                    userList.clear();
                    userList.addAll(responseUserList);
                    userRecyclerViewAdapter.notifyDataSetChanged();
                }
                else Log.e(TAG, response.message());
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
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
    public Store findStoreByName(String storeName) {
        return null;
    }
    @Override
    public Store findStoreById(int id) {
        return null;
    }
    @Override
    public void addOneStaffMember(Store store) {

    }

    @Override
    public List<Store> getAllStores() {
        return mStoreList;
    }

    @Override
    public List<City> getAllCities() {
        return mCityList;
    }

    @Override
    public List<Role> getAllRoles() {
        return mRoleList;
    }

    @Override
    public void RESTEditUser(User user, int storeId, Fragment currentFragment) {
        Call<User> call = businessChainRESTService.updateUser(user.getId(), user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    fetchAllUsersOfSelectedStore(storeId);
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
        this.getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        this.getSupportFragmentManager().popBackStackImmediate();
    }
}
