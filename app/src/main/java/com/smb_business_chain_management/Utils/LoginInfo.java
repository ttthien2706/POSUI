package com.smb_business_chain_management.Utils;

import com.google.gson.annotations.SerializedName;

public class LoginInfo {
    @SerializedName("name")
    String name;
    @SerializedName("exp")
    long expireTime;

    public String getName() {
        return name;
    }
    public long getExpireTime() {
        return expireTime;
    }
}
