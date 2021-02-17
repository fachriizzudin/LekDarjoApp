package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

public class GraphMeta {

    @ColumnInfo(name = "meta_id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;

    @Embedded
    @SerializedName("subject")
    private Subject subject;

    @ColumnInfo(name = "horizontal")
    @SerializedName("horizontal")
    private String horizontal;

    @ColumnInfo(name = "vertical")
    @SerializedName("vertical")
    private String vertical;

    @ColumnInfo(name = "vertical_unit")
    @SerializedName("vertical_unit")
    private String verticalUnit;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String description;

    public GraphMeta(int id, String title, Subject subject, String horizontal, String vertical, String verticalUnit, String description) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.verticalUnit = verticalUnit;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(String horizontal) {
        this.horizontal = horizontal;
    }

    public String getVertical() {
        return vertical;
    }

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }

    public String getVerticalUnit() {
        return verticalUnit;
    }

    public void setVerticalUnit(String verticalUnit) {
        this.verticalUnit = verticalUnit;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "GraphMeta{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", horizontal='" + horizontal + '\'' +
                ", vertical='" + vertical + '\'' +
                ", verticalUnit='" + verticalUnit + '\'' +
                '}';
    }
}
