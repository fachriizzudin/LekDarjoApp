package com.lazuardifachri.bps.lekdarjoapp.model.api;

import com.lazuardifachri.bps.lekdarjoapp.model.request.LoginRequest;
import com.lazuardifachri.bps.lekdarjoapp.model.response.LoginResponse;
import com.lazuardifachri.bps.lekdarjoapp.util.Constant;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {
    @POST(Constant.SIGNIN_URL)
    Observable<LoginResponse> login(@Body LoginRequest loginRequest);
}
