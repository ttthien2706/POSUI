package com.smb_business_chain_management.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ward implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private int id;

    /**
     * No args constructor for use in serialization
     */
    public Ward() {
    }

    /**
     * @param id
     * @param name
     */
    public Ward(String name, int id) {
        super();
        this.name = name;
        this.id = id;
    }

    protected Ward(Parcel in) {
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<Ward> CREATOR = new Creator<Ward>() {
        @Override
        public Ward createFromParcel(Parcel in) {
            return new Ward(in);
        }

        @Override
        public Ward[] newArray(int size) {
            return new Ward[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
    }
}