package com.MartinRastrilla.inmobiliaria_2025.data.api;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Pago;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface PagoService {
    @GET("api/Pago/contrato/{contratoId}")
    Call<List<Pago>> getPagosByContratoId(
            @Header("Authorization") String token,
            @Path("contratoId") int contratoId
    );
}
