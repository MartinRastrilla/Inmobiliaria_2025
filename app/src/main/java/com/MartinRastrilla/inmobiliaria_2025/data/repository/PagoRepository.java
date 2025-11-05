package com.MartinRastrilla.inmobiliaria_2025.data.repository;

import android.content.Context;
import android.util.Log;

import com.MartinRastrilla.inmobiliaria_2025.data.api.ApiClient;
import com.MartinRastrilla.inmobiliaria_2025.data.api.PagoService;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Pago;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagoRepository {
    private static final String TAG = "PagoRepo";
    private PagoService pagoService;
    private PreferencesHelper preferencesHelper;

    public PagoRepository(Context context) {
        this.pagoService = ApiClient.getPagoService();
        this.preferencesHelper = new PreferencesHelper(context);
    }

    public void getPagosByContratoId(int contratoId, PagoCallback<List<Pago>> callback) {
        String authToken = preferencesHelper.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "ERROR: Token es null o vacío");
            callback.onError("Error: Token de autenticación no encontrado");
            return;
        }

        String token = "Bearer " + authToken;
        Log.d(TAG, "Intentando obtener pagos para contrato ID: " + contratoId);

        pagoService.getPagosByContratoId(token, contratoId).enqueue(new Callback<List<Pago>>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                Log.d(TAG, "Response recibido. Code: " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Pago> pagos = response.body();
                        Log.d(TAG, "Pagos obtenidos: " + pagos.size());
                        callback.onSuccess(pagos);
                    } else {
                        Log.d(TAG, "No hay pagos (array vacío)");
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.d(TAG, "Error body: " + errorBody);

                            if (errorBody.contains("No se encontraron pagos")) {
                                Log.d(TAG, "No hay pagos para este contrato");
                                callback.onSuccess(new ArrayList<>());
                                return;
                            }

                            Gson gson = new Gson();
                            try {
                                ErrorMessage errorMsg = gson.fromJson(errorBody, ErrorMessage.class);
                                if (errorMsg.message != null && errorMsg.message.contains("No se encontraron pagos")) {
                                    Log.d(TAG, "No hay pagos para este contrato");
                                    callback.onSuccess(new ArrayList<>());
                                    return;
                                }
                            } catch (JsonSyntaxException e) {
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error leyendo errorBody", e);
                        }
                    }

                    String errorMsg = "Error al obtener pagos: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);

                if (t.getCause() != null) {
                    Log.e(TAG, "Causa: " + t.getCause().getMessage(), t.getCause());
                }

                callback.onError(errorMsg);
            }
        });
    }

    public interface PagoCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    private static class ErrorMessage {
        String message;
    }
}