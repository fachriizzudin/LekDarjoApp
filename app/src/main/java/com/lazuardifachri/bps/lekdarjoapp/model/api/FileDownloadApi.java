package com.lazuardifachri.bps.lekdarjoapp.model.api;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface FileDownloadApi {
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
