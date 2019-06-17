package com.smb_business_chain_management.func_main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.base.BaseActivity;
import com.smb_business_chain_management.func_products.ProductActivity;
import com.smb_business_chain_management.func_selling.SellingActivity;
import com.smb_business_chain_management.func_settings.SettingsActivity;
import com.smb_business_chain_management.models.Order;
import com.smb_business_chain_management.models.Product;

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

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, PastOrderListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ARG_SAVED_ORDER = "savedOrder";
    public static final String ARG_SAVED_ORDER_FILENAME = "fileName";

    String BARCODE = "";
    private static final int SAVE_ORDER_CODE = 2;
    boolean IS_CONNECTED = false;
    public static IMyBinder binder;
    ServiceConnection printerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Bind successfully
            binder= (IMyBinder) iBinder;
            Log.e("binder","connected");
            connectUsb(usbAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("disbinder","disconnected");
        }
    };
    public static PosPrinterDev.PortType portType;
    String usbAddress = "";

    SparseArray<Order> orderList = new SparseArray<>(0);
    RecyclerView pastOrdersRecyclerView;
    RecyclerView.Adapter pastOrdersRecyclerViewAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.app_title));
        setContentView(R.layout.activity_main);
        setupPrinter();

        viewLookup();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        CardView menuSettings = findViewById(R.id.settingsMenu);
        CardView menuProducts = findViewById(R.id.productsMenu);
        CardView menuNewOrder = findViewById(R.id.newOrderMenu);
        menuSettings.setOnClickListener(new MenuIconListener());
        menuProducts.setOnClickListener(new MenuIconListener());
        menuNewOrder.setOnClickListener(new MenuIconListener());

        orderList = getSavedOrders();

        initRecyclerView();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    @Override
    public void continueOrder(ArrayList<Product> order, String fileName) {
        Intent intent = new Intent(getBaseContext(), SellingActivity.class);
        intent.putExtra("isParentRoot", isTaskRoot());
        intent.putParcelableArrayListExtra(ARG_SAVED_ORDER, order);
        intent.putExtra(ARG_SAVED_ORDER_FILENAME, fileName);
//                    view.getContext().startActivity(intent);
        startActivityForResult(intent, SAVE_ORDER_CODE);
    }

    class MenuIconListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.newOrderMenu:
                    intent = new Intent(view.getContext(), SellingActivity.class);
                    intent.putExtra("isParentRoot", isTaskRoot());
//                    view.getContext().startActivity(intent);
                    startActivityForResult(intent, SAVE_ORDER_CODE);
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
                case R.id.settingsMenu:
                    intent = new Intent(view.getContext(), SettingsActivity.class);
                    intent.putExtra("isParentRoot", isTaskRoot());
                    view.getContext().startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if (keyCode != KeyEvent.KEYCODE_ENTER) BARCODE = BARCODE + (String.valueOf(c));

        switch (keyCode){
            case KeyEvent.KEYCODE_ENTER: {
                Intent intent = new Intent(this, SellingActivity.class);
                intent.putExtra("isParentRoot", isTaskRoot());
                intent.putExtra("barcode", BARCODE);
                BARCODE = "";
                this.startActivityForResult(intent, RESULT_OK);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void viewLookup(){
        pastOrdersRecyclerView = findViewById(R.id.pastOrdersRV);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
    }
    public void initRecyclerView(){
        pastOrdersRecyclerViewAdapter = new PastOrdersRecyclerViewAdapter(orderList, this);
        RecyclerView.LayoutManager orderProductRecyclerViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        pastOrdersRecyclerView.setLayoutManager(orderProductRecyclerViewLayoutManager);
        pastOrdersRecyclerView.setAdapter(pastOrdersRecyclerViewAdapter);
    }

    private SparseArray<Order> getSavedOrders(){
        List<String> orderFiles = getAllOrderFilename();
        if (orderFiles.size() == 0) return new SparseArray<>(0);
        SparseArray<Order> retOrderList = new SparseArray<>(0);
        for (String filename : orderFiles){
            Order tempOrder = readFiles(filename);
            try {
                retOrderList.put(Integer.parseInt(filename.split("order")[1]), tempOrder);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return retOrderList;
    }
    private Order readFiles(String filename){
        List<Product> tempProductList = new ArrayList<>(0);
        Order tempOrder = new Order();
        tempOrder.setId(Integer.parseInt(filename.split("order")[1]));
        try {
            FileInputStream fileInputStream = openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            tempOrder.setOrderDate((String) objectInputStream.readObject());
            int orderSize = objectInputStream.readInt();
//            for (int i = 0; i < orderSize; ++i){
//                Object o = objectInputStream.readObject();
//                try {
//                    tempProductList.add((Product) o);
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//            tempOrder.setProducts(tempProductList);
            tempOrder = (Order) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return tempOrder;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (EOFException e){
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassCastException e){
            e.printStackTrace();
            return null;
        }
    }
    private List<String> getAllOrderFilename(){
        List<String> retNames = new ArrayList<>(0);
        for (String filename : fileList()){
            if (filename.contains("order")){
                retNames.add(filename);
            }
        }
        return retNames;
    }
    private void setupPrinter(){
        //bind service，get ImyBinder object
        Intent intent=new Intent(this,PosprinterService.class);
        bindService(intent, printerConnection, BIND_AUTO_CREATE);

        usbAddress = PosPrinterDev.GetUsbPathNames(this).get(0);
    }
    private void connectUsb(String usbAddress) {
        if (usbAddress.isEmpty()){

        }else {
            binder.connectUsbPort(this, usbAddress, new UiExecute() {
                @Override
                public void onsucess() {
                    IS_CONNECTED =true;
                    setPortType(PosPrinterDev.PortType.USB);
                }
                @Override
                public void onfailed() {
                    IS_CONNECTED =false;
                }
            });
        }
    }
    private void setPortType(PosPrinterDev.PortType portType){
        this.portType=portType;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SAVE_ORDER_CODE && resultCode == RESULT_CANCELED){
            orderList.clear();
            SparseArray<Order> tmpList = getSavedOrders();
            for (int i = 0; i < tmpList.size(); ++i){
                orderList.append(tmpList.keyAt(i), tmpList.valueAt(i));
            }
            pastOrdersRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
