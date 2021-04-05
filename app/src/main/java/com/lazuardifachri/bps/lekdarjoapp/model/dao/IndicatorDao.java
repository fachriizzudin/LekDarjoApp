package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.Indicator;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface IndicatorDao {

    @Insert
    Single<List<Long>> insertAll(List<Indicator> indicators);

    @Query("SELECT * FROM indicator WHERE subject_id =:subjectId")
    Single<List<Indicator>> getIndicatorBySubject(int subjectId);

    @Query("SELECT * FROM indicator WHERE category_id =:categoryId")
    Single<List<Indicator>> getIndicatorByCategory(int categoryId);

    @Query("SELECT * FROM indicator WHERE category_id =:categoryId AND release_date LIKE :monthYear")
    Single<List<Indicator>> getIndicatorByCategory(int categoryId, String monthYear);

    @Query("SELECT * FROM indicator WHERE subject_id =:subjectId AND release_date LIKE :monthYear")
    Single<List<Indicator>> getIndicatorByMonthYear(int subjectId, String monthYear);

    @Query("DELETE FROM indicator")
    Completable deleteAllIndicator();

}
