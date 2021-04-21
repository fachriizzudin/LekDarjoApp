package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface GraphDataDao {
    @Insert
    Single<Long> insertALl(GraphData graphData);

    @Query("SELECT * FROM graph_data WHERE serial_number=:metaId")
    Single<GraphData> getGraphDataByMetaId(int metaId);

    @Query("DELETE FROM graph_data WHERE serial_number= :metaId")
    Completable deleteByMetaIdGraphData(int metaId);
}
