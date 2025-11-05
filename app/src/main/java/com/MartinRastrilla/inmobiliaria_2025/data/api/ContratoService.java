package com.MartinRastrilla.inmobiliaria_2025.data.api;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Contrato;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inquilino;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Pago;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ContratoService {
    @GET("api/Contrato")
    Call<List<Contrato>> getContratos(@Header("Authorization") String token);

    @GET("api/Contrato/{id}")
    Call<Contrato> getContratoById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @GET("api/Inquilino/{id}")
    Call<Inquilino> getInquilinoById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @GET("api/Pago/contrato/{contratoId}")
    Call<List<Pago>> getPagosByContratoId(
            @Header("Authorization") String token,
            @Path("contratoId") int contratoId
    );
}
