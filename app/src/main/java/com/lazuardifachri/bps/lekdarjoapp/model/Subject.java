package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class  Subject {

    @ColumnInfo(name = "subject_id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "subject_name")
    @SerializedName("name")
    private String name;

    public Subject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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