package com.smb_business_chain_management.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order implements Parcelable, Serializable {
    private int id;
    @SerializedName("receiptCode")
    @Expose
    private String receiptCode;
    @SerializedName("shopId")
    @Expose
    private int shopId;
    @SerializedName("userId")
    @Expose
    private int userId;
    private String orderDate = "";
    @SerializedName("payment")
    @Expose
    private String payment = "";
    @SerializedName("receiptTotalPrice")
    @Expose
    private int receiptTotalPrice = 0;
    @SerializedName("productReceiptDetails")
    @Expose
    private List<Product> products = new ArrayList<>(0);
    public final static Parcelable.Creator<Order> CREATOR = new Creator<Order>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }
        public Order[] newArray(int size) {
            return (new Order[size]);
        }
    };
    protected Order(Parcel in) {
        this.shopId = ((int) in.readValue((int.class.getClassLoader())));
        this.payment = ((String) in.readValue((String.class.getClassLoader())));
        this.receiptCode = ((String) in.readValue(String.class.getClassLoader()));
        in.readList(this.products, (com.smb_business_chain_management.models.Product.class.getClassLoader()));
    }
    /**
     * No args constructor for use in serialization
     *
     */
    public Order() {
    }
    /**
     *
     * @param payment
     * @param shopId
     * @param products
     */
    public Order(int id, int shopId, String payment, List<Product> products) {
        super();
        this.id = id;
        this.shopId = shopId;
        this.payment = payment;
        this.products = products;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getReceiptCode() {
        return receiptCode;
    }
    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }
    public int getShopId() {
        return shopId;
    }
    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public int getReceiptTotalPrice() {
        return receiptTotalPrice;
    }
    public void setReceiptTotalPrice(int receiptTotalPrice) {
        this.receiptTotalPrice = receiptTotalPrice;
    }
    public String getPayment() {
        return payment;
    }
    public void setPayment(String payment) {
        this.payment = payment;
    }
    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(shopId);
        dest.writeValue(payment);
        dest.writeValue(receiptCode);
        dest.writeList(products);
    }
    public int describeContents() {
        return 0;
    }
    public BigInteger getTotalPrice(){
        BigInteger total = BigInteger.ZERO;
        for (Product product : products){
            BigInteger subTotal = new BigInteger(String.valueOf(product.getQuantity() * product.getPrice()));
            total = total.add(subTotal);
        }
        return total;
    }
}
