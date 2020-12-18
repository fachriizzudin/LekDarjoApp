package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class Category {

    @ColumnInfo(name = "category_id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "category_name")
    @SerializedName("name")
    private String name;

    @Embedded
    @SerializedName("subject")
    private Subject subject;

    public Category(int id, String name, Subject subject) {
        this.id = id;
        this.name = name;
        this.subject = subject;
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return name;
    }
}
