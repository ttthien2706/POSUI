package com.smb_business_chain_management.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class District {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("wards")
    @Expose
    private List<Ward> wards = null;
    @SerializedName("id")
    @Expose
    private Long id;

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
    public District(String name, List<Ward> wards, Long id) {
        super();
        this.name = name;
        this.wards = wards;
        this.id = id;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}