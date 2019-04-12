package com.smb_business_chain_management;

public class Store {
    private long storeId;
    private String storeName;
    private String storePhoneNumber;
    private String storeAddress;
    private Integer storeActiveStaff;
    private boolean storeActive;

    public Store(){

    }

    public Store(long id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive){
        storeId = id;
        storeName = name;
        storePhoneNumber = phoneNumber;
        storeAddress = address;
        storeActiveStaff = staffNumber;
        storeActive = isActive;
    }

    public long getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStorePhoneNumber() {
        return storePhoneNumber;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public Integer getStoreActiveStaff() {
        return storeActiveStaff;
    }

    public boolean isStoreActive() {
        return storeActive;
    }
}
