package com.smb_business_chain_management.func_selling;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.base.BasePresentation;
import com.smb_business_chain_management.func_login.SaveSharedPreference;
import com.smb_business_chain_management.models.Product;

import java.util.List;

public class CustomerScreen extends BasePresentation {
    protected List<Product> mOrderListCustomer;

    protected RecyclerView orderListCustomerRecyclerView;
    protected RecyclerView.Adapter orderListCustomerRecyclerViewAdapter;
    protected TextView orderTotalTextView;
    protected TextView orderTitleTextView;
    protected AlertDialog thankYouDialog;
    protected ViewFlipper viewFlipper;
    private TextView cashierName;

    public CustomerScreen(Context outerContext, Display display, List<Product> orderList) {
        super(outerContext, display);
        mOrderListCustomer = orderList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_customer);
        viewLookup();
        initAdapter();
        initThankYouDialog();
        orderTotalTextView.setText(Html.fromHtml(getContext().getString(R.string.order_total, "0"), Html.FROM_HTML_MODE_LEGACY));
        orderTitleTextView.setText(getContext().getString(R.string.order_title_label, mOrderListCustomer.size()));
        cashierName.setText(getContext().getResources().getString(R.string.action_bar_label_cashier, SaveSharedPreference.getName(getContext().getApplicationContext())));
    }

    private void initThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        thankYouDialog=  builder.setTitle("Đơn hàng và in bill đã hoàn tất")
                .setMessage("\nCảm ơn quý khác đã mua hàng. Hẹ gặp lại!\n")
                .setIcon(R.drawable.ic_thankyou)
                .create();
    }

    protected void showThankYouDialog(){
        thankYouDialog.show();
    }

    private void viewLookup(){
        orderListCustomerRecyclerView = findViewById(R.id.orderListRV);
        orderTotalTextView = findViewById(R.id.orderTotalPrice);
        orderTitleTextView = findViewById(R.id.orderTitle);
        viewFlipper = findViewById(R.id.viewFliper);
        cashierName = findViewById(R.id.cashierName);
    }

    private void initAdapter(){
        orderListCustomerRecyclerViewAdapter = new OrderListRecyclerViewAdapter(mOrderListCustomer, orderTotalTextView, orderTitleTextView, this);
        RecyclerView.LayoutManager orderListRecyclerViewLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        orderListCustomerRecyclerView.setLayoutManager(orderListRecyclerViewLayoutManager);
        orderListCustomerRecyclerView.setAdapter(orderListCustomerRecyclerViewAdapter);
    }

    public void addToOrderCustomer(Product selectedProduct, String totalPrice){
        if (this.mOrderListCustomer.size() == 0) viewFlipper.showNext();
        this.mOrderListCustomer.add(selectedProduct);
        this.orderListCustomerRecyclerViewAdapter.notifyDataSetChanged();
        this.orderTotalTextView.setText(Html.fromHtml(getContext().getString(R.string.order_total, totalPrice), Html.FROM_HTML_MODE_LEGACY));
//        this.orderTitleTextView.setText(getContext().getString(R.string.order_title_label,((SellingActivity)getOwnerActivity()).getTotalQuantity()));
    }

//    private void increaseOrderQuantity(String name) {
//        int tempQuantity = Integer.parseInt(quantityTextInput.getText().toString().isEmpty() ? "1" : quantityTextInput.getText().toString());
//        mOrderListCustomer.stream()
//                .filter(product -> product.getName().equals(name))
//                .findFirst()
//                .ifPresent(tProduct -> {
//                    if (tempQuantity + tProduct.getQuantity() <= tProduct.getLimQuantity()) {
//                        tProduct.setQuantity(tempQuantity + tProduct.getQuantity());
//                    } else {
//
//                    }
//                });
//        orderListCustomerRecyclerViewAdapter.notifyDataSetChanged();
//        orderTotalTextView.setText(Html.fromHtml(getString(R.string.order_total, calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
////        IOrderTotalListener.getOrderTotal(calculateOrderTotalPriceNumber());
//    }
}
