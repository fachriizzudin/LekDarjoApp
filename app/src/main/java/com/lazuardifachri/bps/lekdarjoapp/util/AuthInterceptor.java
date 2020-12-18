package com.lazuardifachri.bps.lekdarjoapp.util;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        if (SharedPreferencesHelper.getInstance(context).fetchAuthToken() != null)
            requestBuilder.addHeader("Authorization", "Bearer " + SharedPreferencesHelper.getInstance(context).fetchAuthToken());

        return chain.proceed(requestBuilder.build());
    }
}
