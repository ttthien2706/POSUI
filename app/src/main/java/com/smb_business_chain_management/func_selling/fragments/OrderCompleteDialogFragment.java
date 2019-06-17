package com.smb_business_chain_management.func_selling.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.func_selling.OrderListenerInterface;

public class OrderCompleteDialogFragment extends DialogFragment {

    private OrderListenerInterface listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OrderListenerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement OrderListenerInterface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.order_complete_dialog_title)
                .setMessage(R.string.order_complete_message)
                .setPositiveButton("THANH TOÁN", (dialogInterface, i) -> {
                    //TODO: call REST to submit order
                    listener.paymentDialog();
                })
                .setNegativeButton("LƯU ĐƠN", (dialogInterface, i) -> {
                    //TODO: call REST to save order
                    listener.saveAndClearOrder();
                });
        return builder.create();
    }

    @Override
    public void onResume() {
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height_short);
        getDialog().getWindow().setLayout(width, height);
        super.onResume();
    }
}