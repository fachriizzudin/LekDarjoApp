package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.Download;
import com.lazuardifachri.bps.lekdarjoapp.model.FileModel;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface DownloadDao {
    @Insert
    Single<Long> insertWaitingFile(Download download);

    @Query("SELECT EXISTS(SELECT * FROM download WHERE id = :fileId)")
    Single<Boolean> isWaitingFileExist(int fileId);

    @Query("DELETE FROM download WHERE id = :fileId")
    Completable deleteWaitingFile(int fileId);
}
