package com.smb_business_chain_management;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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

import com.smb_business_chain_management.model.Store;

import java.util.ArrayList;
import java.util.List;

public class CreateUserDialogFragment extends DialogFragment {
    private static final String TAG = CreateUserDialogFragment.class.getSimpleName();
    private ShopListenerInterface listener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.create_store_dialog, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.create_user_dialog, null);
        builder.setView(dialogView);

        List<Store> storeList = listener.getAllStores();
        List<String> allNames = new ArrayList<>(0);
        if(storeList != null){
            for (int index = 0; index < storeList.size(); ++index){
                allNames.add(storeList.get(index).getName());
            }
        }

        final Spinner storeSpinner = dialogView.findViewById(R.id.store);
        ArrayAdapter<String> storeAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, allNames);
        storeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);

        builder.setTitle("TẠO TÀI KHOẢN NHÂN VIÊN")
                .setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "DEBUG-----------");
                Log.d(TAG, getActivity().toString());
                Log.d(TAG, dialogInterface.toString());

                TextInputEditText usernameInput = ((AlertDialog) dialogInterface).findViewById(R.id.Username);
                TextInputEditText empNameInput = ((AlertDialog) dialogInterface).findViewById(R.id.EmployeeName);

                String storeName = usernameInput.getText().toString();
                String storeAddress = empNameInput.getText().toString();

                Store selectedStore = listener.findStoreByName(storeSpinner.getSelectedItem().toString());
                listener.addOneStaffMember(selectedStore);
            }
        })
                .setNegativeButton("HUỶ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        return builder.create();
    }

    public List<String> getStoreNameList(List<Store> listStore){
        List<String> allNames = new ArrayList<>(0);
        if(listStore != null){
            for (int i = 0; i < listStore.size(); ++i){
                allNames.add(listStore.get(i).getName());
            }
        }
        return allNames;
    }
}
