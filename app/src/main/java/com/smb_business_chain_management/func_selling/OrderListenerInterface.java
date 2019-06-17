package com.smb_business_chain_management.func_selling;

public interface OrderListenerInterface {
    void saveAndClearOrder();
    void paymentDialog();
    void completeOrderAndSubmit();
    void clearOrder();
    void showDoneDialog();
}
