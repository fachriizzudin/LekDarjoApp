package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "statistical_news")
public class StatisticalNews implements Serializable {

    @ColumnInfo(name = "pub_id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    @Embedded
    @SerializedName("category")
    private Category category;

    @ColumnInfo(name = "abstraction")
    @SerializedName("abstraction")
    private String abstraction;

    @ColumnInfo(name = "document_uri")
    @SerializedName("document_uri")
    private String documentUri;

    @PrimaryKey(autoGenerate = true)
    private int uuid;

    public StatisticalNews(int id, String title, String releaseDate, Category category, String abstraction, String documentUri) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.category = category;
        this.abstraction = abstraction;
        this.documentUri = documentUri;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getAbstraction() {
        return abstraction;
    }

    public void setAbstraction(String abstraction) {
        this.abstraction = abstraction;
    }

    public String getDocumentUri() {
        return documentUri;
    }

    public void setDocumentUri(String documentUri) {
        this.documentUri = documentUri;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "StatisticalNews{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", category=" + category +
                ", abstraction='" + abstraction + '\'' +
                ", documentUri='" + documentUri + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
