package com.smb_business_chain_management.base;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.smb_business_chain_management.R;

public class BaseCustomerScreen extends BasePresentation {
    public BaseCustomerScreen(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_customer);
    }
}
