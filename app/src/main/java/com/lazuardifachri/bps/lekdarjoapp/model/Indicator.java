package com.lazuardifachri.bps.lekdarjoapp.model;


import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.lazuardifachri.bps.lekdarjoapp.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

@Entity(tableName = "indicator")
public class Indicator {

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

    @ColumnInfo(name = "stat_type")
    @SerializedName("stat_type")
    private String statType;

    @ColumnInfo(name = "document_uri")
    @SerializedName("document_uri")
    private String documentUri;

    @PrimaryKey(autoGenerate = true)
    private int uuid;

    public Indicator(int id, String title, String releaseDate, Category category, String statType, String documentUri) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.category = category;
        this.statType = statType;
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

    public String getStatType() {
        return StringUtil.capitalizeWord(statType.toLowerCase().replace("_", " "));
    }

    public void setStatType(String statType) {
        this.statType = statType;
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
        return "Indicator{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", category=" + category +
                ", statType='" + statType + '\'' +
                ", documentUri='" + documentUri + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
