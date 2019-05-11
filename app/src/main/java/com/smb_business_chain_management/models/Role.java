package com.smb_business_chain_management.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Role implements Parcelable {
    private int id;
    private String name;

    public Role(int id, String name){
        this.id = id;
        this.name = name;
    }

    protected Role(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Role> CREATOR = new Creator<Role>() {
        @Override
        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        @Override
        public Role[] newArray(int size) {
            return new Role[size];
        }
    };

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
