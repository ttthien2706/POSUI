package com.smb_business_chain_management.func_selling;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.models.Product;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class OrderListRecyclerViewAdapter extends RecyclerView.Adapter<OrderListRecyclerViewAdapter.OrderListViewHolder>{
    private static final String TAG = OrderListRecyclerViewAdapter.class.getSimpleName();
    private Context context;
    private List<Product> mOrderList;
    private TextView mOrderTotalText;
    private TextView mOrderTitle;
    private AlphaAnimation fadeOut;
    private CustomerScreen customerScreen;

    OrderListRecyclerViewAdapter(List<Product> orderList, TextView totalPriceText, TextView titleText, CustomerScreen customerScreen) {
        mOrderList = orderList;
        mOrderTotalText = totalPriceText;
        mOrderTitle = titleText;
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(200);
        context = totalPriceText.getContext();
        this.customerScreen = customerScreen;
    }


    public class OrderListViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        TextView retailPriceTextView;
        TextView totalPriceTextView;
        TextView quantityText;
        Button incrementButton;
        Button decrementButton;
        ImageView removeButton;

        public OrderListViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            retailPriceTextView = itemView.findViewById(R.id.productRetailPrice);
            totalPriceTextView = itemView.findViewById(R.id.subTotalPrice);
            quantityText = itemView.findViewById(R.id.subQuantityText);
            removeButton = itemView.findViewById(R.id.removeButton);
            incrementButton = itemView.findViewById(R.id.increment);
            decrementButton = itemView.findViewById(R.id.decrement);
            incrementButton.setOnClickListener(view -> {
                Log.d(TAG, "increase");
                if (Integer.parseInt(quantityText.getText().toString())+1 <= mOrderList.get(getAdapterPosition()).getLimQuantity()) {
                    quantityText.setText(String.valueOf(Integer.parseInt(quantityText.getText().toString())+1));
                    mOrderList.get(getAdapterPosition()).setQuantity(Integer.parseInt(quantityText.getText().toString()));
                    mOrderTitle.setText(context.getString(R.string.order_title_label, ((SellingActivity) context).getTotalQuantity()));
                    totalPriceTextView.setText(NumberFormat.getNumberInstance(Locale.GERMANY).format(calculateSubTotalPrice(mOrderList.get(getAdapterPosition()).getRetailPrice(), mOrderList.get(getAdapterPosition()).getQuantity())));
                    customerScreen.orderListCustomerRecyclerViewAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                    customerScreen.orderTitleTextView.setText(context.getString(R.string.order_title_label, ((SellingActivity)context).getTotalQuantity()));
                    customerScreen.orderTotalTextView.setText(Html.fromHtml(context.getString(R.string.order_total, ((SellingActivity) context).calculateOrderTotalPrice()),Html.FROM_HTML_MODE_LEGACY));
                    mOrderTotalText.setText(Html.fromHtml(context.getString(R.string.order_total, ((SellingActivity) context).calculateOrderTotalPrice()),Html.FROM_HTML_MODE_LEGACY));
//                    ((SellingActivity) (context)).IOrderTotalListener.getOrderTotal(((SellingActivity) context).calculateOrderTotalPriceNumber());
                } else {
                    Toast.makeText(itemView.getContext(), "Hàng trong kho không đủ!", Toast.LENGTH_LONG).show();
                }
            });
            decrementButton.setOnClickListener(view -> {
                Log.d(TAG, "decrease");
                if (Integer.parseInt(quantityText.getText().toString())-1 > 0){
                    quantityText.setText(String.valueOf(Integer.parseInt(quantityText.getText().toString())-1));
                    mOrderList.get(getAdapterPosition()).setQuantity(Integer.parseInt(quantityText.getText().toString()));
                    mOrderTitle.setText(context.getString(R.string.order_title_label, ((SellingActivity) context).getTotalQuantity()));
                    totalPriceTextView.setText(NumberFormat.getNumberInstance(Locale.GERMANY).format(calculateSubTotalPrice(mOrderList.get(getAdapterPosition()).getRetailPrice(), mOrderList.get(getAdapterPosition()).getQuantity())));
                    customerScreen.orderListCustomerRecyclerViewAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                    customerScreen.orderTitleTextView.setText(context.getString(R.string.order_title_label, ((SellingActivity)context).getTotalQuantity()));
                    customerScreen.orderTotalTextView.setText(Html.fromHtml(context.getString(R.string.order_total, ((SellingActivity) context).calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
                    mOrderTotalText.setText(Html.fromHtml(context.getString(R.string.order_total, ((SellingActivity) context).calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
//                    ((SellingActivity) (context)).IOrderTotalListener.getOrderTotal(((SellingActivity) context).calculateOrderTotalPriceNumber());
                }
                else removeButton.performClick();
            });
            removeButton.setOnClickListener(view -> {
                int position = getAdapterPosition();
                mOrderList.remove(position);
                mOrderTitle.setText(context.getString(R.string.order_title_label, ((SellingActivity) context).getTotalQuantity()));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mOrderList.size());
                customerScreen.mOrderListCustomer.remove(position);
                if (customerScreen.mOrderListCustomer.size() == 0) customerScreen.viewFlipper.showPrevious();
                customerScreen.orderTitleTextView.setText(context.getString(R.string.order_title_label, ((SellingActivity)context).getTotalQuantity()));
                customerScreen.orderListCustomerRecyclerViewAdapter.notifyItemRemoved(position);
                customerScreen.orderListCustomerRecyclerViewAdapter.notifyDataSetChanged();
                customerScreen.orderTotalTextView.setText(Html.fromHtml(context.getString(R.string.order_total, ((SellingActivity) context).calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
                mOrderTotalText.setText(Html.fromHtml(context.getString(R.string.order_total, ((SellingActivity) context).calculateOrderTotalPrice()), Html.FROM_HTML_MODE_LEGACY));
//                ((SellingActivity) (context)).IOrderTotalListener.getOrderTotal(((SellingActivity) context).calculateOrderTotalPriceNumber());
            });
        }
    }

    @NonNull
    @Override
    public OrderListRecyclerViewAdapter.OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_product_list_item, parent, false);
        return new OrderListViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListRecyclerViewAdapter.OrderListViewHolder holder, int position) {
        holder.nameTextView.setText(mOrderList.get(position).getName());
        holder.retailPriceTextView.setText(NumberFormat.getNumberInstance(Locale.GERMANY).format(mOrderList.get(position).getRetailPrice()));
        holder.quantityText.setText(String.valueOf(mOrderList.get(position).getQuantity()));
        holder.totalPriceTextView.setText(NumberFormat.getNumberInstance(Locale.GERMANY).format(calculateSubTotalPrice(mOrderList.get(position).getRetailPrice(), mOrderList.get(position).getQuantity())));
        holder.quantityText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        holder.quantityText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
    }

    private BigInteger calculateSubTotalPrice(int retailPrice, int quantity){
        return BigInteger.valueOf((long)retailPrice*quantity);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

}
