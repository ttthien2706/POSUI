package com.smb_business_chain_management.func_login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    public static String LOGGED_IN_PREF = "isLoggedIn";
    public static String TOKEN_STRING = "tokenString";
    public static String TOKEN_TYPE = "tokenType";
    public static String NAME_ = "name";
    public static String EXPIRE_TIME = "expire";
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }
    /**
     * Set the Token String
     * @param context
     * @param token
     */
    static void setTokenString(Context context, String token) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(TOKEN_STRING, token);
        editor.apply();
    }
    /**
     * Set the Token Type String
     * @param context
     * @param tokenType
     */
    static void setTokenType(Context context, String tokenType) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(TOKEN_TYPE, tokenType);
        editor.apply();
    }
    /**
     * Set the Token Type String
     * @param context
     * @param name
     */
    static void setName(Context context, String name) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(NAME_, name);
        editor.apply();
    }
    /**
     * Set the Token Type String
     * @param context
     * @param expire
     */
    static void setExpireTime(Context context, long expire) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putLong(EXPIRE_TIME, expire);
        editor.apply();
    }
    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }
    /**
     * Get the Token String
     * @param context
     * @return String: access token
     */
    public static String getTokenString(Context context) {
        return getPreferences(context).getString(TOKEN_STRING, "");
    }
    /**
     * Get the Token Type String
     * @param context
     * @return String: access token
     */
    public static String getTokenType(Context context) {
        return getPreferences(context).getString(TOKEN_TYPE, "");
    }
    /**
     * Get the currently logged in user's name
     * @param context
     * @return String: access token
     */
    public static String getName(Context context) {
        return getPreferences(context).getString(NAME_, "");
    }
    /**
     * Get the expire time of the current token
     * @param context
     * @return String: access token
     */
    public static long getExpireTime(Context context) {
        return getPreferences(context).getLong(EXPIRE_TIME, 0);
    }
}
