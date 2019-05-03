package com.smb_business_chain_management.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ward {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Long id;

    /**
     * No args constructor for use in serialization
     */
    public Ward() {
    }

    /**
     * @param id
     * @param name
     */
    public Ward(String name, Long id) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString()
    {
        return String.format("Phường %s", name);
    }
}