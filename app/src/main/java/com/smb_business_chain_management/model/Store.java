package com.smb_business_chain_management.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Store implements Parcelable {
    @SerializedName("id")
    @Expose
    private int id;
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
    @SerializedName("count")
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
    @SerializedName("fullAddress")
    @Expose
    private String fullAddress;

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

    public Store(int id, String name, String phoneNumber, String address, Integer staffNumber, boolean isActive, Long cityId, Long districtId, Long wardId){
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

    protected Store(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        if (in.readByte() == 0) {
            amountUser = null;
        } else {
            amountUser = in.readInt();
        }
        if (in.readByte() == 0) {
            cityId = null;
        } else {
            cityId = in.readLong();
        }
        if (in.readByte() == 0) {
            districtId = null;
        } else {
            districtId = in.readLong();
        }
        if (in.readByte() == 0) {
            wardId = null;
        } else {
            wardId = in.readLong();
        }
        fullAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        if (amountUser == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(amountUser);
        }
        if (cityId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(cityId);
        }
        if (districtId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(districtId);
        }
        if (wardId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(wardId);
        }
        dest.writeString(fullAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

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

    public int getId() {
        return id;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}