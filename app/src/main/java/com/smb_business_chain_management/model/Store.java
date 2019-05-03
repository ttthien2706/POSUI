package com.smb_business_chain_management.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Store {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("amountUser")
    @Expose
    private Integer amountUser;
    @SerializedName("cityId")
    @Expose
    private Long cityId;
    @SerializedName("districtId")
    @Expose
    private Long districtId;
    @SerializedName("wardId")
    @Expose
    private Long wardId;

    public Store(String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId){
        this.name = name;
        this.phone = phoneNumber;
        this.address = address;
        this.amountUser = staffNumber;
        this.isActive = isActive;
        this.cityId = cityId;
        this.districtId = districtId;
        this.wardId = wardId;
    }

    public Store(long id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId){
        this.id = id;
        this.name = name;
        this.phone = phoneNumber;
        this.address = address;
        this.amountUser = staffNumber;
        this.isActive = isActive;
        this.cityId = cityId;
        this.districtId = districtId;
        this.wardId = wardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getAmountUser() {
        return amountUser;
    }

    public void setAmountUser(Integer amountUser) {
        this.amountUser = amountUser;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(Long wardId) {
        this.wardId = wardId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}