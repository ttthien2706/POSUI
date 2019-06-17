package com.smb_business_chain_management.func_settings.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.TextValidator;
import com.smb_business_chain_management.func_settings.listener_interface.ShopListenerInterface;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.District;
import com.smb_business_chain_management.models.Role;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;
import com.smb_business_chain_management.models.Ward;

import java.util.ArrayList;
import java.util.List;

public class CreateUserDialogFragment extends DialogFragment {
    private static final String TAG = CreateUserDialogFragment.class.getSimpleName();
    private ShopListenerInterface listener;
    protected static final AppUtils appUtils = new AppUtils();
    protected List<City> mCityList;
    protected List<Role> mRoleList;

    TextInputEditText nameInput;
    TextInputEditText usernameInput;
    TextInputEditText addressInput;
    TextInputEditText phoneInput;
    Spinner citySpinner;
    Spinner districtSpinner;
    Spinner wardSpinner;
    Spinner storeSpinner;
    Spinner roleSpinner;

    boolean IS_VALIDATED_NAME = false, IS_VALIDATED_ADDRESS = false;
    MenuItem THIS_ACTION_BAR_MENU_CONFIRM_BUTTON;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ShopListenerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement CreateUserDialogListener");
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.create_user_dialog, container, false);

        citySpinner = dialog.findViewById(R.id.city);
        districtSpinner = dialog.findViewById(R.id.district);
        wardSpinner = dialog.findViewById(R.id.ward);
        storeSpinner = dialog.findViewById(R.id.store);
        roleSpinner = dialog.findViewById(R.id.role);

        List<District> curDistricts = new ArrayList(0);
        List<Ward> curWards = new ArrayList(0);
        List<Store> storeList = listener.getAllStores();

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, mCityList);
        ArrayAdapter<District> districtAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curDistricts);
        ArrayAdapter<Ward> wardAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curWards);
        ArrayAdapter<Store> storeAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, storeList);
        ArrayAdapter<Role> roleAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, mRoleList);

        citySpinner.setAdapter(cityAdapter);
        districtSpinner.setAdapter(districtAdapter);
        wardSpinner.setAdapter(wardAdapter);
        storeSpinner.setAdapter(storeAdapter);
        roleSpinner.setAdapter(roleAdapter);

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

        nameInput = dialog.findViewById(R.id.EmployeeName);
        usernameInput = dialog.findViewById(R.id.Username);
        addressInput = dialog.findViewById(R.id.Address);
        phoneInput = dialog.findViewById(R.id.Phone);
        nameInput.setOnFocusChangeListener((view, hasFocus) -> {
            TextValidator validator = new TextValidator((TextView)view) {
                @Override
                public void validate(TextView textView, String text) {
                    validateName(textView, text);
                }
            };
            validator.validate((TextView)view, ((TextView) view).getText().toString());
        });
        nameInput.addTextChangedListener(new TextValidator(nameInput) {
            @Override
            public void validate(TextView textView, String text) {
                validateName(textView, text);
            }
        });
        return dialog;
    }

    protected void validateName(TextView textView, String text){
        if (TextUtils.isEmpty(text)) {
            textView.setError(((TextInputLayout) textView.getParent().getParent()).getHint() + getString(R.string.requiredTextIsEmpty));
            IS_VALIDATED_NAME = false;
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_NAME);
        } else if (text.length() >= 50) {
            IS_VALIDATED_NAME = false;
            textView.setError(getString(R.string.storeNameMaximumSizeExceeded));
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_NAME);
        }
        else {
            IS_VALIDATED_NAME = true;
            THIS_ACTION_BAR_MENU_CONFIRM_BUTTON.setEnabled(IS_VALIDATED_NAME);
        }
    }
    MenuItem.OnMenuItemClickListener confirmButtonClicked = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            String username = usernameInput.getText().toString();
            String employeeName = nameInput.getText().toString();
            Store selectedStore = (Store) storeSpinner.getSelectedItem();
            Role selectedRole = (Role) roleSpinner.getSelectedItem();
            String address = addressInput.getText().toString();
            City selectedCity = (City) citySpinner.getSelectedItem();
            District selectedDistrict = (District) districtSpinner.getSelectedItem();
            Ward selectedWard = (Ward) wardSpinner.getSelectedItem();
            String phoneNumber = phoneInput.getText().toString();

            User newUser = new User(employeeName,phoneNumber,address,selectedRole.getId() ,selectedStore.getId(), selectedWard.getId(), selectedDistrict.getId(), selectedCity.getId(), selectedRole.getName());
            listener.RESTAddNewUser(newUser);
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle data = getArguments();
        mCityList = data.getParcelableArrayList("cities");
        mRoleList = data.getParcelableArrayList("roles");
        super.onCreate(savedInstanceState);
    }
}
