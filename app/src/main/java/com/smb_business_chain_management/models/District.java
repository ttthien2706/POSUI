package com.smb_business_chain_management.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class District implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("wards")
    @Expose
    private List<Ward> wards = null;
    @SerializedName("id")
    @Expose
    private int id;

    /**
     * No args constructor for use in serialization
     */
    public District() {
    }

    /**
     * @param id
     * @param wards
     * @param name
     */
    public District(String name, List<Ward> wards, int id) {
        super();
        this.name = name;
        this.wards = wards;
        this.id = id;
    }

    protected District(Parcel in) {
        name = in.readString();
        id = in.readInt();
        wards = new ArrayList<>();
        in.readList(wards, Ward.class.getClassLoader());
    }

    public static final Creator<District> CREATOR = new Creator<District>() {
        @Override
        public District createFromParcel(Parcel in) {
            return new District(in);
        }

        @Override
        public District[] newArray(int size) {
            return new District[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ward> getWards() {
        return wards;
    }

    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString()
    {
        return String.format("%s", name);
    }

    public Ward findWardById(Long wardId){
        Ward ret = new Ward();
        ret = wards.stream()
                .filter(ward -> wardId.equals(ward.getId()))
                .findFirst().orElse(getWards().get(0));
        return ret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeList(wards);
    }
}