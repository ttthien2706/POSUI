package com.smb_business_chain_management.func_products;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.func_settings.listener_interface.ShopListenerInterface;
import com.smb_business_chain_management.models.Brand;
import com.smb_business_chain_management.models.Category;
import com.smb_business_chain_management.models.Measurement;
import com.smb_business_chain_management.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateProductDialogFragment extends DialogFragment{
    MenuItem THIS_ACTION_BAR_MENU_CONFIRM_BUTTON;
    boolean IS_VALIDATED_NAME = false, IS_VALIDATED_SKU = false, IS_VALIDATED_BARCODE = false, IS_VALIDATED_IMPORT_PRICE = false, IS_VALIDATED_WHOLESALE_PRICE = false, IS_VALIDATED_RETAIL_PRICE = false;
    SparseArray<Category> mCategories;
    SparseArray<Brand> mBrands;
    SparseArray<Measurement> mMeasurements;

    TextView nameInput;
    Spinner categorySpinner;
    Spinner brandSpinner;
    TextView skuInput;
    TextView barcodeInput;
    TextView quantityInput;
    TextView importPriceInput;
    TextView retailPriceInput;
    TextView wholesalePriceInput;
    Spinner unitInput;
    TextView descriptionInput;
    SwitchCompat isActiveSwitch;

    private ShopListenerInterface listener;


    MenuItem.OnMenuItemClickListener confirmButtonClicked = menuItem -> {
        String productName = nameInput.getText().toString();
        Category category = (Category) categorySpinner.getSelectedItem();
        Brand brand = (Brand) brandSpinner.getSelectedItem();
        String sku = skuInput.getText().toString();
        String barcode = barcodeInput.getText().toString();
        int quantity = Integer.parseInt(quantityInput.getText().toString());
        int importPrice = Integer.parseInt(importPriceInput.getText().toString());
        int retailPrice = Integer.parseInt(retailPriceInput.getText().toString());
        int wholesalePrice = Integer.parseInt(wholesalePriceInput.getText().toString());
        Measurement measurement = (Measurement) unitInput.getSelectedItem();
        String description = descriptionInput.getText().toString();
        boolean isActive = isActiveSwitch.isChecked();

        Product newProduct = new Product(category.getId(), brand.getId(), description, 0, false, false, productName, quantity, isActive, sku, barcode, importPrice, retailPrice, wholesalePrice, null, null);
        listener.RESTAddNewProduct(newProduct);
        return false;
    };

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ShopListenerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement CreateShopDialogListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_dialog_menu, menu);
        THIS_ACTION_BAR_MENU_CONFIRM_BUTTON = menu.findItem(R.id.confirm);
//        THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(false);
        menu.findItem(R.id.action_get_all).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setOnMenuItemClickListener(confirmButtonClicked);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_product_dialog, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        viewLookup(view);
        ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, asList(mCategories));
        ArrayAdapter<Brand> brandArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, asList(mBrands));
        ArrayAdapter<Measurement> measurementArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, asList(mMeasurements));

        categorySpinner.setAdapter(categoryArrayAdapter);
        brandSpinner.setAdapter(brandArrayAdapter);
        unitInput.setAdapter(measurementArrayAdapter);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle data = getArguments();
        setupArrays(data);
        super.onCreate(savedInstanceState);
    }

    private void setupArrays(Bundle data) {
        mCategories = data.getSparseParcelableArray(ProductDetailFragment.ARG_CATEGORY);
        mBrands = data.getSparseParcelableArray(ProductDetailFragment.ARG_BRAND);
        mMeasurements = data.getSparseParcelableArray(ProductDetailFragment.ARG_MEASUREMENT);
    }

    private void viewLookup(@NonNull View view) {
        nameInput = view.findViewById(R.id.nameInputText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        brandSpinner = view.findViewById(R.id.brandSpinner);
        skuInput = view.findViewById(R.id.skuInputText);
        barcodeInput = view.findViewById(R.id.barcodeInputText);
        quantityInput = view.findViewById(R.id.quantityInputText);
        importPriceInput = view.findViewById(R.id.importPriceInputText);
        retailPriceInput = view.findViewById(R.id.retailPriceInputText);
        wholesalePriceInput = view.findViewById(R.id.wholesalePriceInputText);
        isActiveSwitch = view.findViewById(R.id.isActiveSwitch);

        importPriceInput.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        retailPriceInput.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        wholesalePriceInput.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        quantityInput.setTransformationMethod(new NumericKeyBoardTransformationMethod());

        unitInput = view.findViewById(R.id.unitSpinner);
        descriptionInput = view.findViewById(R.id.descInputText);
    }

    protected void validateTextInput(TextView textView, String text, Boolean VALIDATE_FIELD) {
        if (TextUtils.isEmpty(text)) {
            VALIDATE_FIELD = false;
            textView.setError(((TextInputLayout) textView.getParent().getParent()).getHint() + getString(R.string.requiredTextIsEmpty));
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(isValidatedAll());
        } else {
            VALIDATE_FIELD = true;
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(isValidatedAll());
        }
    }

    private boolean isValidatedAll(){
        return IS_VALIDATED_NAME && IS_VALIDATED_SKU && IS_VALIDATED_BARCODE && IS_VALIDATED_IMPORT_PRICE && IS_VALIDATED_RETAIL_PRICE && IS_VALIDATED_WHOLESALE_PRICE;
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

}
