package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.response.PublicationResponse;
import com.lazuardifachri.bps.lekdarjoapp.model.response.StatisticalNewsResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface StatisticalNewsApi {
    @GET("api/statnews")
    Single<StatisticalNewsResponse> getStatisticalNews();
}