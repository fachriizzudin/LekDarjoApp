package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "publication")
public class Publication {

    @ColumnInfo(name = "pub_id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    @ColumnInfo(name = "catalog_no")
    @SerializedName("catalog_no")
    private String catalogNo;

    @ColumnInfo(name = "publication_no")
    @SerializedName("publication_no")
    private String publicationNo;

    @ColumnInfo(name = "issn_or_isbn")
    @SerializedName("issn_or_isbn")
    private String issnOrIsbn;

    @Embedded
    @SerializedName("district")
    private District district;

    @Embedded
    @SerializedName("subject")
    private Subject subject;

    @ColumnInfo(name = "information")
    @SerializedName("information")
    private String information;

    @ColumnInfo(name = "image_uri")
    @SerializedName("image_uri")
    private String imageUri;

    @ColumnInfo(name = "document_uri")
    @SerializedName("document_uri")
    private String documentUri;

    @PrimaryKey(autoGenerate = true)
    private int uuid;

    public Publication() {

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

    public String getCatalogNo() {
        return catalogNo;
    }

    public void setCatalogNo(String catalogNo) {
        this.catalogNo = catalogNo;
    }

    public String getPublicationNo() {
        return publicationNo;
    }

    public void setPublicationNo(String publicationNo) {
        this.publicationNo = publicationNo;
    }

    public String getIssnOrIsbn() {
        return issnOrIsbn;
    }

    public void setIssnOrIsbn(String issnOrIsbn) {
        this.issnOrIsbn = issnOrIsbn;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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
        return "Publication{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", catalogNo='" + catalogNo + '\'' +
                ", publicationNo='" + publicationNo + '\'' +
                ", issnOrIsbn='" + issnOrIsbn + '\'' +
                ", district='" + district + '\'' +
                ", subject='" + subject + '\'' +
                ", information='" + information + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", documentUri='" + documentUri + '\'' +
                ", uuid=" + uuid +
                '}';
    }

}
