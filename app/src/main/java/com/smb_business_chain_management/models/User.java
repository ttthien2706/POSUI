package com.smb_business_chain_management.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("roleId")
    @Expose
    private int roleId;
    @SerializedName("shopId")
    @Expose
    private int shopId;
    @SerializedName("wardId")
    @Expose
    private int wardId;
    @SerializedName("districtId")
    @Expose
    private int districtId;
    @SerializedName("cityId")
    @Expose
    private int cityId;
    @SerializedName("roleName")
    @Expose
    private String roleName;
    @SerializedName("fullAddress")
    @Expose
    private String fullAddress;

    public User(String name, String phone, String address, int roleId, int shopId, int wardId, int districtId, int cityId, String roleName) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.roleId = roleId;
        this.shopId = shopId;
        this.wardId = wardId;
        this.districtId = districtId;
        this.cityId = cityId;
        this.roleName = roleName;
    }

    public User(int id, String name, String phone, String address, int roleId, int shopId, int wardId, int districtId, int cityId, String roleName) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.roleId = roleId;
        this.shopId = shopId;
        this.wardId = wardId;
        this.districtId = districtId;
        this.cityId = cityId;
        this.roleName = roleName;
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phone = in.readString();
        address = in.readString();
        roleId = in.readInt();
        shopId = in.readInt();
        wardId = in.readInt();
        districtId = in.readInt();
        cityId = in.readInt();
        roleName = in.readString();
        fullAddress = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getWardId() {
        return wardId;
    }

    public void setWardId(int wardId) {
        this.wardId = wardId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(address);
        parcel.writeInt(roleId);
        parcel.writeInt(shopId);
        parcel.writeInt(wardId);
        parcel.writeInt(districtId);
        parcel.writeInt(cityId);
        parcel.writeString(roleName);
        parcel.writeString(fullAddress);
    }
}