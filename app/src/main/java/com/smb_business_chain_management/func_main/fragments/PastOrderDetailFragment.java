package com.smb_business_chain_management.func_main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.func_main.PastOrderListener;
import com.smb_business_chain_management.func_main.PastOrdersRecyclerViewAdapter;
import com.smb_business_chain_management.models.Order;
import com.smb_business_chain_management.models.Product;

import java.util.ArrayList;


public class PastOrderDetailFragment extends Fragment {
    private static Order mOrder;
    TextView orderPrice;
    ListView orderItemListView;
    ArrayAdapter<String> orderItemListViewAdapter;
    Button resumeButton;
    PastOrderListener IListener;
    String fileName;
    ArrayList<Product> products;

    private Button.OnClickListener resumeButtonOnclickListener = view -> {
        IListener.continueOrder(products, fileName);
    };

    public PastOrderDetailFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            IListener = (PastOrderListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement PastOrderListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(PastOrdersRecyclerViewAdapter.ARG_ORDER)) {
            mOrder = getArguments().getParcelable(PastOrdersRecyclerViewAdapter.ARG_ORDER);
            fileName = getArguments().getString(PastOrdersRecyclerViewAdapter.ARG_FILENAME);
            products = getArguments().getParcelableArrayList("productList");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.past_order_detail_fragment, container, false);
        viewLookup(view);
        setUpViews();
        orderPrice.setText(AppUtils.formattedStringResource(getString(R.string.order_total, AppUtils.formattedMoneyString(mOrder.getTotalPrice()))));
        return view;
    }

    private void viewLookup(View view) {
        orderPrice = view.findViewById(R.id.orderTotalPrice);
        orderItemListView = view.findViewById(R.id.orderContentRV);
        resumeButton = view.findViewById(R.id.resumeButton);
    }

    private void setUpViews() {
        ArrayList<String> itemNameList = new ArrayList<>(0);
        for (Product product : mOrder.getProducts()) {
            itemNameList.add(product.getName());
        }
        orderItemListViewAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemNameList);
        orderItemListView.setAdapter(orderItemListViewAdapter);
        resumeButton.setOnClickListener(resumeButtonOnclickListener);
    }
}
