package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;
import com.lazuardifachri.bps.lekdarjoapp.model.StatisticalNews;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface StatisticalNewsDao {
    @Insert
    Single<List<Long>> insertAll(List<StatisticalNews> statisticalNews);

    @Query("SELECT * FROM statistical_news")
    Single<List<StatisticalNews>> getAllStatisticalNews();

    @Query("SELECT * FROM statistical_news WHERE uuid = :uuid")
    Single<StatisticalNews> getStatisticalNewsByUuid(int uuid);

    @Query("SELECT * FROM statistical_news WHERE subject_id =:subjectId AND release_date LIKE :monthYear")
    Single<List<StatisticalNews>> getStatisticalNewsBySubject(int subjectId, String monthYear);

    @Query("SELECT * FROM statistical_news WHERE category_id =:categoryId AND release_date LIKE :monthYear")
    Single<List<StatisticalNews>> getStatisticalNewsByCategory(int categoryId, String monthYear);

    @Query("SELECT * FROM statistical_news WHERE subject_id =:subjectId AND category_id =:categoryId AND release_date LIKE :monthYear")
    Single<List<StatisticalNews>> getStatisticalNewsBySubjectAndCategory(int subjectId, int categoryId, String monthYear);

    @Query("SELECT * FROM statistical_news WHERE release_date LIKE :monthYear")
    Single<List<StatisticalNews>> getStatisticalNewsByMonthYear(String monthYear);

    @Query("DELETE FROM statistical_news")
    Completable deleteAllStatisticalNews();
}
