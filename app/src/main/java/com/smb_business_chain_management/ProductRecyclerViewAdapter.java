package com.smb_business_chain_management;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smb_business_chain_management.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductsViewHolder> {
    private static final String TAG = ProductRecyclerViewAdapter.class.getSimpleName();

    private static List<Product> mProductList;
    private final ProductActivity mParentActivity;
    private static boolean EMPTY_VIEW = false;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Product mCurrentProduct = (Product) view.getTag();
            Bundle arguments = new Bundle();
            arguments.putInt(ProductDetailFragment.ARG_ITEM_ID, mCurrentProduct.getId());
            arguments.putParcelable(ProductDetailFragment.ARG_CURRENT_PRODUCT, mCurrentProduct);
            arguments.putSparseParcelableArray("category", mParentActivity.mDataUtils.mCategoryMap);
            arguments.putSparseParcelableArray("brand", mParentActivity.mDataUtils.mBranTheWheelyWheelyLegsNoFreely);
            arguments.putSparseParcelableArray("measurement", mParentActivity.mDataUtils.mMeasurementMap);


            ProductDetailFragment fragment = new ProductDetailFragment();
            fragment.setArguments(arguments);
            ((TextView)mParentActivity.findViewById(R.id.productDetailTitle)).setText("");
            ((TextView)mParentActivity.findViewById(R.id.productDetailHint)).setText("");
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.productDetailContent, fragment)
                    .commit();
        }
    };

    ProductRecyclerViewAdapter(ProductActivity parent, List<Product> productList, FragmentManager fragmentManager) {
        mParentActivity = parent;
        mProductList = productList;
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productDetail;
        public ProductsViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productDetail = itemView.findViewById(R.id.productDetail);
        }
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        return new ProductsViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        getProductDetails(mProductList.get(position).getId(), holder);

        holder.productName.setText(mProductList.get(position).getName());
        holder.productDetail.setText(mProductList.get(position).getDetails());

        holder.itemView.setOnClickListener(mOnClickListener);
    }

    protected boolean isProductListEmpty(){
        if (mProductList.isEmpty()) return true;
        return false;
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public void getProductDetails(int productId, ProductsViewHolder holder){
        Call<Product> call = mParentActivity.businessChainRESTService.getProductDetails(productId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200){
                    assert response.body() != null;
                    Log.d(TAG, response.body().toString());
                    holder.itemView.setTag(new Product(response.body()));
                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
}
