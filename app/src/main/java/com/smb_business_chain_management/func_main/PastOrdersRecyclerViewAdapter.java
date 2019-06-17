package com.smb_business_chain_management.func_main;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.func_main.fragments.PastOrderDetailFragment;
import com.smb_business_chain_management.models.Order;

import java.util.ArrayList;

public class PastOrdersRecyclerViewAdapter extends RecyclerView.Adapter<PastOrdersRecyclerViewAdapter.OrderListViewHolder> {
    public static final String ARG_ORDER = "currentOrder";
    public static final String ARG_FILENAME = "fileName";

    SparseArray<Order> mOrderList;
    private MainActivity mParentActivity;
    private static final AppUtils appUtils;
    static {
        appUtils = new AppUtils();
    }
    private final View.OnClickListener mOnclickListener = view -> {
        Order currentOrder = (Order) view.getTag();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ORDER, currentOrder);
        arguments.putParcelableArrayList("productList", (ArrayList<? extends Parcelable>) currentOrder.getProducts());
        arguments.putString(ARG_FILENAME, "order".concat(String.valueOf(view.getTag(R.id.orderId))));
        PastOrderDetailFragment fragment = new PastOrderDetailFragment();
        fragment.setArguments(arguments);
        mParentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pastOrderContent, fragment)
                .commit();
    };

    public PastOrdersRecyclerViewAdapter(SparseArray<Order> orderList, Context context) {
        mOrderList = orderList;
        mParentActivity = (MainActivity) context;
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder{
        TextView orderId;
        TextView orderDate;
        TextView orderTotalPrice;
        public OrderListViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTotalPrice = itemView.findViewById(R.id.orderPrice);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int position) {
        Order order = mOrderList.get(mOrderList.keyAt(position));
        holder.itemView.setTag(order);
        holder.itemView.setTag(R.id.orderId, mOrderList.keyAt(position));
        holder.orderId.setText(String.valueOf(order.getId()));
        holder.orderDate.setText(mParentActivity.getResources().getString(R.string.pastOrderDate, order.getOrderDate()));
        Spanned formattedOrderTotal = AppUtils.formattedStringResource(mParentActivity.getResources().getString(R.string.pastOrdersTotalPrice, AppUtils.formattedMoneyString(order.getTotalPrice())));
        holder.orderTotalPrice.setText(formattedOrderTotal);
        holder.itemView.setOnClickListener(mOnclickListener);
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_order_list_item, parent, false);
        return new PastOrdersRecyclerViewAdapter.OrderListViewHolder(productView);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
