package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "graph")
public class Graph implements Comparable<Graph> {

    @ColumnInfo(name = "data")
    @SerializedName("data")
    private List<GraphData> data;

    @Embedded
    @SerializedName("meta")
    private GraphMeta meta;

    @PrimaryKey(autoGenerate = true)
    private int uuid;


    public Graph() {
    }

    public Graph(List<GraphData> data, GraphMeta meta, int uuid) {
        this.data = data;
        this.meta = meta;
        this.uuid = uuid;
    }

    public List<GraphData> getData() {
        return data;
    }

    public void setData(List<GraphData> data) {
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

    @Override
    public int compareTo(Graph graph) {
        int serialNumber = graph.getMeta().getSerialNumber();
        return this.getMeta().getSerialNumber() - serialNumber;
    }
}
