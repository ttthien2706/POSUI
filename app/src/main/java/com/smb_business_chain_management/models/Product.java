package com.smb_business_chain_management.models;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product implements Parcelable, Serializable
{
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("categoryId")
    @Expose
    private int categoryId;
    @SerializedName("categoryName")
    @Expose
    private String category;
    @SerializedName("brandId")
    @Expose
    private int brandId;
    @SerializedName("brandName")
    @Expose
    private String brand;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("photoPath")
    @Expose
    private String photoPath;
    private int measurementId;
    private boolean multipleEditions;
    private boolean uniqueEditionPrice;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    private int limQuantity;
    @SerializedName("isActive")
    @Expose
    private boolean isActive;
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
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("productShops")
    @Expose
    private List<Store> stores = new ArrayList<>(0);
    @SerializedName("productStorehouses")
    @Expose
    private List<Store> storehouses = new ArrayList<>(0);

    private List<SubProduct> subProducts = new ArrayList<>(0);
    private boolean isSub = false;
    private int parentId = -1;
    public final static Parcelable.Creator<Product> CREATOR = new Creator<Product>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }
        public Product[] newArray(int size) {
            return (new Product[size]);
        }
    };
    protected Product(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.categoryId = ((int) in.readValue((int.class.getClassLoader())));
        this.category = ((String) in.readValue(String.class.getClassLoader()));
        this.brandId = ((int) in.readValue((int.class.getClassLoader())));
        this.brand = ((String) in.readValue(String.class.getClassLoader()));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.photoPath = ((String) in.readValue((String.class.getClassLoader())));
        this.measurementId = ((int) in.readValue((int.class.getClassLoader())));
        this.multipleEditions = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.uniqueEditionPrice = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.quantity = ((int) in.readValue((int.class.getClassLoader())));
        this.isActive = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.sku = ((String) in.readValue((String.class.getClassLoader())));
        this.barcode = ((String) in.readValue((String.class.getClassLoader())));
        this.importPrice = ((int) in.readValue((int.class.getClassLoader())));
        this.retailPrice = ((int) in.readValue((int.class.getClassLoader())));
        this.wholesalePrice = ((int) in.readValue((int.class.getClassLoader())));
        this.price = ((int) in.readValue((int.class.getClassLoader())));

        in.readList(this.stores, (com.smb_business_chain_management.models.Store.class.getClassLoader()));
        in.readList(this.storehouses, (com.smb_business_chain_management.models.Store.class.getClassLoader()));
        in.readList(this.subProducts, (com.smb_business_chain_management.models.SubProduct.class.getClassLoader()));
    }
    public Product() {
        this.id = -1;
        this.categoryId = -1;
        this.category = "";
        this.brandId = -1;
        this.brand = "";
        this.description = "";
        this.photoPath = "default.jpg";
        this.measurementId = -1;
        this.multipleEditions = false;
        this.uniqueEditionPrice = false;
        this.name = "";
        this.quantity = -1;
        this.isActive = false;
        this.sku = "";
        this.barcode = "";
        this.importPrice = -1;
        this.retailPrice = -1;
        this.wholesalePrice = -1;
        this.stores = null;
        this.storehouses = null;
        this.subProducts = null;
    }

    public Product(Product product){
        this.id = product.getId();
        this.categoryId = product.getCategoryId();
        this.category = product.getCategory();
        this.brandId = product.getBrandId();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.photoPath = product.getPhotoPath();
        this.measurementId = product.getMeasurementId();
        this.multipleEditions = product.isMultipleEditions();
        this.uniqueEditionPrice = product.isUniqueEditionPrice();
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.limQuantity = product.getLimQuantity();
        this.isActive = product.isActive();
        this.sku = product.getSku();
        this.barcode = product.getBarcode();
        this.importPrice = product.getImportPrice();
        this.retailPrice = product.getRetailPrice();
        this.wholesalePrice = product.getWholesalePrice();
        this.price = product.getPrice();
        this.stores = product.getStores();
        this.storehouses = product.getStorehouses();
        this.subProducts = product.getSubProducts();
        this.isSub = false;
        this.parentId = -1;
    }

    public Product(int categoryId, int brandId, String description, int measurementId, boolean multipleEditions, boolean uniqueEditionPrice, String name, int quantity, boolean isActive, String sku, String barcode, int importPrice, int retailPrice, int wholesalePrice, List<Store> stores, List<SubProduct> subProducts) {
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.description = description;
        this.measurementId = measurementId;
        this.multipleEditions = multipleEditions;
        this.uniqueEditionPrice = uniqueEditionPrice;
        this.name = name;
        this.quantity = quantity;
        this.isActive = isActive;
        this.sku = sku;
        this.barcode = barcode;
        this.importPrice = importPrice;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
        this.stores = stores;
        this.subProducts = subProducts;
    }

    public Product(SubProduct subProduct, int parentId){
        this.parentId = parentId;
        this.name = subProduct.getName();
        this.id = subProduct.getId();
        this.barcode = subProduct.getBarcode() != null ? subProduct.getBarcode() : "";
        this.isSub = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(int measurementId) {
        this.measurementId = measurementId;
    }

    public boolean isMultipleEditions() {
        return multipleEditions;
    }

    public void setMultipleEditions(boolean multipleEditions) {
        this.multipleEditions = multipleEditions;
    }

    public boolean isUniqueEditionPrice() {
        return uniqueEditionPrice;
    }

    public void setUniqueEditionPrice(boolean uniqueEditionPrice) {
        this.uniqueEditionPrice = uniqueEditionPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public List<SubProduct> getSubProducts() {
        return subProducts;
    }

    public void setSubProducts(List<SubProduct> subProducts) {
        this.subProducts = subProducts;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getLimQuantity() {
        return limQuantity;
    }

    public void setLimQuantity(int limQuantity) {
        this.limQuantity = limQuantity;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(categoryId);
        dest.writeValue(category);
        dest.writeValue(brandId);
        dest.writeValue(brand);
        dest.writeValue(description);
        dest.writeValue(photoPath);
        dest.writeValue(measurementId);
        dest.writeValue(multipleEditions);
        dest.writeValue(uniqueEditionPrice);
        dest.writeValue(name);
        dest.writeValue(quantity);
        dest.writeValue(isActive);
        dest.writeValue(sku);
        dest.writeValue(barcode);
        dest.writeValue(importPrice);
        dest.writeValue(retailPrice);
        dest.writeValue(wholesalePrice);
        dest.writeValue(price);
        dest.writeList(stores);
        dest.writeList(storehouses);
        dest.writeList(subProducts);
    }

    public int describeContents() {
        return 0;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<Store> getStorehouses() {
        return storehouses;
    }

    public void setStorehouses(List<Store> storehouses) {
        this.storehouses = storehouses;
    }

    public String getDetails(){
        return (NumberFormat.getNumberInstance(Locale.GERMANY).format(this.getRetailPrice())) + " đ";
    }
}