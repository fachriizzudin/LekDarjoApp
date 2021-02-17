package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface InfographicDao {

    @Insert
    Single<List<Long>> insertAll(List<Infographic> infographics);

    @Query("SELECT * FROM infographic")
    Single<List<Infographic>> getAllInfographic();

    @Query("SELECT * FROM infographic WHERE uuid = :uuid")
    Single<Infographic> getInfographicByUuid(int uuid);

    @Query("SELECT * FROM infographic WHERE subject_id = :subjectId")
    Single<List<Infographic>> getInfographicBySubject(int subjectId);

    @Query("SELECT EXISTS(SELECT * FROM infographic WHERE uuid = :uuid)")
    Single<Boolean> isInfographicExist(int uuid);

    @Query("DELETE FROM infographic")
    Completable deleteAllInfographics();
}
