package com.smb_business_chain_management;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.TextValidator;
import com.smb_business_chain_management.model.City;
import com.smb_business_chain_management.model.District;
import com.smb_business_chain_management.model.Ward;

import java.util.ArrayList;
import java.util.List;

public class CreateStoreDialogFragment extends DialogFragment {
    private static final String TAG = CreateStoreDialogFragment.class.getSimpleName();
    TextInputEditText nameInput;
    TextInputEditText addressInput;
    TextInputEditText phoneInput;
    SwitchCompat isActiveSwitch;
    AppUtils utils = new AppUtils();
    private ShopListenerInterface listener;

    boolean IS_VALIDATED_NAME = false, IS_VALIDATED_ADDRESS = false;

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
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.create_store_dialog, null);
        builder.setView(dialogView);

        final Spinner citySpinner = dialogView.findViewById(R.id.city);
        final Spinner districtSpinner = dialogView.findViewById(R.id.district);
        final Spinner wardSpinner = dialogView.findViewById(R.id.ward);

        List<City> allCities = new ArrayList(0);
        List<District> curDistricts = new ArrayList(0);
        List<Ward> curWards = new ArrayList(0);

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, allCities);
        utils.fetchAllAdministrativeUnits(allCities, cityAdapter);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<District> districtAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curDistricts);
        districtSpinner.setAdapter(districtAdapter);

        ArrayAdapter<Ward> wardAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curWards);
        wardSpinner.setAdapter(wardAdapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                City curCity = (City) adapterView.getAdapter().getItem((int) id);
                curDistricts.clear();
                curDistricts.addAll(curCity.getDistricts());
                districtAdapter.notifyDataSetChanged();

                curWards.clear();
                curWards.addAll(curDistricts.get(0).getWards());
                wardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                District curDistrict = (District) adapterView.getAdapter().getItem((int) id);

                curWards.clear();
                curWards.addAll(curDistrict.getWards());
                wardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setTitle("TẠO CỬA HÀNG")
                .setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        nameInput = ((AlertDialog) dialogInterface).findViewById(R.id.Name);
//                        addressInput = ((AlertDialog) dialogInterface).findViewById(R.id.Address);
                        phoneInput = ((AlertDialog) dialogInterface).findViewById(R.id.Phone);
                        isActiveSwitch = ((AlertDialog) dialogInterface).findViewById(R.id.isActiveSwitch);

                        String storeName = nameInput.getText().toString();
                        String storeAddress = addressInput.getText().toString();
                        String storePhone = phoneInput.getText().toString();

                        String city = citySpinner.getSelectedItem().toString();
                        String district = districtSpinner.getSelectedItem().toString();
                        String ward = wardSpinner.getSelectedItem().toString();

                        Long cityId = allCities.get((int) citySpinner.getSelectedItemId()).getId();
                        Long districtId = curDistricts.get((int) districtSpinner.getSelectedItemId()).getId();
                        Long wardId = curWards.get((int) wardSpinner.getSelectedItemId()).getId();

                        boolean storeIsActive = isActiveSwitch.isChecked();

                        listener.RESTAddNewStore(storeName, storePhone, storeAddress, 0, storeIsActive, cityId, districtId, wardId);
                    }
                })
                .setNegativeButton("HUỶ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();

        nameInput = dialogView.findViewById(R.id.Name);
        addressInput = dialogView.findViewById(R.id.Address);
//        phoneInput = dialogView.findViewById(R.id.Phone);
        nameInput.setOnFocusChangeListener((view, hasFocus) -> {
            TextValidator validator = new TextValidator((TextView)view) {
                @Override
                public void validate(TextView textView, String text) {
                    validateName(dialog, textView, text);
                }
            };
            validator.validate((TextView)view, ((TextView) view).getText().toString());
        });
        nameInput.addTextChangedListener(new TextValidator(nameInput) {
            @Override
            public void validate(TextView textView, String text) {
                validateName(dialog, textView, text);
            }
        });

        addressInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                TextValidator validator = new TextValidator((TextView)view) {
                    @Override
                    public void validate(TextView textView, String text) {
                        validateAddress(dialog, textView, text);
                    }
                };
                validator.validate((TextView)view, ((TextView) view).getText().toString());
            }
        });
        addressInput.addTextChangedListener(new TextValidator(addressInput) {
            @Override
            public void validate(TextView textView, String text) {
                validateAddress(dialog, textView, text);
            }
        });

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false));

        return dialog;
    }

    protected void validateName(AlertDialog dialog, TextView textView, String text){
        if (TextUtils.isEmpty(text)) {
            textView.setError(((TextInputLayout) textView.getParent().getParent()).getHint() + getString(R.string.requiredTextIsEmpty));
            IS_VALIDATED_NAME = false;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        } else if (text.length() >= 50) {
            IS_VALIDATED_NAME = false;
            textView.setError(getString(R.string.storeNameMaximumSizeExceeded));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        }
        else {
            IS_VALIDATED_NAME = true;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        }
    }
    protected void validateAddress(AlertDialog dialog, TextView textView, String text){
        if (TextUtils.isEmpty(text)) {
            IS_VALIDATED_ADDRESS = false;
            textView.setError(((TextInputLayout) textView.getParent().getParent()).getHint() + getString(R.string.requiredTextIsEmpty));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        }
        else {
            IS_VALIDATED_ADDRESS = true;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        }
    }

}
