package com.smb_business_chain_management.func_settings.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.smb_business_chain_management.func_settings.listener_interface.ShopListenerInterface;

public class ConfirmDeleteDialogFragment extends DialogFragment {
    private ShopListenerInterface listener;

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int id = getArguments().getInt("id");
        int position = getArguments().getInt("position");
        builder.setTitle("XOÁ CỬA HÀNG")
                .setMessage("Bạn có chắc chắn xoá cửa hàng này?")
                .setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.RESTDeleteStore(id, position);
                    }
                })
        .setNegativeButton("HUỶ", (dialogInterface, i) -> {    });
        return builder.create();
    }
}
