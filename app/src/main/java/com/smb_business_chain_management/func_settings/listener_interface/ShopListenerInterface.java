package com.smb_business_chain_management.func_settings.listener_interface;

import android.support.v4.app.Fragment;

import com.smb_business_chain_management.models.City;
import com.smb_business_chain_management.models.Product;
import com.smb_business_chain_management.models.Role;
import com.smb_business_chain_management.models.Store;
import com.smb_business_chain_management.models.User;

import java.util.List;

public interface ShopListenerInterface {
    void RESTAddNewStore(Store store);
    void RESTEditStore(int id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, int cityId, int districtId, int wardId);
    void RESTDeleteStore(int id, int position);

    void RESTAddNewUser(User user);
    void RESTEditUser(User user, int storeId, Fragment currentFragment);

    void RESTAddNewProduct(Product product);

    List<Store> getAllStores();
    List<City> getAllCities();
    List<Role> getAllRoles();

    Store findStoreByName(String storeName);
    Store findStoreById(int id);
    Store getSelectedStore();

    void addOneStaffMember(Store store);
}
