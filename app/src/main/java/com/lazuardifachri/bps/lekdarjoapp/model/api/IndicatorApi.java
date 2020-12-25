package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.response.IndicatorResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface IndicatorApi {
    @GET("api/indicators")
    Single<IndicatorResponse> getIndicators();
}
