package com.smb_business_chain_management.func_selling;

public interface OrderListenerInterface {
    void saveAndClearOrder();
    void paymentDialog();
    void completeOrderAndSubmit(String received, String change, PaymentDialog dismissFragment);
    void clearOrder();
    void showDoneDialog(String received, String change);
}
