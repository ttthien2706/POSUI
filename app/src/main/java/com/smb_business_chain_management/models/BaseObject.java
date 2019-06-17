package com.smb_business_chain_management.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseObject implements Parcelable {

    public final static Parcelable.Creator<BaseObject> CREATOR = new Creator<BaseObject>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BaseObject createFromParcel(Parcel in) {
            return new BaseObject(in);
        }

        public BaseObject[] newArray(int size) {
            return (new BaseObject[size]);
        }

    };
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private int id;

    protected BaseObject(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((int) in.readValue((int.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public BaseObject() {
    }

    /**
     * @param id
     * @param name
     */
    public BaseObject(String name, int id) {
        super();
        this.name = name;
        this.id = id;
    }

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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(id);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}