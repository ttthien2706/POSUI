package com.smb_business_chain_management.func_selling;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.func_products.ProductRecyclerViewAdapter;
import com.smb_business_chain_management.models.Product;

import java.util.List;

class OrderProductRecyclerViewAdapter extends RecyclerView.Adapter<OrderProductRecyclerViewAdapter.OrderProductViewHolder> {
    private static final String TAG = ProductRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private static List<Product> mProductList;
    private static RecyclerViewClickListener mClickListener;
    public static ColorStateList mTextColor;

    OrderProductRecyclerViewAdapter(List<Product> productList, RecyclerViewClickListener itemListener, Context context) {
        mContext = context;
        mProductList = productList;
        mClickListener = itemListener;
    }

    public static class OrderProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private  ImageView productImage;
        private TextView productName;
        private TextView productBarcode;
        private CardView container;
        public OrderProductViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.orderProductCardContainer);
            productImage = itemView.findViewById(R.id.orderProductImage);
            productName = itemView.findViewById(R.id.orderProductName);
            productBarcode = itemView.findViewById(R.id.orderProductBarcode);
            itemView.setOnClickListener(this);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            container.setCardBackgroundColor(container.getContext().getResources().getColor(R.color.colorPrimaryDark, container.getContext().getTheme()));
//            productName.setTextColor(container.getContext().getResources().getColor(R.color.white, container.getContext().getTheme()));
//            productBarcode.setTextColor(container.getContext().getResources().getColor(R.color.white, container.getContext().getTheme()));
            mClickListener.recyclerViewListClickListener(view, this.getLayoutPosition(), mTextColor);
        }
    }

    @NonNull
    @Override
    public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_grid_item, parent, false);
        return new OrderProductViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        holder.productName.setText(mProductList.get(position).getName());
        holder.productBarcode.setText(mProductList.get(position).getBarcode());
        Glide.with(mContext).load(BusinessChainRESTClient.BASE_URL+"/images/product/"+((mProductList.get(position).getPhotoPath() != null) ? mProductList.get(position).getPhotoPath() : "default.jpg")).into(holder.productImage);
        mTextColor = holder.productName.getTextColors();
    }
    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
