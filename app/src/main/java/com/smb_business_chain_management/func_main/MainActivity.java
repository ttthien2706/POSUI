package com.smb_business_chain_management.func_main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.ScreenManager;
import com.smb_business_chain_management.base.BaseActivity;
import com.smb_business_chain_management.base.BaseCustomerScreen;
import com.smb_business_chain_management.base.BasePresentation;
import com.smb_business_chain_management.func_login.LoginActivity;
import com.smb_business_chain_management.func_login.SaveSharedPreference;
import com.smb_business_chain_management.func_main.fragments.PastOrderDetailFragment;
import com.smb_business_chain_management.func_products.ProductActivity;
import com.smb_business_chain_management.func_selling.CustomerScreen;
import com.smb_business_chain_management.func_selling.SellingActivity;
import com.smb_business_chain_management.models.Order;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.Store;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.PosPrinterDev;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, PastOrderListener {
    public static final String ARG_SAVED_ORDER = "savedOrder";
    public static final String ARG_SAVED_ORDER_FILENAME = "fileName";
    public static final int SAVE_ORDER_CODE = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static IMyBinder binder;
    public static PosPrinterDev.PortType portType;
    private static Snackbar doneDialog;
    private static AlertDialog noPrinterDialog;
    public boolean IS_CONNECTED = false;
    String BARCODE = "";
    String usbAddress = "";
    ServiceConnection printerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Bind successfully
            binder = (IMyBinder) iBinder;
            Log.e("binder", "connected");
            connectUsb(usbAddress);
            Log.d("bind successfully, IS_CONNECTED", String.valueOf(IS_CONNECTED));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("disbinder", "disconnected");
        }
    };
    SparseArray<Order> orderList = new SparseArray<>(0);
    RecyclerView pastOrdersRecyclerView;
    RecyclerView.Adapter pastOrdersRecyclerViewAdapter;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    private BaseCustomerScreen customerScreen;
    private Display[] displays;
    private BusinessChainRESTService businessChainRESTService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.app_title));
        setContentView(R.layout.activity_main);
        viewLookup();
        initDialogs();
        setupPrinter();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

//        CardView menuSettings = findViewById(R.id.settingsMenu);
        CardView menuProducts = findViewById(R.id.productsMenu);
        CardView menuNewOrder = findViewById(R.id.newOrderMenu);
//        menuSettings.setOnClickListener(new MenuIconListener());
        menuProducts.setOnClickListener(new MenuIconListener());
        menuNewOrder.setOnClickListener(new MenuIconListener());

        orderList = getSavedOrders();
        Log.d(getApplicationContext().toString(), "Login status: " + SaveSharedPreference.getLoggedStatus(getApplicationContext()));

        setupViews();
        setupDisplays();

        navigationView.getMenu().getItem(BaseActivity.NAV_MENU_HOME_ID).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.navSelling) {
            if (!IS_CONNECTED) {
                if (!noPrinterDialog.isShowing()) noPrinterDialog.show();
            } else {
                intent = new Intent(MainActivity.this, SellingActivity.class);
                intent.putExtra("isParentRoot", isTaskRoot());
                startActivityForResult(intent, SAVE_ORDER_CODE);
            }
        } else if (id == R.id.navReturn) {
        } else if (id == R.id.navProduct) {
            intent = new Intent(MainActivity.this, ProductActivity.class);
            intent.putExtra("isParentRoot", isTaskRoot());
            startActivity(intent);
        } /*else if (id == R.id.navSettings) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("isParentRoot", isTaskRoot());
            startActivity(intent);
        } */ else if (id == R.id.navLogout) {
            SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
            intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("isParentRoot", isTaskRoot());
            finish();
            startActivity(intent);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if (keyCode != KeyEvent.KEYCODE_ENTER) BARCODE = BARCODE + (c);

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d("onKeyUp(), IS_CONNECTED", String.valueOf(IS_CONNECTED));
            if (!IS_CONNECTED) {
                if (!noPrinterDialog.isShowing()) noPrinterDialog.show();
                BARCODE = "";
            } else {
                Intent intent = new Intent(this, SellingActivity.class);
                intent.putExtra("isParentRoot", isTaskRoot());
                intent.putExtra("barcode", BARCODE);
                BARCODE = "";
                this.startActivityForResult(intent, RESULT_OK);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (customerScreen != null) {
            customerScreen.show();
        }
    }

    @Override
    public void continueOrder(ArrayList<Product> order, String fileName) {
        if (!IS_CONNECTED) {
            if (!noPrinterDialog.isShowing()) noPrinterDialog.show();
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(PastOrderDetailFragment.TAG);
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            Intent intent = new Intent(getBaseContext(), SellingActivity.class);
            intent.putExtra("isParentRoot", isTaskRoot());
            intent.putParcelableArrayListExtra(ARG_SAVED_ORDER, order);
            intent.putExtra(ARG_SAVED_ORDER_FILENAME, fileName);
            startActivityForResult(intent, SAVE_ORDER_CODE);
        }
    }

    public void viewLookup() {
        pastOrdersRecyclerView = findViewById(R.id.pastOrdersRV);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
    }

    public void setupViews() {
        pastOrdersRecyclerViewAdapter = new PastOrdersRecyclerViewAdapter(orderList, this);
        RecyclerView.LayoutManager orderProductRecyclerViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        pastOrdersRecyclerView.setLayoutManager(orderProductRecyclerViewLayoutManager);
        pastOrdersRecyclerView.setAdapter(pastOrdersRecyclerViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(pastOrdersRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        pastOrdersRecyclerView.addItemDecoration(dividerItemDecoration);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initDialogs() {
        doneDialog = Snackbar.make(pastOrdersRecyclerView, "Kết nối với máy in thành công", Snackbar.LENGTH_LONG);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kết nối máy in")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Không tìm thấy máy in, hãy kiểm tra lại kết nối giữa máy in và máy POS")
                .setPositiveButton("Thử lại", null)
                .setNegativeButton("Huỷ", ((dialog, which) -> {
                    IS_CONNECTED = false;
                    SellingActivity.setIsPrinterConnected(IS_CONNECTED);
                }));
        noPrinterDialog = builder.create();
        noPrinterDialog.setOnShowListener(dialogInterface -> {
            Button button = noPrinterDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                getUsbAddress();
                connectUsb(usbAddress);
            });
        });
    }

    private SparseArray<Order> getSavedOrders() {
        List<String> orderFiles = getAllOrderFilename();
        if (orderFiles.size() == 0) return new SparseArray<>(0);
        SparseArray<Order> retOrderList = new SparseArray<>(0);
        for (String filename : orderFiles) {
            Order tempOrder = readFiles(filename);
            try {
                retOrderList.put(Integer.parseInt(filename.split("order")[1]), tempOrder);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return retOrderList;
    }

    private Order readFiles(String filename) {
        Order tempOrder = new Order();
        tempOrder.setId(Integer.parseInt(filename.split("order")[1]));
        try {
            FileInputStream fileInputStream = openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            tempOrder.setOrderDate((String) objectInputStream.readObject());
            tempOrder = (Order) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            if (tempOrder == null) {
                deleteFile(filename);
            }
            return tempOrder;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (EOFException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getAllOrderFilename() {
        List<String> retNames = new ArrayList<>(0);
        for (String filename : fileList()) {
            if (filename.contains("order")) {
                retNames.add(filename);
            }
        }
        return retNames;
    }

    protected void updateSavedOrdersList() {
        orderList.clear();
        SparseArray<Order> tmpList = getSavedOrders();
        for (int i = 0; i < tmpList.size(); ++i) {
            orderList.append(tmpList.keyAt(i), tmpList.valueAt(i));
        }
        pastOrdersRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void setupDisplays() {
        ScreenManager screenManager = ScreenManager.getInstance();
        screenManager.init(this);
        displays = screenManager.getDisplays();
        if (displays.length >1) customerScreen = new BaseCustomerScreen(this, displays[1]); // small screen
    }

    private void setupPrinter() {
        //bind service，get ImyBinder object
        Intent intent = new Intent(this, PosprinterService.class);
        bindService(intent, printerConnection, BIND_AUTO_CREATE);

        getUsbAddress();
    }

    private void getUsbAddress() {
        if (PosPrinterDev.GetUsbPathNames(this) != null) {
            usbAddress = PosPrinterDev.GetUsbPathNames(this).get(0);
            if (noPrinterDialog.isShowing()) noPrinterDialog.dismiss();
            Log.d("getUsbAddress(), IS_CONNECTED", String.valueOf(IS_CONNECTED));
        } else {
            usbAddress = "";
            noPrinterDialog.show();
        }
    }

    private void connectUsb(String usbAddress) {
        if (usbAddress.isEmpty()) {
            Log.d("in connectUsb(), IS_CONNECTED", String.valueOf(IS_CONNECTED));
            IS_CONNECTED = false;
            SellingActivity.setIsPrinterConnected(IS_CONNECTED);
        } else {
            binder.connectUsbPort(this, usbAddress, new UiExecute() {
                @Override
                public void onsucess() {
                    IS_CONNECTED = true;
                    SellingActivity.setIsPrinterConnected(IS_CONNECTED);
                    setPortType(PosPrinterDev.PortType.USB);
                    doneDialog.show();
                }

                @Override
                public void onfailed() {
                    IS_CONNECTED = false;
                    SellingActivity.setIsPrinterConnected(IS_CONNECTED);
                    Log.d("onFailed(), IS_CONNECTED", String.valueOf(IS_CONNECTED));
                }
            });
        }
    }

    private void setPortType(PosPrinterDev.PortType portType) {
        this.portType = portType;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SAVE_ORDER_CODE && resultCode == RESULT_CANCELED) {
            updateSavedOrdersList();
        }
    }

    class MenuIconListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.newOrderMenu:
                    if (!IS_CONNECTED) {
                        if (!noPrinterDialog.isShowing()) noPrinterDialog.show();
                    } else {
                        intent = new Intent(view.getContext(), SellingActivity.class);
                        intent.putExtra("isParentRoot", isTaskRoot());
                        startActivityForResult(intent, SAVE_ORDER_CODE);
                    }
                    break;
                case R.id.returnOrderMenu:
                    break;
//                case R.id.importOrderMenu:
//                    break;
//                case R.id.exportOrderMenu:
//                    break;
//                case R.id.customersMenu:
//                    break;
                case R.id.productsMenu:
                    intent = new Intent(view.getContext(), ProductActivity.class);
                    intent.putExtra("isParentRoot", isTaskRoot());
                    view.getContext().startActivity(intent);
                    break;
//                case R.id.reportMenu:
//                    break;
//                case R.id.settingsMenu:
//                    intent = new Intent(view.getContext(), SettingsActivity.class);
//                    intent.putExtra("isParentRoot", isTaskRoot());
//                    view.getContext().startActivity(intent);
//                    break;
                default:
                    break;
            }
        }
    }
}
