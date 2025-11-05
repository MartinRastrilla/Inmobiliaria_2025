package com.MartinRastrilla.inmobiliaria_2025.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.100.49:5275/";
    private static Retrofit retrofit;
    private static AuthService authService;
    private static UserService userService;
    private static InmuebleService inmuebleService;
    private static ContratoService contratoService;
    private static InquilinoService inquilinoService;
    private static PagoService pagoService;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthService getAuthService() {
        if (authService == null) {
            authService = getRetrofitInstance().create(AuthService.class);
        }
        return authService;
    }

    public static UserService getUserService() {
        if (userService == null) {
            userService = getRetrofitInstance().create(UserService.class);
        }
        return userService;
    }

    public static InmuebleService getInmuebleService() {
        if (inmuebleService == null) {
            inmuebleService = getRetrofitInstance().create(InmuebleService.class);
        }
        return inmuebleService;
    }

    public static ContratoService getContratoService() {
        if (contratoService == null) {
            contratoService = getRetrofitInstance().create(ContratoService.class);
        }
        return contratoService;
    }

    public static InquilinoService getInquilinoService() {
        if (inquilinoService == null) {
            inquilinoService = getRetrofitInstance().create(InquilinoService.class);
        }
        return inquilinoService;
    }

    public static PagoService getPagoService() {
        if (pagoService == null) {
            pagoService = getRetrofitInstance().create(PagoService.class);
        }
        return pagoService;
    }
}
