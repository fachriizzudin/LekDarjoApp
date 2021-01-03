package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.GraphData;
import com.lazuardifachri.bps.lekdarjoapp.model.response.IndicatorResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GraphDataApi {
    @GET("api/graph/{id}")
    Single<GraphData> getGraphData(@Path("id") int id);
}
