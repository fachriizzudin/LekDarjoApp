package com.lazuardifachri.bps.lekdarjoapp.model;

import com.google.gson.annotations.SerializedName;

public class GraphData {

    @SerializedName("id")
    private int id;

    @SerializedName("value")
    private double value;

    @SerializedName("year")
    private int year;

    public GraphData(int id, double value, int year) {
        this.id = id;
        this.value = value;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "id=" + id +
                ", value=" + value +
                ", year=" + year +
                '}';
    }
}
