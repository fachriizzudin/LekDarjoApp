package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;

import io.reactivex.rxjava3.core.Single;


@Dao
public interface FileModelDao {
    @Insert
    Single<Long> insertFile(FileModel file);

    @Query("SELECT EXISTS(SELECT * FROM file_model WHERE id = :fileId)")
    Single<Boolean> isFileExist(int fileId);

    @Query("SELECT EXISTS(SELECT * FROM file_model WHERE id = :fileId)")
    Single<Integer> isIndicatorExist(int fileId);

    @Query("SELECT file_name FROM file_model WHERE id = :fileId")
    Single<String> getFileName(int fileId);
}
