package com.lazuardifachri.bps.lekdarjoapp.model;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GraphConverter {
    @TypeConverter
    public static List<Graph> jsonToList(String value) {
        Type listType = new TypeToken<List<Graph>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String listToJson(List<Graph> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
