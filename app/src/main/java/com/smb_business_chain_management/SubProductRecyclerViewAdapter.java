package com.smb_business_chain_management;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.SubProduct;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class SubProductRecyclerViewAdapter extends RecyclerView.Adapter<SubProductRecyclerViewAdapter.SubProductViewHolder> {

    private final List<SubProduct> mSubProductList;
    private final ProductDetailFragment mParentFragment;

    public SubProductRecyclerViewAdapter(ProductDetailFragment parent, List<SubProduct> subProductList, FragmentManager fragmentManager) {
        mParentFragment = parent;
        mSubProductList = subProductList;
    }

    @NonNull
    @Override
    public SubProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View subProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_product_list_item, parent, false);
        return new SubProductViewHolder(subProductView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubProductViewHolder holder, int position) {
        holder.subProductNameTextView.setText(mSubProductList.get(position).getName());

        holder.subProductQuantityTextView.setText(String.format("%s %s", NumberFormat.getNumberInstance(Locale.US).format(mSubProductList.get(position).getQuantity()), mParentFragment.measurementName));
        holder.subProductRetailPriceTextView.setText(String.format("%s%s", mParentFragment.getResources().getString(R.string.product_detail_retail_label), NumberFormat.getNumberInstance(Locale.US).format(mSubProductList.get(position).getRetailPrice())));
        holder.subProductWholesaleTextView.setText(String.format("%s%s", mParentFragment.getResources().getString(R.string.product_detail_wholesale_label), NumberFormat.getNumberInstance(Locale.US).format(mSubProductList.get(position).getWholesalePrice())));
        holder.subProductImportTextView.setText(String.format("%s%s", mParentFragment.getResources().getString(R.string.product_detail_import_label), NumberFormat.getNumberInstance(Locale.US).format(mSubProductList.get(position).getImportPrice())));
    }

    @Override
    public int getItemCount() {
        return mSubProductList.size();
    }

    public static class SubProductViewHolder extends RecyclerView.ViewHolder {
        private TextView subProductNameTextView;
        private TextView subProductQuantityTextView;
        private TextView subProductRetailPriceTextView;
        private TextView subProductWholesaleTextView;
        private TextView subProductImportTextView;

        public SubProductViewHolder(View itemView) {
            super(itemView);
            viewLookup(itemView);
        }

        private void viewLookup(View view){
            subProductNameTextView = view.findViewById(R.id.subProductName);
            subProductQuantityTextView = view.findViewById(R.id.subProductQuantity);
            subProductRetailPriceTextView = view.findViewById(R.id.subProductRetailPrice);
            subProductWholesaleTextView = view.findViewById(R.id.subProductWholesalePrice);
            subProductImportTextView = view.findViewById(R.id.subProductImportPrice);
        }
    }
}
