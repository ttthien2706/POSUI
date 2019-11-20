package com.smb_business_chain_management.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginToken {
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("expires_in")
    @Expose
    private int expiresIn;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    /**
     * No args constructor for use in serialization
     *
     */
    public LoginToken() {
    }
    /**
     *
     * @param tokenType
     * @param accessToken
     * @param expiresIn
     */
    public LoginToken(String accessToken, int expiresIn, String tokenType) {
        super();
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public int getExpiresIn() {
        return expiresIn;
    }
    public String getTokenType() {
        return tokenType;
    }
}