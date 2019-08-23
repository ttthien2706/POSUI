package com.smb_business_chain_management.func_selling;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.ScreenManager;
import com.smb_business_chain_management.base.BaseActivity;
import com.smb_business_chain_management.func_login.LoginActivity;
import com.smb_business_chain_management.func_login.SaveSharedPreference;
import com.smb_business_chain_management.func_main.MainActivity;
import com.smb_business_chain_management.func_products.ProductActivity;
import com.smb_business_chain_management.models.Category;
import com.smb_business_chain_management.models.Order;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.Store;

import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.utils.DataForSendToPrinterPos80;

import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellingActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewClickListener, OrderListenerInterface {
    private static final String TAG = SellingActivity.class.getSimpleName();
    private static final AppUtils appUtil;
    private CustomerScreen customerScreen;
    private Display[] displays;
    public static boolean IS_PRINTER_CONNECTED = false;
    private static ProgressDialog dataLoadingDialog;
    private static AlertDialog doneDialog;

    static {
        appUtil = new AppUtils();
    }

//    public OrderTotalListener IOrderTotalListener;
    String BARCODE = "";
    private SearchView orderProductSearchView;
    private RecyclerView orderProductRecyclerView;
    private RecyclerView.Adapter orderProductRecyclerViewAdapter;
    private TextInputEditText quantityTextInput;
    RecyclerView.OnTouchListener onTouchListener = ((View view, MotionEvent motionEvent) -> {
        view.performClick();
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
////                hideButton(addToOrderButton);
//                view.performClick();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                hideButton(addToOrderButton);
//                break;
//            case MotionEvent.ACTION_UP:
//                showButton(addToOrderButton);
//                break;
//        }
//        return super.onTouchEvent(motionEvent);

        hideKeypad();
        orderProductSearchView.clearFocus();
        quantityTextInput.clearFocus();
        return false;
    });
    //    private Button addToOrderButton;
    private RecyclerView orderListRecyclerView;
    private RecyclerView.Adapter orderListRecyclerViewAdapter;
    private RecyclerView categoryListRecyclerView;
    private RecyclerView.Adapter categoryListRecyclerViewAdapter;
    private Button cancelButton;
    private Button saveButton;
    private Button paymentButton;
    private TextView orderTitle;
    private TextView orderTotalTextView;
//    private TabLayout tabLayout;
//    private ViewPager viewPager;
//    private PaymentPagerAdapter viewPagerAdapter;
    private Store mStore;
    private List<Product> mProductList = new ArrayList<>();
    private List<Product> mDisplayProductList = new ArrayList<>();
    private List<Product> mOrderList = new ArrayList<>();
    private List<Category> mCategoryList = new ArrayList<>();
    private Order mCurrentOrder = new Order();
    private Product mSelectedProduct = null;
//    private View selectedCard = null;
    private int selectedPos = -1;
    private BusinessChainRESTService businessChainRESTService;
    private SearchView.OnCloseListener searchViewCloseListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
//            int range = mDisplayProductList.size();
//            mDisplayProductList.clear();
//            orderProductRecyclerViewAdapter.notifyItemRangeRemoved(0, range);
//            mDisplayProductList.addAll(mProductList);
//            orderProductRecyclerViewAdapter.notifyItemRangeChanged(0, mDisplayProductList.size());
//            orderProductRecyclerViewAdapter.notifyDataSetChanged();
            orderProductSearchView.clearFocus();
            hideKeypad();
            return true;
        }
    };
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            hideKeypad();
            searchProducts(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            String[] querySplit = newText.toLowerCase().split(" ");
            mDisplayProductList.clear();
            mDisplayProductList.addAll(mProductList.stream()
                    .filter(item -> Arrays.stream(querySplit).parallel().allMatch(item.getName().toLowerCase()::contains))
                    .collect(Collectors.toList()));
            mDisplayProductList.addAll(mProductList.stream()
                    .filter(item -> item.getBarcode().toLowerCase().contains(newText))
                    .collect(Collectors.toList()));
            orderProductRecyclerViewAdapter.notifyItemRangeChanged(0, mDisplayProductList.size());
            orderProductRecyclerViewAdapter.notifyDataSetChanged();
            return true;
        }
    };
    private SearchView.OnFocusChangeListener focusChangeListener = (view, focused) -> {
        if (!focused) {
//            int range = mDisplayProductList.size();
//            mDisplayProductList.clear();
//            orderProductRecyclerViewAdapter.notifyItemRangeRemoved(0, range);
//            mDisplayProductList.addAll(mProductList);
//            orderProductRecyclerViewAdapter.notifyItemRangeChanged(0, mDisplayProductList.size());
//            orderProductRecyclerViewAdapter.notifyDataSetChanged();
            hideKeypad();
        }
    };
    private DrawerLayout mDrawerLayout;
    {
        mCategoryList.add(new Category(-1, "Tất cả"));
    }

    private Button.OnClickListener paymentButtonListener = view -> {
        if (mOrderList.size() < 1) {
            makeSnackbar(view, R.string.order_add_not_selected_warning);
            paymentDialog();

        } else {
//            if(isInOrder(mSelectedProduct.getName())){
////                if (quantityTextInput.getText().toString().isEmpty()) quantityTextInput.setText("1");
//                increaseOrderQuantity(mSelectedProduct.getName());
//                quantityTextInput.getText().clear();
//            }
//            else {
////                if (quantityTextInput.getText().toString().isEmpty()) quantityTextInput.setText("1");
//                getSelectedProductDetailAndAddToOrder(mSelectedProduct.getId(), false);
//            }
            paymentDialog();
        }
    };

    private Button.OnClickListener cancelButtonListener = view -> {
        try {
            this.finish();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
    };

    private Button.OnClickListener saveButtonListener = view -> {
        if (mOrderList.size() < 1) {
            makeSnackbar(view, R.string.order_add_not_selected_warning);
        } else {
            saveAndClearOrder();
        }
    };
    public static void setIsPrinterConnected(boolean isPrinterConnected) {
        IS_PRINTER_CONNECTED = isPrinterConnected;
    }

    private void onClickAdd(Product selectedProduct) {
        if (isInOrder(selectedProduct.getName())) {
//            if (quantityTextInput.getText().toString().isEmpty()) quantityTextInput.setText("1");
            increaseOrderQuantity(selectedProduct.getName());
            quantityTextInput.getText().clear();
        } else {
//            if (quantityTextInput.getText().toString().isEmpty()) quantityTextInput.setText("1");
            getSelectedProductDetailAndAddToOrder(selectedProduct.getId(), false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_selling_name));
        invalidateOptionsMenu();
        setContentView(R.layout.activity_selling);
        Log.d(getApplicationContext().toString(), "Login status: " + SaveSharedPreference.getLoggedStatus(getApplicationContext()));
        viewLookup();
        viewSetup();
        setupDisplays();
        setViewsListeners();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.getMenu().getItem(BaseActivity.NAV_MENU_SELLING_ID).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        businessChainRESTService = BusinessChainRESTClient.getClient(getApplicationContext()).create(BusinessChainRESTService.class);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initDialog();
        initRecyclerViews();
        getStoreInfo();
        getAllProducts();
        getAllCategoriesSortedByPopularity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.navSelling) {
        } else if (id == R.id.navMain) {
            finish();
//            intent = new Intent(SellingActivity.this, MainActivity.class);
//            startActivity(intent);
        } else if (id == R.id.navReturn) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        } else if (id == R.id.navProduct) {
            finish();
            intent = new Intent(SellingActivity.this, ProductActivity.class);
            startActivity(intent);
        } /*else if (id == R.id.navSettings) {
            finish();
            intent = new Intent(SellingActivity.this, SettingsActivity.class);
            startActivity(intent);
        }*/ else if (id == R.id.navLogout) {
            SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
            intent = new Intent(SellingActivity.this, LoginActivity.class);
            intent.putExtra("isParentRoot", isTaskRoot());
            finish();
            startActivity(intent);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!orderProductSearchView.hasFocus() && !quantityTextInput.hasFocus()) {
            char c = (char) event.getUnicodeChar();
            if (keyCode != KeyEvent.KEYCODE_ENTER && c != '\u0000') BARCODE = BARCODE + (String.valueOf(c));

            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER: {
                    scanAndAdd(BARCODE);
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataLoadingDialog.cancel();
        doneDialog.cancel();
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
        List<Product> customerOrderList = new ArrayList<>(0);
        customerOrderList.addAll(mOrderList);
        customerScreen = new CustomerScreen(this, displays[1], customerOrderList); // small screen
    }

    private void viewLookup() {
        orderProductSearchView = findViewById(R.id.orderProductSearchView);
        orderProductRecyclerView = findViewById(R.id.orderProductRV);
        orderListRecyclerView = findViewById(R.id.orderListRV);
        categoryListRecyclerView = findViewById(R.id.categoryRV);
        quantityTextInput = findViewById(R.id.quantityInputText);
        orderTitle = findViewById(R.id.orderTitle);
        orderTotalTextView = findViewById(R.id.orderTotalPrice);
//        addToOrderButton = findViewById(R.id.addToListButton);
//        tabLayout = findViewById(R.id.tabLayout);
//        viewPager = findViewById(R.id.paymentViewPager);
//        tabLayout.setupWithViewPager(viewPager);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        cancelButton = findViewById(R.id.cancel);
        saveButton = findViewById(R.id.save);
        paymentButton = findViewById(R.id.done);
    }

    private void viewSetup() {
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, '0'), Html.FROM_HTML_MODE_LEGACY));
        orderTitle.setText(getString(R.string.order_title_label, getTotalQuantity()));
        quantityTextInput.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        orderProductSearchView.setIconified(false);
        orderProductSearchView.clearFocus();
//        viewPagerAdapter = new PaymentPagerAdapter(this, getSupportFragmentManager());
//        viewPager.setAdapter(viewPagerAdapter);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setViewsListeners() {
        orderProductSearchView.setOnQueryTextFocusChangeListener(focusChangeListener);
        orderProductSearchView.setOnQueryTextListener(queryTextListener);
        orderProductSearchView.setOnCloseListener(searchViewCloseListener);
        quantityTextInput.setOnFocusChangeListener(focusChangeListener);
        orderProductRecyclerView.setOnTouchListener(onTouchListener);
        orderListRecyclerView.setOnTouchListener(onTouchListener);
        cancelButton.setOnClickListener(cancelButtonListener);
        saveButton.setOnClickListener(saveButtonListener);
        paymentButton.setOnClickListener(paymentButtonListener);
//        TextValidator quantityTextWatcher = new TextValidator(quantityTextInput) {
//            @Override
//            public void validate(TextView textView, String text) {
//                if (text.length() < 1) {
//                    textView.setText("1");
//                }
//            }
//        };
//
//        quantityTextInput.addTextChangedListener(quantityTextWatcher);
//        addToOrderButton.setOnClickListener(paymentButtonListener);
    }

    private void initDialog() {
        dataLoadingDialog = new ProgressDialog(this);
        dataLoadingDialog.setMessage(getString(R.string.data_loading_message));
//        dataLoadingDialog.getWindow().setGravity(Gravity.BOTTOM);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        doneDialog = builder.setTitle("Đơn hàng và in bill đã hoàn tất")
                .setMessage("\nBill đã được ghi nhận\n")
                .setIcon(R.drawable.ic_payment_done)
                .create();

//        doneDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
//        doneDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_done);
    }

    private void initRecyclerViews() {
        orderProductRecyclerViewAdapter = new OrderProductRecyclerViewAdapter(mDisplayProductList, this, (Context) this);
        RecyclerView.LayoutManager orderProductRecyclerViewLayoutManager = new GridLayoutManager(this, 3);
        orderProductRecyclerView.setLayoutManager(orderProductRecyclerViewLayoutManager);
        orderProductRecyclerView.setAdapter(orderProductRecyclerViewAdapter);

        orderListRecyclerViewAdapter = new OrderListRecyclerViewAdapter(mOrderList, orderTotalTextView, orderTitle, customerScreen);
        RecyclerView.LayoutManager orderListRecyclerViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
         orderListRecyclerView.setLayoutManager(orderListRecyclerViewLayoutManager);
        orderListRecyclerView.setAdapter(orderListRecyclerViewAdapter);

        categoryListRecyclerViewAdapter = new CategoryListRecyclerViewAdapter(mCategoryList, this);
        RecyclerView.LayoutManager categoryListRecyclerViewLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        categoryListRecyclerView.setLayoutManager(categoryListRecyclerViewLayoutManager);
        categoryListRecyclerView.setAdapter(categoryListRecyclerViewAdapter);
    }

    private void getAllProducts() {
        Call<List<Product>> call = businessChainRESTService.GetAllProductsApi(Integer.parseInt(SaveSharedPreference.getChainId(getApplicationContext())), Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())));

        dataLoadingDialog.show();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    mProductList.clear();
                    response.body().stream().forEach(product -> {
                        mProductList.add(product);
                    });
                    mDisplayProductList.addAll(mProductList);
                    orderProductRecyclerViewAdapter.notifyDataSetChanged();
                    dataLoadingDialog.hide();

                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("barcode")) {
                        scanAndAdd(getIntent().getStringExtra("barcode"));
                    } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(MainActivity.ARG_SAVED_ORDER)) {
                        mOrderList.addAll(getIntent().getExtras().getParcelableArrayList(MainActivity.ARG_SAVED_ORDER));
                        orderTitle.setText(getString(R.string.order_title_label, getTotalQuantity()));
                        customerScreen.mOrderListCustomer.addAll(getIntent().getExtras().getParcelableArrayList(MainActivity.ARG_SAVED_ORDER));
                        if (customerScreen.mOrderListCustomer.size() != 0) customerScreen.viewFlipper.showNext();
                        customerScreen.orderTitleTextView.setText(getString(R.string.order_title_label, getTotalQuantity()));
                        orderListRecyclerViewAdapter.notifyDataSetChanged();
                        customerScreen.orderListCustomerRecyclerViewAdapter.notifyDataSetChanged();
                        orderTitle.setText(getString(R.string.order_title_label, getTotalQuantity()));
                        customerScreen.orderTitleTextView.setText(getString(R.string.order_title_label, getTotalQuantity()));
                        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
                        customerScreen.orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
//                        IOrderTotalListener.getOrderTotal(calculateOrderTotalPriceNumber());
                    }

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    Log.e(TAG, response.message());
                    makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                    dataLoadingDialog.hide();
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
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                dataLoadingDialog.hide();
            }
        });
    }

    private void searchProducts(String query) {
        Call<List<Product>> call = businessChainRESTService.GetProductsByNameApi(Integer.parseInt(SaveSharedPreference.getChainId(getApplicationContext())), Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())), query);

        dataLoadingDialog.show();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    mDisplayProductList.clear();
                    mDisplayProductList.addAll(response.body());
                    orderProductRecyclerViewAdapter.notifyDataSetChanged();
                    dataLoadingDialog.hide();
                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    Log.e(TAG, response.message());
                    dataLoadingDialog.hide();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                throwable.printStackTrace();
                dataLoadingDialog.hide();
            }
        });
    }

    private void getSelectedProductDetailAndAddToOrder(int id, boolean fromScan) {
        Call<Product> call = businessChainRESTService.GetProductByIdApi(Integer.parseInt(SaveSharedPreference.getChainId(getApplicationContext())), Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())), id);
        dataLoadingDialog.show();
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200) {
                    Product curProduct = response.body();
                    curProduct.setLimQuantity(curProduct.getQuantity());
                    if (curProduct.getQuantity() > 0) {
                        if (Integer.parseInt(quantityTextInput.getText().toString().isEmpty() ? "1" : quantityTextInput.getText().toString()) <= curProduct.getQuantity()) {
                            curProduct.setQuantity(Integer.parseInt(quantityTextInput.getText().toString().isEmpty() ? "1" : quantityTextInput.getText().toString()));
                        }
                        if (isWholeSale(curProduct.getQuantity())) {
                            curProduct.setPrice(curProduct.getWholesalePrice());
                        } else {
                            curProduct.setPrice(curProduct.getRetailPrice());
                        }
                        addToOrder(curProduct);
//                        customerScreen.addToOrderCustomer(curProduct, calculateOrderTotalPrice());
                        quantityTextInput.getText().clear();
                        if (!fromScan) mSelectedProduct = curProduct;
                    } else {
                        Snackbar.make(orderProductRecyclerView, "Không đủ hàng trong kho (tại cửa hàng chỉ có: " + curProduct.getLimQuantity() + " món)!", Snackbar.LENGTH_LONG).show();
                    }
                    dataLoadingDialog.hide();
                } else {
                    Log.e(TAG, String.valueOf(response.code()));
                    Log.e(TAG, response.message());
                    dataLoadingDialog.hide();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable throwable) {
                throwable.printStackTrace();
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                dataLoadingDialog.hide();
            }
        });
    }

    private void getAllCategoriesSortedByPopularity() {
        Call<List<Category>> call = businessChainRESTService.GetSortedCategories(Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())));
        dataLoadingDialog.show();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    mCategoryList.addAll(response.body());
                    categoryListRecyclerViewAdapter.notifyDataSetChanged();
                    dataLoadingDialog.hide();
                }
                else {
                    Log.d(TAG, response.code() + ": " + response.message());
                    Toast.makeText(getBaseContext(), "Có lỗi xảy ra!\n" + response.code() + ": " + response.message(), Toast.LENGTH_LONG).show();
                    dataLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable throwable) {
                throwable.printStackTrace();
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                dataLoadingDialog.hide();
            }
        });
    }

    private void getAllProductsPerCategory(int categoryId) {
        Call<List<Product>> call = businessChainRESTService.GetProductByCategoryId(categoryId, Integer.parseInt(SaveSharedPreference.getChainId(getApplicationContext())), Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())));
        dataLoadingDialog.show();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.code() == 200) {
                    mDisplayProductList.clear();
                    mDisplayProductList.addAll(response.body());
                    orderProductRecyclerViewAdapter.notifyDataSetChanged();
                    dataLoadingDialog.hide();
                } else {
                    Log.d(TAG, response.code() + ": " + response.message());
                    Toast.makeText(getBaseContext(), "Có lỗi xảy ra!\n" + response.code() + ": " + response.message(), Toast.LENGTH_LONG).show();
                    if (getIntent().getExtras().containsKey(MainActivity.ARG_SAVED_ORDER_FILENAME)) {
                        Log.d(TAG, "deleted");
                        saveAndClearOrder();
                    }
                    dataLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                throwable.printStackTrace();
                dataLoadingDialog.dismiss();
            }
        });
    }

    private boolean isWholeSale(int quantity) {
        return false;
    }

    private void addToOrder(Product selectedProduct) {
        mOrderList.add(selectedProduct);
        orderTitle.setText(getString(R.string.order_title_label, getTotalQuantity()));
        orderListRecyclerViewAdapter.notifyDataSetChanged();
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
        customerScreen.addToOrderCustomer(selectedProduct, calculateOrderTotalPrice());
        customerScreen.orderTitleTextView.setText(getString(R.string.order_title_label, getTotalQuantity()));
        orderListRecyclerView.scrollToPosition(mOrderList.size() - 1);
        customerScreen.orderListCustomerRecyclerView.scrollToPosition(mOrderList.size() - 1);
//        IOrderTotalListener.getOrderTotal(calculateOrderTotalPriceNumber());
    }

    private void increaseOrderQuantity(String name) {
        int tempQuantity = Integer.parseInt(quantityTextInput.getText().toString().isEmpty() ? "1" : quantityTextInput.getText().toString());
        mOrderList.stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .ifPresent(tProduct -> {
                    if (tempQuantity + tProduct.getQuantity() <= tProduct.getLimQuantity()) {
                        tProduct.setQuantity(tempQuantity + tProduct.getQuantity());
                        if (isWholeSale(tProduct.getQuantity())) {
                            tProduct.setPrice(tProduct.getWholesalePrice());
                        }
                    } else {
                        Snackbar.make(orderProductRecyclerView, "Không đủ hàng trong kho (tại cửa hàng chỉ có: " + tProduct.getLimQuantity() + " món)!", Snackbar.LENGTH_LONG).show();
                    }
                });
        orderListRecyclerViewAdapter.notifyDataSetChanged();
        customerScreen.orderListCustomerRecyclerViewAdapter.notifyDataSetChanged();
        orderTitle.setText(getString(R.string.order_title_label, getTotalQuantity()));
        customerScreen.orderTitleTextView.setText(getString(R.string.order_title_label, getTotalQuantity()));
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
        customerScreen.orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
//        IOrderTotalListener.getOrderTotal(calculateOrderTotalPriceNumber());
    }

    private void scanAndAdd(String barcode) {
        Product toAdd = mProductList.stream().filter(product -> product.getBarcode().toLowerCase().equals(barcode)).findFirst().orElse(null);
        if (toAdd != null) {
            if (isInOrder(toAdd.getName())) {
                increaseOrderQuantity(toAdd.getName());
                quantityTextInput.getText().clear();
            } else {
                getSelectedProductDetailAndAddToOrder(toAdd.getId(), true);
            }
        } else {
            makeSnackbar(orderProductRecyclerView, R.string.order_barcode_invalid);
        }
        BARCODE = "";
    }

    private void hideKeypad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
            imm.hideSoftInputFromWindow(orderProductSearchView.getWindowToken(), 0);
        }
    }

    private void makeSnackbar(View view, int stringResourceId) {
        Snackbar.make(view, stringResourceId, Snackbar.LENGTH_LONG)
                .show();
    }

    public String calculateOrderTotalPrice() {
        BigInteger totalPrice = BigInteger.ZERO;
        for (Product product : mOrderList) {
            totalPrice = totalPrice.add(BigInteger.valueOf((long) product.getRetailPrice() * product.getQuantity()));
        }
        return NumberFormat.getNumberInstance(Locale.GERMANY).format(totalPrice);
    }

    public BigInteger calculateOrderTotalPriceNumber() {
        BigInteger totalPrice = BigInteger.ZERO;
        for (Product product : mOrderList) {
            totalPrice = totalPrice.add(BigInteger.valueOf((long) product.getRetailPrice() * product.getQuantity()));
        }
        return totalPrice;
    }

    protected boolean isInOrder(String name) {
        return mOrderList.stream().filter(product -> product.getName().equals(name)).toArray().length != 0;
    }

    private void showButton(Button button) {
        if (button.getAlpha() == (float) 0.15) {
            AlphaAnimation fadeIn = new AlphaAnimation((float) 0.15, (float) 1);
            fadeIn.setDuration(150);
            button.setAnimation(fadeIn);
            button.startAnimation(fadeIn);
            button.setAlpha((float) 1);
        }
    }

    public void createCachedFile(Context context, String fileName, Order order) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("order" + fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(order.getOrderDate());
//            for (Product product : order.getProducts()){
//                objectOutputStream.writeObject(product);
//            }
            objectOutputStream.writeObject(order);
//            objectOutputStream.writeBoolean(true);
            objectOutputStream.flush();
            outputStream.flush();
            objectOutputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideButton(Button button) {
        if (button.getAlpha() == (float) 1) {
            AlphaAnimation fadeOut = new AlphaAnimation((float) 1, (float) 0.15);
            fadeOut.setDuration(150);
            button.setAnimation(fadeOut);
            button.startAnimation(fadeOut);
            button.setAlpha((float) 0.15);
        }
    }

    private String getBill(String received, String change, String paymentType) {
        String bill = AppUtils.centerString("\u0020\u0020" + AppUtils.removeAccent(mStore.getName())) + "\n" + "\n-----------------------||-----------------------\n\n\n" + AppUtils.centerString("\u0020\u0020Dia chi: "+ AppUtils.removeAccent(mStore.getAddress())) + "\n\u0020\u0020Ma don: " + mCurrentOrder.getReceiptCode() + "\n\u0020\u0020Ngay: " + AppUtils.getCurrentFormattedDate() + "\n\u0020\u0020Cashier: " + AppUtils.removeAccent(SaveSharedPreference.getName(getApplicationContext())) + "\n------------------------------------------------\n";
        bill = bill.concat(AppUtils.formattedOrderItem("STT", "Don gia", "Slg", "Thanh tien", true));
        int index = 0;
        for (Product product : mOrderList) {
            bill = bill.concat(AppUtils.cutAndAddNewLine(AppUtils.removeAccent(product.getName()), 48)).concat("\n");
            bill = bill.concat(AppUtils.formattedOrderItem(String.valueOf(++index), AppUtils.formattedStringMoneyString(String.valueOf(product.getPrice())), String.valueOf(product.getQuantity()), AppUtils.formattedStringMoneyString(String.valueOf(product.getPrice() * product.getQuantity())), true));
        }
        bill = bill.concat("------------------------------------------------\n")
                .concat(AppUtils.formattedOrderItem("", "Tong tien: ", "", AppUtils.formattedBigIntegerMoneyString(calculateOrderTotalPriceNumber()), false))
                .concat(AppUtils.formattedOrderItem("", "Thanh toan bang: ", "", paymentType.equals("cash") ? "Tien mat" : "The", false))
                .concat(AppUtils.formattedOrderItem("", "Tien khach tra: ", "", AppUtils.formattedStringMoneyString(received), false))
                .concat(AppUtils.formattedOrderItem("", "Tien thua: ", "", AppUtils.formattedStringMoneyString(change), false))
                .concat("------------------------------------------------\n")
                .concat(StringUtils.center("CAM ON QUY KHACH VA HEN GAP LAI",48) + "\n\n")
                .concat(StringUtils.center("Hotline: " + mStore.getPhone(), 48))
                .concat(AppUtils.centerString("Mail: " + mStore.getEmail()));
//                .concat("\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020" + mStore.getPhone() + "\n")
//                .concat(AppUtils.formattedOrderItem("", "Mail:", "", "", false))
//                .concat("\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020" + mStore.getEmail() + "\n");
        Log.d(TAG, bill);
        return bill;
    }

    private void printBill(String str) {
        printBarcode();
        if (IS_PRINTER_CONNECTED) MainActivity.binder.writeDataByYouself(
                new UiExecute() {
                    @Override
                    public void onsucess() {

                    }

                    @Override
                    public void onfailed() {

                    }
                }, new ProcessData() {
                    @Override
                    public List<byte[]> processDataBeforeSend() {
                        List<byte[]> list = new ArrayList<byte[]>();
                        if (!str.equals("")) {
                            //initialize the printer
//                            list.add( DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos80.initializePrinter());

                            byte[] data1 = appUtil.strTobytes(str);
                            list.add(data1);
                            //should add the command of print and feed line,because print only when one line is complete, not one line, no print
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            //cut pager
                            list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66, 1));
                            return list;
                        }
                        return null;
                    }
                });
    }

    private void printBarcode() {
        if (IS_PRINTER_CONNECTED) {
            MainActivity.binder.writeDataByYouself(new UiExecute() {
                @Override
                public void onsucess() {

                }

                @Override
                public void onfailed() {

                }
            }, () -> {
                List<byte[]> list = new ArrayList<byte[]>();
                //initialize the printer
                list.add(DataForSendToPrinterPos80.initializePrinter());
                //select alignment
                list.add(DataForSendToPrinterPos80.selectAlignment(1));
                //select HRI position
                list.add(DataForSendToPrinterPos80.selectHRICharacterPrintPosition(02));
                //set the width
                list.add(DataForSendToPrinterPos80.setBarcodeWidth(3));
                //set the height ,usually 162
                list.add(DataForSendToPrinterPos80.setBarcodeHeight(162));
                //print barcode ,attention,there are two method for barcode.
                //different barcode type,please refer to the programming manual
                //UPC-A
                list.add(DataForSendToPrinterPos80.printBarcode(69, 16, mCurrentOrder.getReceiptCode()));

                list.add(DataForSendToPrinterPos80.printAndFeedLine());

                return list;
            });
        }
    }

    @Override
    public void recyclerViewListClickListener(View v, int position, ColorStateList mTextColor) {
        orderProductSearchView.clearFocus();
        hideKeypad();
        mSelectedProduct = mDisplayProductList.get(position);
        mSelectedProduct.setQuantity(Integer.parseInt(quantityTextInput.getText().toString().isEmpty() ? "1" : quantityTextInput.getText().toString()));
        onClickAdd(mSelectedProduct);
//        if (selectedCard != null) {
//            if (selectedPos != position) {
//                TextView nameTV = selectedCard.findViewById(R.id.orderProductName);
//                TextView barcodeTV = selectedCard.findViewById(R.id.orderProductBarcode);
//                CardView cardContainer = selectedCard.findViewById(R.id.orderProductCardContainer);
//                nameTV.setTextColor(mTextColor);
//                barcodeTV.setTextColor(mTextColor);
//                cardContainer.setCardBackgroundColor(this.getResources().getColor(R.color.white, getTheme()));
//                selectedCard = v;
//                selectedPos = position;
//                mSelectedProduct = mDisplayProductList.get(position);
//                Log.d(TAG, "I select another card");
//            }
//            else {
//                TextView nameTV = selectedCard.findViewById(R.id.orderProductName);
//                TextView barcodeTV = selectedCard.findViewById(R.id.orderProductBarcode);
//                CardView cardContainer = selectedCard.findViewById(R.id.orderProductCardContainer);
//                nameTV.setTextColor(mTextColor);
//                barcodeTV.setTextColor(mTextColor);
//                cardContainer.setCardBackgroundColor(this.getResources().getColor(R.color.white, getTheme()));
//                selectedCard = null;
//                selectedPos = -1;
//                mSelectedProduct = null;
//                Log.d(TAG, "I deselect");
//            }
//        }
//        else {
//            selectedCard = v;
//            selectedPos = position;
//            mSelectedProduct = mDisplayProductList.get(position);
//            Log.d(TAG, mSelectedProduct.getName());
//        }
    }

    @Override
    public void categoryRecyclerViewListClickListener(View v, int position) {
        int selectedCategoryId = mCategoryList.get(position).getCategoryId();
        if (selectedCategoryId == -1) {
            mDisplayProductList.clear();
            mDisplayProductList.addAll(mProductList);
            orderProductRecyclerViewAdapter.notifyDataSetChanged();
        } else getAllProductsPerCategory(selectedCategoryId);
    }

    @Override
    public void saveAndClearOrder() {
        if (mCurrentOrder.getOrderDate().isEmpty())
            mCurrentOrder.setOrderDate(AppUtils.getCurrentFormattedDate());
        mCurrentOrder.setProducts(mOrderList);
        if (getIntent().getExtras().containsKey(MainActivity.ARG_SAVED_ORDER)) {
            createCachedFile(this, String.valueOf(getIntent().getExtras().getString(MainActivity.ARG_SAVED_ORDER_FILENAME)), mCurrentOrder);
        } else {
            int currentSavedId = 1;
            String[] localFileList = fileList();
            for (String filename : localFileList) {
                if (filename.contains("order")) {
                    if (Integer.parseInt(filename.split("order")[1]) == currentSavedId)
                        currentSavedId++;
                    else break;
                }
            }
            createCachedFile(this, String.valueOf(currentSavedId), mCurrentOrder);
        }
        Snackbar.make(orderListRecyclerView, "Đơn hàng đã được lưu lại", Snackbar.LENGTH_LONG).show();
        clearOrder();
//        IOrderTotalListener.getOrderTotal(calculateOrderTotalPriceNumber());
    }

    @Override
    public void paymentDialog() {
        PaymentDialog paymentDialog = new PaymentDialog();
        Bundle arguments = new Bundle();
        arguments.putSerializable("orderTotal", calculateOrderTotalPriceNumber());
        paymentDialog.setArguments(arguments);
        paymentDialog.show(getSupportFragmentManager(), "completeDialog");
    }

    @Override
    public void completeOrderAndSubmit(String received, String change, TextView changeAmountTextView, String paymentType) {
        mCurrentOrder.setChainId(SaveSharedPreference.getChainId(getApplicationContext()) != "" ? SaveSharedPreference.getChainId(getApplicationContext()) : "1");
        mCurrentOrder.setShopId(SaveSharedPreference.getStoreId(getApplicationContext()));
        mCurrentOrder.setUserName(SaveSharedPreference.getName(getApplicationContext()));
        mCurrentOrder.setOrderDate(AppUtils.getCurrentFormattedDate());
        mCurrentOrder.setPayment(paymentType);
        mCurrentOrder.setReceiptTotalPrice(calculateOrderTotalPriceNumber().intValue());
        mCurrentOrder.setProducts(mOrderList);
        Call<Order> call = businessChainRESTService.CreateReceipt(mCurrentOrder);
        dataLoadingDialog.show();
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.code() == 200) {
                    Snackbar.make(orderProductSearchView, "Done", Snackbar.LENGTH_LONG);
                    mCurrentOrder.setReceiptCode(response.body().getReceiptCode());
                    try {
                        if (getIntent().getExtras().containsKey(MainActivity.ARG_SAVED_ORDER_FILENAME)) {
                            Log.d(TAG, "deleted");
                            deleteFile("order".concat(getIntent().getExtras().getString(MainActivity.ARG_SAVED_ORDER_FILENAME)));
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    showDoneDialog(paymentType, received, change);
                    clearOrder();
//                    IOrderTotalListener.getOrderTotal(calculateOrderTotalPriceNumber());
                    if (changeAmountTextView != null)
                        changeAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_payment_cash_change, "0")));
                    dataLoadingDialog.dismiss();
                } else {
                    Log.d(TAG, response.code() + ": " + response.message());
                    Toast.makeText(getBaseContext(), "Có lỗi xảy ra!\n" + response.code() + ": " + response.message(), Toast.LENGTH_LONG).show();
                    if (getIntent().getExtras().containsKey(MainActivity.ARG_SAVED_ORDER_FILENAME)) {
                        Log.d(TAG, "deleted");
                        saveAndClearOrder();
                    }
                    dataLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                throwable.printStackTrace();
                dataLoadingDialog.dismiss();
            }
        });
    }

    @Override
    public void clearOrder() {
        int range = mOrderList.size();
        mOrderList.clear();
        orderTitle.setText(getString(R.string.order_title_label, getTotalQuantity()));
        customerScreen.mOrderListCustomer.clear();
        customerScreen.viewFlipper.showPrevious();
        customerScreen.orderTitleTextView.setText(getString(R.string.order_title_label, getTotalQuantity()));
        try {
            orderProductRecyclerView.findViewHolderForAdapterPosition(selectedPos).itemView.performClick();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
//        if (selectedCard != null) selectedCard.performClick();
        orderListRecyclerViewAdapter.notifyItemRangeRemoved(0, range);
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, '0'), Html.FROM_HTML_MODE_LEGACY));
        customerScreen.orderListCustomerRecyclerViewAdapter.notifyItemRangeRemoved(0, range);
        customerScreen.orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, '0'), Html.FROM_HTML_MODE_LEGACY));
    }

    private void getStoreInfo() {
        Call<Store> call = businessChainRESTService.getStoreDetails(Integer.parseInt(SaveSharedPreference.getStoreId(getApplicationContext())));
        dataLoadingDialog.show();
        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.code() == 200){
                    mStore = response.body();
                }
                else {
                    Log.d(TAG, response.code() + ": " + response.message());
                    Toast.makeText(getBaseContext(), "Có lỗi xảy ra!\n" + response.code() + ": " + response.message(), Toast.LENGTH_LONG).show();
                    if (getIntent().getExtras().containsKey(MainActivity.ARG_SAVED_ORDER_FILENAME)) {
                        Log.d(TAG, "deleted");
                        saveAndClearOrder();
                    }
                    dataLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                throwable.printStackTrace();
                dataLoadingDialog.dismiss();
            }
        });
    }

    @Override
    public void showDoneDialog(String paymentType, String received, String change) {
        String bill = getBill(received, change, paymentType);
        printBill(bill);
        doneDialog.show();
        customerScreen.showThankYouDialog();
        final Handler doneHandler  = new Handler();
        final Handler thankYouHandler  = new Handler();
        final Runnable doneRunnable = () -> {
            if (doneDialog.isShowing()) {
                doneDialog.dismiss();
            }
        };
        final Runnable thankYouRunnable = () -> {
            if (customerScreen.thankYouDialog.isShowing()) {
                customerScreen.thankYouDialog.dismiss();
            }
        };

        doneDialog.setOnDismissListener(dialog -> doneHandler.removeCallbacks(doneRunnable));
        customerScreen.thankYouDialog.setOnDismissListener(dialog -> thankYouHandler.removeCallbacks(thankYouRunnable));

        doneHandler.postDelayed(doneRunnable, 3000);
        doneHandler.postDelayed(thankYouRunnable, 3000);
    }

    public int getTotalQuantity(){
        final int[] total = {0};
        mOrderList.stream().forEach(product -> total[0] += product.getQuantity());
        return total[0];
    }

    public void setOrderTotalListener(OrderTotalListener listener) {
//        this.IOrderTotalListener = listener;
    }

    public interface OrderTotalListener {
        void getOrderTotal(BigInteger orderTotal);
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
