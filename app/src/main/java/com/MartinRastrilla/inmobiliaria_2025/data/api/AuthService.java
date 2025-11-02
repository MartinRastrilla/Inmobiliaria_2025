package com.MartinRastrilla.inmobiliaria_2025.data.api;

import com.MartinRastrilla.inmobiliaria_2025.data.model.LoginRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.LoginResponse;
import com.MartinRastrilla.inmobiliaria_2025.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/Auth/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);
}
