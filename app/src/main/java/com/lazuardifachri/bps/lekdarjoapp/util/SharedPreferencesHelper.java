package com.lazuardifachri.bps.lekdarjoapp.util;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesHelper {

    private final String GRAPH_TIME = "graph_time";
    private final String NEWS_TIME = "news_time";
    private final String PUB_TIME = "pub_time";
    private final String IDX_TIME = "idx_time";
    private final String INFO_TIME = "info_time";
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

    public void saveGraphUpdateTime(long time) {
        pref.edit().putLong(GRAPH_TIME, time).commit();
    }

    public long getGraphUpdateTime() {
        return pref.getLong(GRAPH_TIME, 0);
    }

    public void saveNewsUpdateTime(long time) {
        pref.edit().putLong(NEWS_TIME, time).commit();
    }

    public long getNewsUpdateTime() {
        return pref.getLong(NEWS_TIME, 0);
    }

    public void savePubUpdateTime(long time) {
        pref.edit().putLong(PUB_TIME, time).commit();
    }

    public long getPubUpdateTime() {
        return pref.getLong(PUB_TIME, 0);
    }

    public void saveIdxUpdateTime(long time) {
        pref.edit().putLong(IDX_TIME, time).commit();
    }

    public long getIdxUpdateTime() {
        return pref.getLong(IDX_TIME, 0);
    }

    public void saveInfoUpdateTime(long time) {
        pref.edit().putLong(INFO_TIME, time).commit();
    }

    public long getInfoUpdateTime() {
        return pref.getLong(INFO_TIME, 0);
    }

    public void saveAuthToken(String token) { pref.edit().putString(TOKEN, token).commit(); }

    public String fetchAuthToken() {
        return pref.getString(TOKEN, null);
    }

}
