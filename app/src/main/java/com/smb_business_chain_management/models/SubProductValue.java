package com.smb_business_chain_management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubProductValue implements Parcelable
{

    @SerializedName("valueId")
    @Expose
    private int valueId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("editionId")
    @Expose
    private int editionId;
    @SerializedName("editionName")
    @Expose
    private String editionName;
    public final static Parcelable.Creator<SubProductValue> CREATOR = new Creator<SubProductValue>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SubProductValue createFromParcel(Parcel in) {
            return new SubProductValue(in);
        }

        public SubProductValue[] newArray(int size) {
            return (new SubProductValue[size]);
        }

    }
            ;

    protected SubProductValue(Parcel in) {
        this.valueId = ((int) in.readValue((int.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.editionId = ((int) in.readValue((int.class.getClassLoader())));
        this.editionName = ((String) in.readValue((String.class.getClassLoader())));
    }

    public SubProductValue() {
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEditionId() {
        return editionId;
    }

    public void setEditionId(int editionId) {
        this.editionId = editionId;
    }

    public String getEditionName() {
        return editionName;
    }

    public void setEditionName(String editionName) {
        this.editionName = editionName;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(valueId);
        dest.writeValue(name);
        dest.writeValue(editionId);
        dest.writeValue(editionName);
    }

    public int describeContents() {
        return 0;
    }

}