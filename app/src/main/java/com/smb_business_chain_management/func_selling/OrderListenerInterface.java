package com.smb_business_chain_management.func_selling;

import android.widget.TextView;

public interface OrderListenerInterface {
    void saveAndClearOrder();
    void paymentDialog();
    void completeOrderAndSubmit(String received, String change, TextView changeAmountTextView, String paymentType);
    void clearOrder();
    void showDoneDialog(String paymentType, String received, String change);
}
