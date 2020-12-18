package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.response.PublicationResponse;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface PublicationApi {
    @GET("api/publications")
    Single<PublicationResponse> getPublications();
}
