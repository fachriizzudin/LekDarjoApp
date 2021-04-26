package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.Graph;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface GraphDao {
    @Insert
    Single<Long> insertAll(Graph graph);

    @Query("SELECT COUNT(*) FROM graph")
    Single<Integer> getCount();

    @Query("SELECT * FROM graph WHERE serial_number=:metaId")
    Single<Graph> getGraphDataByMetaId(int metaId);

    @Query("SELECT * FROM graph WHERE uuid=:uuid")
    Single<Graph> getGraphDataByUuid(int uuid);

    @Query("DELETE FROM graph WHERE serial_number= :metaId")
    Completable deleteByMetaIdGraphData(int metaId);
}
