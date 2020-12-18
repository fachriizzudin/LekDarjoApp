package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class District {
    @ColumnInfo(name = "district_code")
    @SerializedName("code")
    private String code;

    @ColumnInfo(name = "district_name")
    @SerializedName("name")
    private String name;

    public District(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
