package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GraphConverter {
    @TypeConverter
    public static List<GraphData> jsonToList(String value) {
        Type listType = new TypeToken<List<GraphData>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String listToJson(List<GraphData> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
