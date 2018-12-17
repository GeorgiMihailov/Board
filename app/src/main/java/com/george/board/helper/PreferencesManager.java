package com.george.board.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences("MySharedPreferencesFile", Activity.MODE_PRIVATE);
    }

    public static void setUserId(Context c, int userId){
        getPreferences(c).edit().putInt("userId", userId).apply();
    }
    public static  int getUserId(Context c){
        return getPreferences(c).getInt("userId", 0);
    }
    public static void setCompanyId(int companyId, Context context){
        getPreferences(context).edit().putInt("companyId", companyId).apply();
    }
    public static int getCompanyId (Context context){
        return getPreferences(context).getInt("companyId", 0);
    }
    public static Long getTokenExpiry (Context context){
        return getPreferences(context).getLong("expiryTime", 0);
    }
    public static void addTokenExpiry (Context context, long expiryTime){
        getPreferences(context).edit().putLong("expiryTime", expiryTime).apply();
    }
    public static void addAccessToken(String accessToken, Context context){
        getPreferences(context).edit().putString("accessToken", accessToken).apply();
    }
    public static String getAccessToken (Context context){
        return getPreferences(context).getString("accessToken", "");
    }
    public static void setUserPicture(String userPicture, Context context){
        getPreferences(context).edit().putString("userPicture", userPicture).apply();
    }
    public static String getUserPicture (Context context){
        return getPreferences(context).getString("userPicture", "");
    }

    public static void setUserName(String userPicture, Context context){
        getPreferences(context).edit().putString("username", userPicture).apply();
    }
    public static String getUserName (Context context){
        return getPreferences(context).getString("username", "");
    }
    public static void setUserLastname(String userPicture, Context context){
        getPreferences(context).edit().putString("userLastName", userPicture).apply();
    }
    public static String getUserLastname (Context context){
        return getPreferences(context).getString("userLastName", "");
    }





}
