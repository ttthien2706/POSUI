package com.smb_business_chain_management.func_products;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.func_login.SaveSharedPreference;
import com.smb_business_chain_management.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductsViewHolder> {
    private static final String TAG = ProductRecyclerViewAdapter.class.getSimpleName();

    private static List<Product> mProductList;
    private final ProductActivity mParentActivity;

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
        private TextView productSKU;
        public ProductsViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productDetail = itemView.findViewById(R.id.productRetailPrice);
            productSKU = itemView.findViewById(R.id.productSKU);
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
        holder.productSKU.setText(mProductList.get(position).getSku());

        holder.itemView.setOnClickListener(mOnClickListener);
    }

    protected boolean isProductListEmpty(){
        return mProductList.isEmpty();
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    private void getProductDetails(int productId, ProductsViewHolder holder){
        Call<Product> call = mParentActivity.businessChainRESTService.GetProductByIdApi(Integer.parseInt(SaveSharedPreference.getChainId(mParentActivity.getApplicationContext())), Integer.parseInt(SaveSharedPreference.getChainId(mParentActivity.getApplicationContext())), productId);
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
            public void onFailure(Call<Product> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                throwable.printStackTrace();
            }
        });
    }
}
