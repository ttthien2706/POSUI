package com.smb_business_chain_management.func_selling;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.func_main.MainActivity;
import com.smb_business_chain_management.func_selling.fragments.OrderCompleteDialogFragment;
import com.smb_business_chain_management.models.Order;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.SubProduct;
import com.smb_business_chain_management.base.BaseActivity;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.PosPrinterDev;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellingActivity extends BaseActivity implements RecyclerViewClickListener, OrderListenerInterface {
    private static final String TAG = SellingActivity.class.getSimpleName();
    private static final AppUtils appUtil;
    static {
        appUtil = new AppUtils();
    }
    private static ProgressDialog dataLoadingDialog;
    private static AlertDialog doneDialog;

    private SearchView orderProductSearchView;
    private RecyclerView orderProductRecyclerView;
    private RecyclerView.Adapter orderProductRecyclerViewAdapter;
    private TextInputEditText quantityTextInput;
    private Button addToOrderButton;
    private Button completeButton;
    private RecyclerView orderListRecyclerView;
    private RecyclerView.Adapter orderListRecyclerViewAdapter;
    private TextView orderTotalTextView;

    private List<Product> mProductList = new ArrayList<>();
    private List<Product> mDisplayProductList = new ArrayList<>();
    private List<Product> mOrderList = new ArrayList<>();
    private Order mCurrentOrder = new Order();
    private Product mSelectedProduct = null;
    private View selectedCard = null;
    private int selectedPos = -1;
    String BARCODE = "";

    private BusinessChainRESTService businessChainRESTService;
    private SearchView.OnCloseListener searchViewCloseListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            int range = mDisplayProductList.size();
            mDisplayProductList.clear();
            orderProductRecyclerViewAdapter.notifyItemRangeRemoved(0, range);
            mDisplayProductList.addAll(mProductList);
            orderProductRecyclerViewAdapter.notifyItemRangeChanged(0, mDisplayProductList.size());
            orderProductRecyclerViewAdapter.notifyDataSetChanged();
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
            mDisplayProductList.clear();
            mDisplayProductList.addAll(mProductList.stream()
                    .filter(item -> item.getName().toLowerCase().contains(newText))
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
            hideKeypad();
        }
    };
    RecyclerView.OnTouchListener onTouchListener = ((View view, MotionEvent motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                hideButton(addToOrderButton);
                view.performClick();
                break;
            case MotionEvent.ACTION_MOVE:
                hideButton(addToOrderButton);
                break;
            case MotionEvent.ACTION_UP:
                showButton(addToOrderButton);
                break;
        }
        return super.onTouchEvent(motionEvent);
    });

    private Button.OnClickListener addButtonClickListener = view -> {
        if (mSelectedProduct == null) {
            makeSnackbar(view, R.string.order_add_not_selected_warning);
        } else {
            if(isInOrder(mSelectedProduct.getName())){
                increaseOrderQuantity(mSelectedProduct.getName());
                quantityTextInput.setText("1");
            }
            else {
                getSelectedProductDetailAndAddToOrder(mSelectedProduct.getId(), false);
            }
        }
    };
    private Button.OnClickListener completeButtonClickListener = view -> {
        if (mOrderList.size() >= 1) {
            OrderCompleteDialogFragment completeDialog = new OrderCompleteDialogFragment();
            completeDialog.show(getSupportFragmentManager(), "completeDialog");
        }
        else makeSnackbar(orderListRecyclerView, R.string.empty_order_warning);
    };

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

        viewLookup();
        viewSetup();
        setViewsListeners();

        businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initDialog();
        initRecyclerViews();
        getAllProducts();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void viewLookup() {
        orderProductSearchView = findViewById(R.id.orderProductSearchView);
        orderProductRecyclerView = findViewById(R.id.orderProductRV);
        orderListRecyclerView = findViewById(R.id.orderListRV);
        quantityTextInput = findViewById(R.id.quantityInputText);
        orderTotalTextView = findViewById(R.id.orderTotalPrice);
        addToOrderButton = findViewById(R.id.addToListButton);
        completeButton = findViewById(R.id.orderDoneButton);
    }
    private void viewSetup() {
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, '0'), Html.FROM_HTML_MODE_LEGACY));
        quantityTextInput.setTransformationMethod(new NumericKeyBoardTransformationMethod());
    }
    private void setViewsListeners() {
        orderProductSearchView.setOnQueryTextFocusChangeListener(focusChangeListener);
        orderProductSearchView.setOnQueryTextListener(queryTextListener);
        orderProductSearchView.setOnCloseListener(searchViewCloseListener);
        quantityTextInput.setOnFocusChangeListener(focusChangeListener);
        orderProductRecyclerView.setOnTouchListener(onTouchListener);
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
        addToOrderButton.setOnClickListener(addButtonClickListener);
        completeButton.setOnClickListener(completeButtonClickListener);
    }
    private void initDialog() {
        dataLoadingDialog = new ProgressDialog(this);
        dataLoadingDialog.setMessage(getString(R.string.data_loading_message));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Đơn hàng và in bill đã hoàn tất");
        doneDialog = builder.create();
        doneDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        doneDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_done);
    }
    private void initRecyclerViews() {
        orderProductRecyclerViewAdapter = new OrderProductRecyclerViewAdapter(mDisplayProductList, this);
        RecyclerView.LayoutManager orderProductRecyclerViewLayoutManager = new GridLayoutManager(this, 4);
        orderProductRecyclerView.setLayoutManager(orderProductRecyclerViewLayoutManager);
        orderProductRecyclerView.setAdapter(orderProductRecyclerViewAdapter);

        orderListRecyclerViewAdapter = new OrderListRecyclerViewAdapter(mOrderList, orderTotalTextView);
        RecyclerView.LayoutManager orderListRecyclerViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        orderListRecyclerView.setLayoutManager(orderListRecyclerViewLayoutManager);
        orderListRecyclerView.setAdapter(orderListRecyclerViewAdapter);
    }

    private void getAllProducts() {
        Call<List<Product>> call = businessChainRESTService.getAllProducts();

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

                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("barcode")){
                        scanAndAdd(getIntent().getStringExtra("barcode"));
                    }
                    else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(MainActivity.ARG_SAVED_ORDER)){
                        mOrderList.addAll(getIntent().getExtras().getParcelableArrayList(MainActivity.ARG_SAVED_ORDER));
                        orderListRecyclerViewAdapter.notifyDataSetChanged();
                        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
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
                Log.e(TAG, throwable.getMessage());
                Log.e(TAG, throwable.toString());
                throwable.printStackTrace();
                makeSnackbar(orderProductRecyclerView, R.string.REST_fail);
                dataLoadingDialog.hide();
            }
        });
    }
    private void searchProducts(String query) {
        Call<List<Product>> call = businessChainRESTService.searchProducts(query);

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
                throwable.printStackTrace();
                dataLoadingDialog.hide();
            }
        });
    }
    private void getSelectedProductDetailAndAddToOrder(int id, boolean fromScan) {
        Call<Product> call = businessChainRESTService.getProductDetails(id);
        dataLoadingDialog.show();
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200) {
                    Product curProduct = response.body();
                    if (Integer.parseInt(quantityTextInput.getText().toString()) < curProduct.getQuantity()) {
                        curProduct.setQuantity(Integer.parseInt(quantityTextInput.getText().toString()));
                    }
                    if (isWholeSale(curProduct.getQuantity())) {
                        curProduct.setPrice(curProduct.getWholesalePrice());
                    } else{
                        curProduct.setPrice(curProduct.getRetailPrice());
                    }
                    addToOrder(curProduct);
                    quantityTextInput.setText("1");
                    if (!fromScan) mSelectedProduct = curProduct;
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
                dataLoadingDialog.hide();
            }
        });
    }

    private boolean isWholeSale(int quantity) {
        if (quantity > 50) return true;
        return false;
    }

    private void addToOrder(Product selectedProduct) {
        mOrderList.add(selectedProduct);
//        orderListRecyclerViewAdapter.notifyItemRangeChanged(0, mOrderList.size());
        orderListRecyclerViewAdapter.notifyDataSetChanged();
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
    }
    private void increaseOrderQuantity(String name) {
        mOrderList.stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .ifPresent(tProduct -> {
                    tProduct.setQuantity(Integer.parseInt(quantityTextInput.getText().toString()) + tProduct.getQuantity());
                });
        orderListRecyclerViewAdapter.notifyDataSetChanged();
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
    }
    private void scanAndAdd(String barcode){
        Product toAdd = mProductList.stream().filter(product -> product.getBarcode().equals(barcode)).findFirst().orElse(null);
        if (toAdd != null){
            if(isInOrder(toAdd.getName())){
                increaseOrderQuantity(toAdd.getName());
                quantityTextInput.setText("1");
            }
            else {
                getSelectedProductDetailAndAddToOrder(toAdd.getId(), true);
            }
        }
        else{
            makeSnackbar(orderProductRecyclerView, R.string.order_barcode_invalid);
        }
        BARCODE = "";
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!orderProductSearchView.hasFocus() && !quantityTextInput.hasFocus()){
            char c = (char) event.getUnicodeChar();
            if (keyCode != KeyEvent.KEYCODE_ENTER) BARCODE = BARCODE + (String.valueOf(c));

            switch (keyCode){
                case KeyEvent.KEYCODE_ENTER: {
                    scanAndAdd(BARCODE);
                }
            }
        }
        return super.onKeyUp(keyCode, event);
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
        return NumberFormat.getNumberInstance(Locale.FRENCH).format(totalPrice);
    }
    public BigInteger calculateOrderTotalPriceNumber() {
        BigInteger totalPrice = BigInteger.ZERO;
        for (Product product : mOrderList) {
            totalPrice = totalPrice.add(BigInteger.valueOf((long) product.getRetailPrice() * product.getQuantity()));
        }
        return totalPrice;
    }
    protected boolean isInOrder(String name){
        return mOrderList.stream().filter(product -> product.getName().equals(name)).toArray().length != 0;
    }
    private void showButton(Button button) {
        if (button.getAlpha() == (float) 0.15) {
            AlphaAnimation fadeIn = new AlphaAnimation((float)0.15, (float)1);
            fadeIn.setDuration(150);
            button.setAnimation(fadeIn);
            button.startAnimation(fadeIn);
            button.setAlpha((float) 1);
        }
    }
    public void createCachedFile (Context context, String fileName, Order order) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("order"+fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(order.getOrderDate());
            objectOutputStream.writeInt(order.getProducts().size());
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
    private String getBill() {
        String bill = "\u0009Cua hang Nguyen Hue\n\u0020\u0020\u0020\u0020Dia chi: 92 Nguyen Hue, P. Ben Nghe, Q. 1, TP. HCM\n\u0020\u0020\u0020\u0020So: 1\n\u0020\u0020\u0020\u0020Ngay: " + "14/06/2019 13:11" +"\n\u0020\u0020\u0020\u0020Cashier: Ta Huy Hoang\n\u0020\u0020\u0020===================================\n";
        bill = bill.concat(AppUtils.formattedOrderItem("STT", "Don gia", "Slg", "Thanh tien"));
        int index = 0;
        for (Product product : mOrderList){
            bill = bill.concat(AppUtils.cutAndAddNewLine(product.getName(), 48)).concat("\n");
            bill = bill.concat(AppUtils.formattedOrderItem(String.valueOf(index++), String.valueOf(product.getPrice()), String.valueOf(product.getQuantity()), String.valueOf(product.getPrice()*product.getQuantity())));
        }
        return bill;
    }
    private void printBill(String str){
        MainActivity.binder.writeDataByYouself(
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
                        List<byte[]> list=new ArrayList<byte[]>();
                        if (str.equals(null)||str.equals("")){
                        }else {
                            //initialize the printer
//                            list.add( DataForSendToPrinterPos58.initializePrinter());
                            list.add(DataForSendToPrinterPos80.initializePrinter());

                            byte[] data1= appUtil.strTobytes(str);
                            list.add(data1);
                            //should add the command of print and feed line,because print only when one line is complete, not one line, no print
                            list.add(DataForSendToPrinterPos80.printAndFeedLine());
                            //cut pager
                            list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
                            return list;
                        }
                        return null;
                    }
                });
    }

    @Override
    public void recyclerViewListClickListener(View v, int position, ColorStateList mTextColor) {
        orderProductSearchView.clearFocus();
        hideKeypad();
        if (selectedCard != null) {
            if (selectedPos != position) {
                TextView nameTV = selectedCard.findViewById(R.id.orderProductName);
                CardView cardContainer = selectedCard.findViewById(R.id.orderProductCardContainer);
                nameTV.setTextColor(mTextColor);
                cardContainer.setCardBackgroundColor(this.getResources().getColor(R.color.white, getTheme()));
                selectedCard = v;
                selectedPos = position;
                mSelectedProduct = mDisplayProductList.get(position);
                Log.d(TAG, "I select another card");
            }
            else {
                TextView nameTV = selectedCard.findViewById(R.id.orderProductName);
                CardView cardContainer = selectedCard.findViewById(R.id.orderProductCardContainer);
                nameTV.setTextColor(mTextColor);
                cardContainer.setCardBackgroundColor(this.getResources().getColor(R.color.white, getTheme()));
                selectedCard = null;
                selectedPos = -1;
                mSelectedProduct = null;
                Log.d(TAG, "I deselect");
            }
        }
        else {
            selectedCard = v;
            selectedPos = position;
            mSelectedProduct = mDisplayProductList.get(position);
            Log.d(TAG, mSelectedProduct.getName());
        }
    }

    @Override
    public void recyclerViewListClickListener(View v, int position) {/*unused*/}

    @Override
    public void saveAndClearOrder() {
        int currentSavedId = 1;
        String[] localFileList = fileList();
        for (String filename : localFileList){
            if (filename.contains("order")){
                if (Integer.parseInt(filename.split("order")[1]) == currentSavedId)
                    currentSavedId++;
                else break;
            }
        }
//        mCurrentOrder.setId(currentSavedId);
        if (mCurrentOrder.getOrderDate().isEmpty()) mCurrentOrder.setOrderDate(appUtil.getCurrentFormattedDate());
        mCurrentOrder.setProducts(mOrderList);
        createCachedFile(this, String.valueOf(currentSavedId) ,mCurrentOrder);
        clearOrder();
    }

    @Override
    public void paymentDialog() {
        PaymentDialog paymentDialog = new PaymentDialog();

        paymentDialog.show(getSupportFragmentManager(), "completeDialog");
    }

    @Override
    public void completeOrderAndSubmit() {
        mCurrentOrder.setShopId(1);
        mCurrentOrder.setUserId(1);
        mCurrentOrder.setOrderDate(appUtil.getCurrentFormattedDate());
        mCurrentOrder.setProducts(mOrderList);
        Call<Order> call = businessChainRESTService.submitOrder(mCurrentOrder);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.code() == 200){
                    Snackbar.make(orderProductSearchView, "Done", Snackbar.LENGTH_INDEFINITE);
                }
                else {
                    Snackbar.make(orderProductSearchView, response.message(), Snackbar.LENGTH_INDEFINITE);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void clearOrder() {
        int range = mOrderList.size();
        mOrderList.clear();
        try {
            orderProductRecyclerView.findViewHolderForAdapterPosition(selectedPos).itemView.performClick();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
//        if (selectedCard != null) selectedCard.performClick();
        orderListRecyclerViewAdapter.notifyItemRangeRemoved(0, range);
        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, '0'), Html.FROM_HTML_MODE_LEGACY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataLoadingDialog.cancel();
        doneDialog.cancel();
    }

    @Override
    public void showDoneDialog() {
        String bill = getBill();
        printBill(bill);
        doneDialog.show();
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
