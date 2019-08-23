package com.smb_business_chain_management.func_products;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.models.Brand;
import com.smb_business_chain_management.models.Category;
import com.smb_business_chain_management.models.Measurement;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.Store;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "productId";
    public static final String ARG_CURRENT_PRODUCT = "product";
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_BRAND = "brand";
    public static final String ARG_MEASUREMENT = "measurement";

    private static final String TAG = ProductDetailFragment.class.getSimpleName();

    private static Product mItem;
    private static List<Store> mStoreList;
    protected String categoryName;
    protected String brandName;
    protected String measurementName;
    private ImageView productImage;
    private TextView nameTextView;
    private TextView retailPriceTextView;
    private TextView wholesalePriceTextView;
    private TextView importPriceTextView;
    private TextView inStockTextView;
    private TextView unitTextView;
    private TextView skuTextView;
    private TextView barcodeTextView;
    private TextView categoryTextView;
    private TextView brandTextView;
    private TextView statusTextView;
    private TextView descTextView;
    private RecyclerView storeRecyclerView;
    private RecyclerView.Adapter storeRecyclerViewAdapter;
    private TextView emptyView;

    public ProductDetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            getArgumentsAndPopulateSupportingArrays(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail, container, false);

        viewLookup(view);

        Glide.with(this).load(BusinessChainRESTClient.BASE_URL + "images/product/" + ((mItem.getPhotoPath() != null) ? mItem.getPhotoPath() : "default.jpg")).into(productImage);

        handleTextViews();
        handleSubProductRecyclerView();

        return view;
    }

    private void getArgumentsAndPopulateSupportingArrays(@NonNull Fragment fragment) {
        mItem = fragment.getArguments().getParcelable(ARG_CURRENT_PRODUCT);
        mStoreList = mItem.getStores();
        mStoreList.addAll(mItem.getStorehouses());

        SparseArray<Category> categories = fragment.getArguments().getSparseParcelableArray(ARG_CATEGORY);
        SparseArray<Brand> brands = fragment.getArguments().getSparseParcelableArray(ARG_BRAND);
        SparseArray<Measurement> measurements = fragment.getArguments().getSparseParcelableArray(ARG_MEASUREMENT);

        categoryName = mItem.getCategory();
        brandName = mItem.getBrand();
        measurementName = (measurements != null && measurements.size() > 0) ? measurements.get(mItem.getMeasurementId()).getName() : "N/A";
    }

    private void handleSubProductRecyclerView() {
        if (mStoreList == null || mStoreList.isEmpty()) {
            storeRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            storeRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            storeRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
            storeRecyclerView.setLayoutManager(rvLayoutManager);

            storeRecyclerViewAdapter = new ProductDetailStoresRecyclerViewAdapter(this, mStoreList, getFragmentManager());
            storeRecyclerView.setAdapter(storeRecyclerViewAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(storeRecyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);
            storeRecyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    private void handleTextViews() {
        nameTextView.setText(mItem.getName());
        inStockTextView.setText(NumberFormat.getNumberInstance(Locale.GERMANY).format(mItem.getQuantity()));
        skuTextView.setText(mItem.getSku());
        barcodeTextView.setText(mItem.getBarcode());
        descTextView.setText(mItem.getDescription());
        handlePrices();
        handleCategoryUnitAndBrand();
        handleStatus();
    }

    private void handleCategoryUnitAndBrand() {
        categoryTextView.setText(categoryName);
        brandTextView.setText(brandName);
//        unitTextView.setText(measurementName);
    }

    private void viewLookup(@NonNull View view) {
        productImage = view.findViewById(R.id.productImage);
        nameTextView = view.findViewById(R.id.productDetailName);
        retailPriceTextView = view.findViewById(R.id.productDetailRetailPrice);
//        wholesalePriceTextView = view.findViewById(R.id.productDetailWholesalePrice);
//        importPriceTextView = view.findViewById(R.id.productDetailImportPrice);
        inStockTextView = view.findViewById(R.id.productDetailInStock);
//        unitTextView = view.findViewById(R.id.productDetailUnit);
        skuTextView = view.findViewById(R.id.productDetailSKU);
        barcodeTextView = view.findViewById(R.id.productDetailBarcode);
        categoryTextView = view.findViewById(R.id.productDetailCategory);
        brandTextView = view.findViewById(R.id.productDetailBrand);
        statusTextView = view.findViewById(R.id.productDetailStatus);
        descTextView = view.findViewById(R.id.productDesc);
        emptyView = view.findViewById(R.id.subProductEmpty);

        storeRecyclerView = view.findViewById(R.id.subProductListView);
    }

    private void handlePrices() {
        retailPriceTextView.setText(NumberFormat.getNumberInstance(Locale.GERMANY).format(mItem.getRetailPrice()));
    }

    private void handleStatus() {
        if (mItem.isActive()) {
            statusTextView.setText(R.string.product_isActive_true);
        } else statusTextView.setText(R.string.product_isActive_false);
    }
}
