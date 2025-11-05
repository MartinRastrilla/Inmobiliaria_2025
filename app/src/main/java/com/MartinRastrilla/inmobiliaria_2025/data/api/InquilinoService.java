package com.MartinRastrilla.inmobiliaria_2025.data.api;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Inquilino;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface InquilinoService {
    @GET("api/Inquilino/{id}")
    Call<Inquilino> getInquilinoById(
            @Header("Authorization") String token,
            @Path("id") int id
    );
}
