package com.MartinRastrilla.inmobiliaria_2025.data.repository;

import android.content.Context;
import android.util.Log;

import com.MartinRastrilla.inmobiliaria_2025.data.api.ApiClient;
import com.MartinRastrilla.inmobiliaria_2025.data.api.InquilinoService;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inquilino;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InquilinoRepository {
    private static final String TAG = "InquilinoRepo";
    private InquilinoService inquilinoService;
    private PreferencesHelper preferencesHelper;

    public InquilinoRepository(Context context) {
        this.inquilinoService = ApiClient.getInquilinoService();
        this.preferencesHelper = new PreferencesHelper(context);
    }

    public void getInquilinoById(int id, InquilinoCallback<Inquilino> callback) {
        String authToken = preferencesHelper.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "ERROR: Token es null o vacío");
            callback.onError("Error: Token de autenticación no encontrado");
            return;
        }

        String token = "Bearer " + authToken;
        Log.d(TAG, "Obteniendo inquilino con ID: " + id);

        inquilinoService.getInquilinoById(token, id).enqueue(new Callback<Inquilino>() {
            @Override
            public void onResponse(Call<Inquilino> call, Response<Inquilino> response) {
                Log.d(TAG, "Response recibido. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Inquilino inquilino = response.body();
                    Log.d(TAG, "Inquilino obtenido: " + inquilino.getId());
                    callback.onSuccess(inquilino);
                } else {
                    String errorMsg = "Error al obtener inquilino: " + response.code();

                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);

                            Gson gson = new Gson();
                            try {
                                ErrorMessage errorMessage = gson.fromJson(errorBody, ErrorMessage.class);
                                if (errorMessage.message != null) {
                                    errorMsg = errorMessage.message;
                                }
                            } catch (JsonSyntaxException e) {
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error leyendo errorBody", e);
                        }
                    }

                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Inquilino> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    public interface InquilinoCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    private static class ErrorMessage {
        String message;
    }
}