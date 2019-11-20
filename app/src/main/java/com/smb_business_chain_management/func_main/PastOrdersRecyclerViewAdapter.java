package com.smb_business_chain_management.func_main;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.func_main.fragments.PastOrderDetailFragment;
import com.smb_business_chain_management.func_selling.SellingActivity;
import com.smb_business_chain_management.models.Order;

import java.util.ArrayList;

public class PastOrdersRecyclerViewAdapter extends RecyclerView.Adapter<PastOrdersRecyclerViewAdapter.OrderListViewHolder> {
    protected int selectedPosition = -1;
    public static final String ARG_ORDER = "currentOrder";
    public static final String ARG_FILENAME = "fileName";

    SparseArray<Order> mOrderList;
    private MainActivity mParentActivity;
    private static final AppUtils appUtils;
    static {
        appUtils = new AppUtils();
    }
    public PastOrdersRecyclerViewAdapter(SparseArray<Order> orderList, Context context) {
        mOrderList = orderList;
        mParentActivity = (MainActivity) context;
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView removeButton;
        TextView orderId;
        TextView orderDate;
        TextView orderTotalPrice;
        ImageView selectedMarker;
        public OrderListViewHolder(View itemView) {
            super(itemView);
            removeButton = itemView.findViewById(R.id.removeSavedOrderButton);
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTotalPrice = itemView.findViewById(R.id.orderPrice);
            selectedMarker = itemView.findViewById(R.id.selectedMarker);
            itemView.setOnClickListener(this);

            removeButton.setOnClickListener(v -> {
                SparseArray<Order> debug = mOrderList;
                int position = getAdapterPosition();
                int key = mOrderList.keyAt(position);
                mParentActivity.deleteFile("order" + key);
                mParentActivity.updateSavedOrdersList();
                mOrderList.remove(key);
                notifyItemRemoved(position);
                notifyDataSetChanged();
//                notifyItemRangeChanged(position, mOrderList.size());
            });
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION){
                return;
            }

            notifyItemChanged(selectedPosition);
            selectedPosition = getAdapterPosition();
            notifyItemChanged(selectedPosition);

            Order currentOrder = (Order) view.getTag();
            Bundle arguments = new Bundle();
            arguments.putParcelable(ARG_ORDER, currentOrder);
            arguments.putParcelableArrayList("productList", (ArrayList<? extends Parcelable>) currentOrder.getProducts());
            arguments.putString(ARG_FILENAME, String.valueOf(view.getTag(R.id.orderId)));
            PastOrderDetailFragment fragment = new PastOrderDetailFragment();
            fragment.setArguments(arguments);
            mParentActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.pastOrderContent, fragment, PastOrderDetailFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int position) {
        holder.selectedMarker.setVisibility(selectedPosition == position ? View.VISIBLE : View.INVISIBLE);
        Order order = mOrderList.get(mOrderList.keyAt(position));
        holder.itemView.setTag(order);
        holder.itemView.setTag(R.id.orderId, mOrderList.keyAt(position));
        holder.orderId.setText(mParentActivity.getResources().getString(R.string.past_order_name, String.valueOf(position + 1)));
        holder.orderDate.setText(mParentActivity.getResources().getString(R.string.pastOrderDate, order.getOrderDate()));
        Spanned formattedOrderTotal = AppUtils.formatStringToHTMLSpanned(mParentActivity.getResources().getString(R.string.past_orders_total_price, AppUtils.formattedBigIntegerMoneyString(order.getTotalPrice())));
        holder.orderTotalPrice.setText(formattedOrderTotal);
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
