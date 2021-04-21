package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "graph_meta")
public class GraphMeta {

    @ColumnInfo(name = "id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "serial_number")
    @SerializedName("serial_number")
    private int serialNumber;

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

    @ColumnInfo(name = "graph_type")
    @SerializedName("graph_type")
    private int graphType;

    @ColumnInfo(name = "data_type")
    @SerializedName("data_type")
    private int dataType;

    @ColumnInfo(name = "image_uri")
    @SerializedName("image_uri")
    private String imageUri;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "meta_uuid")
    private int uuid;

    public GraphMeta() {
    }

    public GraphMeta(int id, int serialNumber, String title, Subject subject, String horizontal, String vertical, String verticalUnit, String description, int graphType, int dataType, String imageUri) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.title = title;
        this.subject = subject;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.verticalUnit = verticalUnit;
        this.description = description;
        this.graphType = graphType;
        this.dataType = dataType;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
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

    public int getGraphType() {
        return graphType;
    }

    public void setGraphType(int graphType) {
        this.graphType = graphType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return "GraphMeta{" +
                "id=" + id +
                ", serialNumber=" + serialNumber +
                ", title='" + title + '\'' +
                ", subject=" + subject +
                ", horizontal='" + horizontal + '\'' +
                ", vertical='" + vertical + '\'' +
                ", verticalUnit='" + verticalUnit + '\'' +
                ", description='" + description + '\'' +
                ", graphType=" + graphType +
                ", dataType=" + dataType +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }
}
