package com.smb_business_chain_management.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("districts")
    @Expose
    private List<District> districts = null;
    @SerializedName("id")
    @Expose
    private Long id;

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
    public City(String name, List<District> districts, Long id) {
        super();
        this.name = name;
        this.districts = districts;
        this.id = id;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString()
    {
        return String.format("Thành Phố %s", name);
    }

    public District findDistrictById(Long districtId){
        District ret = new District();
        ret = districts.stream()
                .filter(district -> districtId.equals(district.getId()))
                .findFirst().orElse(getDistricts().get(0));
        return ret;
    }
}