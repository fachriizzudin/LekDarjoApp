package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "graph_data")
public class GraphData {

    @ColumnInfo(name = "data")
    @SerializedName("data")
    private List<Graph> data;

    @Embedded
    @SerializedName("meta")
    private GraphMeta meta;

    @PrimaryKey(autoGenerate = true)
    private int uuid;


    public GraphData() {
    }

    public List<Graph> getData() {
        return data;
    }

    public void setData(List<Graph> data) {
        this.data = data;
    }

    public GraphMeta getMeta() {
        return meta;
    }

    public void setMeta(GraphMeta meta) {
        this.meta = meta;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "GraphData{" +
                "data=" + data +
                ", meta=" + meta +
                ", uuid=" + uuid +
                '}';
    }
}
