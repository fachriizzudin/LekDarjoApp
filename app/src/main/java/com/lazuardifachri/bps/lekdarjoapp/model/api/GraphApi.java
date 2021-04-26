package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.Graph;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GraphApi {
    @GET("api/graph/{id}")
    Single<Graph> getGraph(@Path("id") int id);
}
