package com.smb_business_chain_management.Utils;

import com.google.gson.annotations.SerializedName;

public class LoginInfo {
    @SerializedName("Name")
    String name;
    @SerializedName("exp")
    long expireTime;
    @SerializedName("ChainId")
    String chainId;
    @SerializedName("ShopId")
    String shopId;

    @SerializedName("StorehouseId")
    String storehouseId;

    public void setName(String name) {
        this.name = name;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setStorehouseId(String storehouseId) {
        this.storehouseId = storehouseId;
    }

    public String getShopId() {
        return shopId;
    }

    public String getChainId() {
        return chainId;
    }

    public String getName() {
        return name;
    }
    public long getExpireTime() {
        return expireTime;
    }

    public String getStorehouseId() {
        return storehouseId;
    }
}
