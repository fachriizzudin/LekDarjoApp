package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.GraphMeta;
import com.lazuardifachri.bps.lekdarjoapp.model.response.PublicationResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface GraphMetaApi {
    @GET("/api/graphmeta/count")
    Single<Long> getGraphMetaCount();
}
