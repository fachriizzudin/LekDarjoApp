package com.lazuardifachri.bps.lekdarjoapp.util;

import com.lazuardifachri.bps.lekdarjoapp.model.response.DownloadResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadInterceptor implements Interceptor {

    private FileDownloadListener listener;

    public DownloadInterceptor(FileDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(
                new DownloadResponseBody(response.body(), listener)
        ).build();
    }
}
