package com.smb_business_chain_management.func_products;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.models.Store;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class ProductDetailStoresRecyclerViewAdapter extends RecyclerView.Adapter<ProductDetailStoresRecyclerViewAdapter.ProductDetailStoreViewHolder> {

    private final List<Store> mSubProductList;
    private final ProductDetailFragment mParentFragment;

    public ProductDetailStoresRecyclerViewAdapter(ProductDetailFragment parent, List<Store> storeList, FragmentManager fragmentManager) {
        mParentFragment = parent;
        mSubProductList = storeList;
    }

    @NonNull
    @Override
    public ProductDetailStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View subProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_product_list_item, parent, false);
        return new ProductDetailStoreViewHolder(subProductView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDetailStoreViewHolder holder, int position) {
        holder.subProductNameTextView.setText(mSubProductList.get(position).getName());

//        holder.subProductQuantityTextView.setText(String.format("%s %s", NumberFormat.getNumberInstance(Locale.FRENCH).format(mSubProductList.get(position).getQuantity()), mParentFragment.measurementName));
        holder.storeQuantityTextView.setText(String.format("%s %s", NumberFormat.getNumberInstance(Locale.FRENCH).format(mSubProductList.get(position).getQuantity()), mParentFragment.measurementName));
    }

    @Override
    public int getItemCount() {
        return mSubProductList.size();
    }

    public static class ProductDetailStoreViewHolder extends RecyclerView.ViewHolder {
        private TextView subProductNameTextView;
        private TextView subProductQuantityTextView;
        private TextView storeQuantityTextView;

        public ProductDetailStoreViewHolder(View itemView) {
            super(itemView);
            viewLookup(itemView);
        }

        private void viewLookup(View view){
            subProductNameTextView = view.findViewById(R.id.subProductName);
            subProductQuantityTextView = view.findViewById(R.id.subProductQuantity);
            storeQuantityTextView = view.findViewById(R.id.subProductWholesalePrice);
        }
    }
}
