package com.example.conav;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {
    private final static String SPKEY = "coNavSP";

    public static void setSetting(Context context, String key, String value){
        AppSettings.getSp(context).edit().putString(key,value).apply();
    }
    public static String getSetting(Context context, String key, String def){
        return AppSettings.getSp(context).getString(key, def);
    }
    public static String getSetting(Context context, String key){
        return AppSettings.getSetting(context, key, "");
    }
    private static SharedPreferences getSp(Context context){
        return context.getSharedPreferences(AppSettings.SPKEY,Context.MODE_PRIVATE);
    }
}

