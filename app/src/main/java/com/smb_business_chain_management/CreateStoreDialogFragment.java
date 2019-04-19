package com.smb_business_chain_management;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.smb_business_chain_management.model.Store;

public class CreateStoreDialogFragment extends DialogFragment {
    private static final String TAG = CreateStoreDialogFragment.class.getSimpleName();
    TextInputEditText nameInput;
    TextInputEditText addressInput;
    TextInputEditText phoneInput;
    SwitchCompat isActiveSwitch;
    private CreateShopDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CreateShopDialogListener) context;
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

        final Spinner districtSpinner = (Spinner) dialogView.findViewById(R.id.district);
        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(getContext(), R.array.districts, R.layout.support_simple_spinner_dropdown_item);
        districtAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);

        final Spinner wardSpinner = (Spinner) dialogView.findViewById(R.id.ward);
        ArrayAdapter<CharSequence> wardAdapter = ArrayAdapter.createFromResource(getContext(), R.array.districts, R.layout.support_simple_spinner_dropdown_item);
        wardAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        wardSpinner.setAdapter(wardAdapter);

        builder.setTitle("TẠO CỬA HÀNG")
                .setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "DEBUG-----------");
                        Log.d(TAG, getActivity().toString());
                        Log.d(TAG, dialogInterface.toString());

                        nameInput = (TextInputEditText) ((AlertDialog) dialogInterface).findViewById(R.id.Name);
                        addressInput = (TextInputEditText) ((AlertDialog) dialogInterface).findViewById(R.id.Address);
                        phoneInput = (TextInputEditText) ((AlertDialog) dialogInterface).findViewById(R.id.Phone);
                        isActiveSwitch = ((AlertDialog) dialogInterface).findViewById(R.id.isActiveSwitch);

                        boolean storeIsActive = isActiveSwitch.isChecked();
                        String storeName = nameInput.getText().toString();
                        String storeAddress = addressInput.getText().toString();
                        String storePhone = phoneInput.getText().toString();
                        String district = districtSpinner.getSelectedItem().toString();
                        String ward = wardSpinner.getSelectedItem().toString();
                        Boolean isActive = isActiveSwitch.isChecked();

                        listener.addNewShop(storeName, storePhone, storeAddress + ", " + ward + ", " + district, 0, isActive);
                    }
                })
                .setNegativeButton("HUỶ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }

    public interface CreateShopDialogListener {
        void addNewShop(String name, String phoneNumber, String address, Integer staffNumber, boolean isActive);
    }
}
