package com.george.board.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences("MySharedPreferencesFile", Activity.MODE_PRIVATE);
    }

    public static void setUserBackground(Context c, String background){
        getPreferences(c).edit().putString("background",background).apply();
    }
    public static  String getUserBackground(Context c){
        return getPreferences(c).getString("background", "");
    }
    public static void setPrimaryColor(Context c, String primaryColor){
        getPreferences(c).edit().putString("primaryColor",primaryColor).apply();
    }
    public static  String getPrimaryColor(Context c){
        return getPreferences(c).getString("primaryColor", "");
    }
    public static void setPrimaryDarkColor(Context c, String primaryDarkColor){
        getPreferences(c).edit().putString("primaryDarkColor",primaryDarkColor).apply();
    }
    public static  String getPrimaryDarkColor(Context c){
        return getPreferences(c).getString("primaryDarkColor", "");
    }
    public static void setAccentColor(Context c, String accentColor){
        getPreferences(c).edit().putString("accentColor",accentColor).apply();
    }
    public static  String getAccentColor(Context c){
        return getPreferences(c).getString("accentColor", "");
    }
    public static void setLogo(Context c, String logo){
        getPreferences(c).edit().putString("logo",logo).apply();
    }
    public static  String getLogo(Context c){
        return getPreferences(c).getString("logo", "");
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
