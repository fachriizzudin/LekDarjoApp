package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.lazuardifachri.bps.lekdarjoapp.model.Publication;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PublicationDao {

    @Insert
    Single<List<Long>> insertAll(List<Publication> publications);

    @Query("SELECT * FROM publication")
    Single<List<Publication>> getAllPublication();

    @Query("SELECT * FROM publication WHERE uuid = :uuid")
    Single<Publication> getPublicationByUuid(int uuid);

    @Query("SELECT * FROM publication WHERE subject_id =:subjectId AND district_code = :districtCode AND release_date LIKE :year")
    Single<List<Publication>> getPublicationByFilter(int subjectId, String districtCode, String year);

    @Query("SELECT * FROM publication WHERE subject_id =:subjectId AND release_date LIKE :year")
    Single<List<Publication>> getPublicationBySubject(int subjectId, String year);

    @Query("SELECT * FROM publication WHERE district_code =:districtCode AND release_date LIKE :year")
    Single<List<Publication>> getPublicationByDistrict(String districtCode, String year);

    @Query("SELECT * FROM publication WHERE release_date LIKE :year")
    Single<List<Publication>> getPublicationByYear(String year);

    @Query("SELECT EXISTS(SELECT * FROM publication WHERE uuid = :uuid)")
    Single<Boolean> isPublicationExist(int uuid);

    @Query("DELETE FROM publication")
    Completable deleteAllPublications();

}
