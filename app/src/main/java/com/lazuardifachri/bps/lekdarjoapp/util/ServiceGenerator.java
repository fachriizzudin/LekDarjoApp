package com.lazuardifachri.bps.lekdarjoapp.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Gson gson = new GsonBuilder()
            .setDateFormat("dd-MM-yyyy")
            .create();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create());

    public static <T> T createService(Class<T> serviceClass, Context context) {

        Retrofit retrofit = builder.client(new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()).build();

        return retrofit.create(serviceClass);
    }

    public static <T> T createDownloadService(Class<T> serviceClass, Context context, FileDownloadListener listener) {

        Retrofit retrofit = builder.client(new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(new DownloadInterceptor(listener))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()).build();

        return retrofit.create(serviceClass);
    }

}
