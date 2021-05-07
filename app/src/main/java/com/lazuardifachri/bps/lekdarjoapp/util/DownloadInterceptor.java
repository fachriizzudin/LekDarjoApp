package com.lazuardifachri.bps.lekdarjoapp.util;

import android.util.Log;

import com.lazuardifachri.bps.lekdarjoapp.model.response.DownloadResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {

    private FileDownloadListener listener;
    private DetailFileDownloadListener newListener;

    public DownloadInterceptor(FileDownloadListener listener) {
        this.listener = listener;
    }

    public DownloadInterceptor(DetailFileDownloadListener listener) {
        this.newListener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(
                new DownloadResponseBody(response.body(), listener)
        ).build();
    }
}
