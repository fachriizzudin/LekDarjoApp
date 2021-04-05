package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.response.PublicationResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface NewPublicationApi {
    @GET("api/publications")
    Single<PublicationResponse> getPublications();
}
