package com.smb_business_chain_management;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreResponse {
    @SerializedName("id")
    long id;
    @SerializedName("name")
    String name;
    @SerializedName("results")
    private List<Store> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("address")
    String address;
    @SerializedName("phone")
    String phoneNumber;
//    @SerializedName()
    LatLng latLng;
//    @SerializedName()
    int activeStaff;
//    @SerializedName()
    boolean isActive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Store> getResults() {
        return results;
    }

    public void setResults(List<Store> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getActiveStaff() {
        return activeStaff;
    }

    public void setActiveStaff(int activeStaff) {
        this.activeStaff = activeStaff;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
