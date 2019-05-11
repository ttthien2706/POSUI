package com.smb_business_chain_management;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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

import com.smb_business_chain_management.Utils.TextValidator;
import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.District;
import com.smb_business_chain_management.models.Role;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;
import com.smb_business_chain_management.models.Ward;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends DialogFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_CURRENT_USER = "currentUser";
    private static final String TAG = UserDetailFragment.class.getSimpleName();
    private static boolean IS_NEW_EDIT = true;
    private static boolean IS_VALIDATED_ADDRESS = true;
    private static boolean IS_VALIDATED_NAME = true;

    private List<Store> mStoreList;
    private List<City> mCityList;
    private List<Role> mRoleList;
    private static int mOldStoreId;

    private TextInputEditText nameInput;
    private Spinner storeSpinner;
    private Spinner roleSpinner;
    private TextInputEditText addressInput;
    private Spinner wardSpinner;
    private Spinner districtSpinner;
    private Spinner citySpinner;
    private TextInputEditText phoneInput;

    private static MenuItem THIS_ACTION_BAR_MENU_CONFIRM_BUTTON;
    private ShopListenerInterface listener;

    private User mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

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
        if (mItem != null) {
            nameInput.setText(mItem.getName());
            addressInput.setText(mItem.getAddress());
            phoneInput.setText(mItem.getPhone());
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mStoreList = listener.getAllStores();
            mItem = getArguments().getParcelable(ARG_CURRENT_USER);
            mCityList = listener.getAllCities();
            mRoleList = listener.getAllRoles();
            mOldStoreId = Objects.requireNonNull(mItem).getShopId();

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getRoleName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);
        nameInput = rootView.findViewById(R.id.EmployeeName);
        storeSpinner = rootView.findViewById(R.id.store);
        roleSpinner = rootView.findViewById(R.id.role);
        addressInput = rootView.findViewById(R.id.Address);
        wardSpinner = rootView.findViewById(R.id.ward);
        districtSpinner = rootView.findViewById(R.id.district);
        citySpinner = rootView.findViewById(R.id.city);
        phoneInput = rootView.findViewById(R.id.Phone);

        List<District> curDistricts = new ArrayList(0);
        List<Ward> curWards = new ArrayList(0);

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, mCityList);
        ArrayAdapter<Store> storeAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, mStoreList);
        ArrayAdapter<District> districtAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curDistricts);
        ArrayAdapter<Ward> wardAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, curWards);
        ArrayAdapter<Role> roleAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, mRoleList);

        citySpinner.setAdapter(cityAdapter);
        storeSpinner.setAdapter(storeAdapter);
        districtSpinner.setAdapter(districtAdapter);
        wardSpinner.setAdapter(wardAdapter);
        roleSpinner.setAdapter(roleAdapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (!IS_NEW_EDIT) {
                    City curCity = (City) adapterView.getAdapter().getItem((int) id);
                    curDistricts.clear();
                    curDistricts.addAll(curCity.getDistricts());
                    districtAdapter.notifyDataSetChanged();

                    curWards.clear();
                    curWards.addAll(curDistricts.get(0).getWards());
                    wardAdapter.notifyDataSetChanged();
                } else {
                    City curCity = (City) adapterView.getAdapter().getItem(mItem.getCityId() - 1);
                    curDistricts.clear();
                    curDistricts.addAll(curCity.getDistricts());
                    districtAdapter.notifyDataSetChanged();

                    curWards.clear();
                    curWards.addAll(curDistricts.get(curDistricts.indexOf(curCity.findDistrictById((long) mItem.getDistrictId()))).getWards());
                    wardAdapter.notifyDataSetChanged();

                    citySpinner.setSelection(mItem.getCityId() - 1);
                    districtSpinner.setSelection(curDistricts.indexOf(curCity.findDistrictById((long) mItem.getDistrictId())));
                    wardSpinner.setSelection(curWards.indexOf(curCity.findDistrictById((long) mItem.getDistrictId()).findWardById((long) mItem.getWardId())));

                    IS_NEW_EDIT = false;
                }
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

        nameInput.setOnFocusChangeListener((view, hasFocus) -> {
            Log.d(TAG, "Sup");
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

        return rootView;
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

    MenuItem.OnMenuItemClickListener confirmButtonClicked = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            Fragment currentFragment = getActivity().getSupportFragmentManager().getFragments().get(0);

            int id = mItem.getId();
            String employeeName = nameInput.getText().toString();
            Store selectedStore = (Store) storeSpinner.getSelectedItem();
            Role selectedRole = (Role) roleSpinner.getSelectedItem();
            String address = addressInput.getText().toString();
            City selectedCity = (City) citySpinner.getSelectedItem();
            District selectedDistrict = (District) districtSpinner.getSelectedItem();
            Ward selectedWard = (Ward) wardSpinner.getSelectedItem();
            String phoneNumber = phoneInput.getText().toString();

            User newUser = new User(id, employeeName,phoneNumber,address,selectedRole.getId() ,selectedStore.getId(), selectedWard.getId(), selectedDistrict.getId(), selectedCity.getId(), selectedRole.getName());
            listener.RESTEditUser(newUser, mOldStoreId, currentFragment);
            return true;
        }
    };

}
