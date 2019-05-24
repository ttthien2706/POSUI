package com.smb_business_chain_management;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.TextValidator;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.District;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.Ward;

import java.util.ArrayList;
import java.util.List;

public class CreateStoreDialogFragment extends DialogFragment {
    private static final String TAG = CreateStoreDialogFragment.class.getSimpleName();
    protected List<City> mCityList;
    AppUtils utils = new AppUtils();
    TextInputEditText nameInput;
    TextInputEditText addressInput;
    TextInputEditText phoneInput;
    SwitchCompat isActiveSwitch;
    Spinner citySpinner;
    Spinner districtSpinner;
    Spinner wardSpinner;
    MenuItem THIS_ACTION_BAR_MENU_CONFIRM_BUTTON;
    boolean IS_VALIDATED_NAME = false, IS_VALIDATED_ADDRESS = false;
    private ShopListenerInterface listener;
    MenuItem.OnMenuItemClickListener confirmButtonClicked = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            String storeName = nameInput.getText().toString();
            String storeAddress = addressInput.getText().toString();
            String storePhone = phoneInput.getText().toString();

            City city = (City) citySpinner.getSelectedItem();
            District district = (District) districtSpinner.getSelectedItem();
            Ward ward = (Ward) wardSpinner.getSelectedItem();

            boolean storeIsActive = isActiveSwitch.isChecked();

            String fullAddress = storeAddress + ", P. " + ward.getName() + ", Q. " + district.getName() + ", TP. " + city.getName();

            Store newStore = new Store(storeName, storePhone, storeAddress, 0, storeIsActive, city.getId(), district.getId(), ward.getId(), fullAddress);

            listener.RESTAddNewStore(newStore);

            return false;
        }
    };

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
        THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(false);
        THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setOnMenuItemClickListener(confirmButtonClicked);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.create_store_dialog, container, false);

        citySpinner = dialog.findViewById(R.id.city);
        districtSpinner = dialog.findViewById(R.id.district);
        wardSpinner = dialog.findViewById(R.id.ward);

        List<District> curDistricts = new ArrayList(0);
        List<Ward> curWards = new ArrayList(0);

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, mCityList);
        ArrayAdapter<District> districtAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curDistricts);
        ArrayAdapter<Ward> wardAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curWards);

        citySpinner.setAdapter(cityAdapter);
        districtSpinner.setAdapter(districtAdapter);
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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        nameInput = dialog.findViewById(R.id.Name);
        addressInput = dialog.findViewById(R.id.Address);
        phoneInput = dialog.findViewById(R.id.Phone);
        isActiveSwitch = dialog.findViewById(R.id.isActiveSwitch);

        nameInput.setOnFocusChangeListener((view, hasFocus) -> {
            TextValidator validator = new TextValidator((TextView) view) {
                @Override
                public void validate(TextView textView, String text) {
                    validateName(textView, text);
                }
            };
            validator.validate((TextView) view, ((TextView) view).getText().toString());
        });
        nameInput.addTextChangedListener(new TextValidator(nameInput) {
            @Override
            public void validate(TextView textView, String text) {
                validateName(textView, text);
            }
        });
        addressInput.setOnFocusChangeListener((view, b) -> {
            TextValidator validator = new TextValidator((TextView) view) {
                @Override
                public void validate(TextView textView, String text) {
                    validateAddress(textView, text);
                }
            };
            validator.validate((TextView) view, ((TextView) view).getText().toString());
        });
        addressInput.addTextChangedListener(new TextValidator(addressInput) {
            @Override
            public void validate(TextView textView, String text) {
                validateAddress(textView, text);
            }
        });

        return dialog;
    }

    protected void validateName(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setError(((TextInputLayout) textView.getParent().getParent()).getHint() + getString(R.string.requiredTextIsEmpty));
            IS_VALIDATED_NAME = false;
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        } else if (text.length() >= 50) {
            IS_VALIDATED_NAME = false;
            textView.setError(getString(R.string.storeNameMaximumSizeExceeded));
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        } else {
            IS_VALIDATED_NAME = true;
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        }
    }

    protected void validateAddress(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            IS_VALIDATED_ADDRESS = false;
            textView.setError(((TextInputLayout) textView.getParent().getParent()).getHint() + getString(R.string.requiredTextIsEmpty));
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        } else {
            IS_VALIDATED_ADDRESS = true;
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_ADDRESS && IS_VALIDATED_NAME);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle data = getArguments();
        mCityList = data.getParcelableArrayList("cities");
        super.onCreate(savedInstanceState);
    }
}
