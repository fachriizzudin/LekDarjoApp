package com.lazuardifachri.bps.lekdarjoapp.util;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesHelper {

    private final String PREF_TIME = "pref_time";
    private final String TOKEN = "jwt_token";

    private static SharedPreferencesHelper instance;
    private final SharedPreferences pref;

    private SharedPreferencesHelper(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context);
        }
        return instance;
    }

    public void saveUpdateTime(long time) {
        pref.edit().putLong(PREF_TIME, time).apply();
    }

    public long getUpdateTime() {
        return pref.getLong(PREF_TIME, 0);
    }

    public void saveAuthToken(String token) { pref.edit().putString(TOKEN, token).apply(); }

    public String fetchAuthToken() {
        return pref.getString(TOKEN, null);
    }

}
