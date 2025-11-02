package com.MartinRastrilla.inmobiliaria_2025.data.api;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.data.model.InmuebleRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Result;
import com.MartinRastrilla.inmobiliaria_2025.data.model.ToggleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface InmuebleService {
    // Listar todos los inmuebles del usuario logueado
    @GET("api/Inmueble")
    Call<List<Inmueble>> getInmuebles(@Header("Authorization") String token);

    // Ver detalles de un inmueble
    @GET("api/Inmueble/{id}")
    Call<Inmueble> getInmuebleById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    // Crear un inmueble
    @POST("api/Inmueble")
    Call<Inmueble> createInmueble(
            @Header("Authorization") String token,
            @Body InmuebleRequest request
    );

    // Habilitar/Deshabilitar inmueble
    @PUT("api/Inmueble/disable-enable/{id}")
    Call<ToggleResponse> toggleInmuebleAvailability(
            @Header("Authorization") String token,
            @Path("id") int id
    );
}
