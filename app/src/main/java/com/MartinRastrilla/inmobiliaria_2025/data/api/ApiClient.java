package com.MartinRastrilla.inmobiliaria_2025.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.100.49:5275/";
    private static Retrofit retrofit;
    private static AuthService authService;
    private static UserService userService;
    private static InmuebleService inmuebleService;


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
}
