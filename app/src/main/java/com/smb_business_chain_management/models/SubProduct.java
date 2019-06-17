package com.smb_business_chain_management.models;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubProduct implements Parcelable, Serializable
{
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("importPrice")
    @Expose
    private int importPrice;
    @SerializedName("retailPrice")
    @Expose
    private int retailPrice;
    @SerializedName("wholesalePrice")
    @Expose
    private int wholesalePrice;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("subProductValues")
    @Expose
    private List<SubProductValue> subProductValues = null;
    public final static Parcelable.Creator<SubProduct> CREATOR = new Creator<SubProduct>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SubProduct createFromParcel(Parcel in) {
            return new SubProduct(in);
        }

        public SubProduct[] newArray(int size) {
            return (new SubProduct[size]);
        }

    }
            ;

    protected SubProduct(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.sku = ((String) in.readValue((String.class.getClassLoader())));
        this.barcode = ((String) in.readValue((String.class.getClassLoader())));
        this.importPrice = ((int) in.readValue((int.class.getClassLoader())));
        this.retailPrice = ((int) in.readValue((int.class.getClassLoader())));
        this.wholesalePrice = ((int) in.readValue((int.class.getClassLoader())));
        this.quantity = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.subProductValues, (com.smb_business_chain_management.models.SubProductValue.class.getClassLoader()));
    }

    public SubProduct() {
        this.id = -1;
        this.name = "";
        this.sku = "";
        this.barcode = "";
        this.importPrice = 0;
        this.retailPrice = 0;
        this.wholesalePrice = 0;
        this.quantity = 0;
        this.subProductValues = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(int importPrice) {
        this.importPrice = importPrice;
    }

    public int getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(int retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(int wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<SubProductValue> getSubProductValues() {
        return subProductValues;
    }

    public void setSubProductValues(List<SubProductValue> subProductValues) {
        this.subProductValues = subProductValues;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(sku);
        dest.writeValue(barcode);
        dest.writeValue(importPrice);
        dest.writeValue(retailPrice);
        dest.writeValue(wholesalePrice);
        dest.writeValue(quantity);
        dest.writeList(subProductValues);
    }

    public int describeContents() {
        return 0;
    }

}