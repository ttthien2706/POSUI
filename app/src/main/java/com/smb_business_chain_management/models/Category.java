package com.smb_business_chain_management.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category extends BaseObject implements Parcelable {

    public Category(){
        categoryId = -2;
        categoryName = "";
    }

    public Category(int id, String name){
        categoryId = id;
        categoryName = name;
    }

    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("categoryId")
    @Expose
    private int categoryId;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}