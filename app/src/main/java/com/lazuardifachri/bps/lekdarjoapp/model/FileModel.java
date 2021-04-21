package com.lazuardifachri.bps.lekdarjoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "file_model")
public class FileModel {

    @PrimaryKey(autoGenerate = true)
    private int uuid;

    private String id;

    @ColumnInfo(name = "file_name")
    private String fileName;

    private String path;

    public FileModel(String id, String fileName, String path) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

}
