package com.smb_business_chain_management.func_main;

import com.smb_business_chain_management.models.Product;

import java.util.ArrayList;

public interface PastOrderListener {
    void continueOrder(ArrayList<Product> order, String fileName);
}
