package com.smb_business_chain_management.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("districts")
    @Expose
    private List<District> districts = null;
    @SerializedName("id")
    @Expose
    private int id;

    /**
     * No args constructor for use in serialization
     */
    public City() {
    }

    /**
     * @param id
     * @param name
     * @param districts
     */
    public City(String name, List<District> districts, int id) {
        super();
        this.name = name;
        this.districts = districts;
        this.id = id;
    }

    protected City(Parcel in) {
        name = in.readString();
        id = in.readInt();
        districts = new ArrayList<>();
        in.readList(districts, District.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
        dest.writeList(districts);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
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

    public District findDistrictById(Long districtId){
        District ret;
        ret = districts.stream()
                .filter(district -> districtId.equals(district.getId()))
                .findFirst().orElse(getDistricts().get(0));
        return ret;
    }

}