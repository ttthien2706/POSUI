package com.smb_business_chain_management;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateStoreDialogFragment extends DialogFragment {
    private static final String TAG = CreateStoreDialogFragment.class.getSimpleName();
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.create_store_dialog, null);
        builder.setView(dialogView);

        Spinner districtSpinner = (Spinner) dialogView.findViewById(R.id.district);
        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(getContext(), R.array.districts, R.layout.support_simple_spinner_dropdown_item);
        districtAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);

        builder.setMessage("TẠO CỬA HÀNG")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "DEBUG-----------");
                Log.d(TAG, getActivity().toString());
                Log.d(TAG, dialogInterface.toString());

                TextInputEditText nameInput = (TextInputEditText) ((AlertDialog) dialogInterface).findViewById(R.id.Name);
                TextInputEditText addressInput = (TextInputEditText) ((AlertDialog) dialogInterface).findViewById(R.id.Address);
                SwitchCompat isActiveSwitch = ((AlertDialog) dialogInterface).findViewById(R.id.isActiveSwitch);

                boolean storeIsActive = isActiveSwitch.isChecked();
                String storeName = nameInput.getText().toString();
                String storeAddress = addressInput.getText().toString();

            }
        })
                .setNegativeButton("NEIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }
}
