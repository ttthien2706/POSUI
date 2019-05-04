package com.smb_business_chain_management;

import com.smb_business_chain_management.model.Store;

import java.util.List;

public interface ShopListenerInterface {
    void RESTAddNewStore(String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId);
    void RESTEditStore(int id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId);
    void RESTDeleteStore(int id, int position);

    List<Store> getAllStores();

    Store findStoreByName(String storeName);
    Store findStoreById(int id);

    void addOneStaffMember(Store store);
}
