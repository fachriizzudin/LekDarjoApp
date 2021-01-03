package com.lazuardifachri.bps.lekdarjoapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.model.Infographic;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface GraphDataDao {
    @Insert
    Single<Long> insertALl(GraphData graphData);

    @Query("SELECT * FROM graph_data WHERE meta_id=:metaId")
    Single<GraphData> getGraphDataByMetaId(int metaId);

    @Query("SELECT * FROM graph_data WHERE uuid = :uuid")
    Single<GraphData> getGraphDataByUuid(int uuid);

    @Query("SELECT EXISTS(SELECT * FROM graph_data WHERE uuid = :uuid)")
    Single<Boolean> isGraphDataExist(int uuid);

    @Query("DELETE FROM graph_data WHERE meta_id= :metaId")
    Completable deleteByMetaIdGraphData(int metaId);
}
