package com.MartinRastrilla.inmobiliaria_2025.data.repository;

import android.content.Context;
import android.util.Log;

import com.MartinRastrilla.inmobiliaria_2025.data.api.ApiClient;
import com.MartinRastrilla.inmobiliaria_2025.data.api.ContratoService;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Contrato;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContratoRepository {
    private static final String TAG = "ContratoRepo";
    private ContratoService contratoService;
    private PreferencesHelper preferencesHelper;

    public ContratoRepository(Context context) {
        this.contratoService = ApiClient.getContratoService();
        this.preferencesHelper = new PreferencesHelper(context);
    }

    public void getContratos(ContratoCallback<List<Contrato>> callback) {
        String authToken = preferencesHelper.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "ERROR: Token es null o vacío");
            callback.onError("Error: Token de autenticación no encontrado");
            return;
        }

        String token = "Bearer " + authToken;
        Log.d(TAG, "Intentando obtener contratos...");

        contratoService.getContratos(token).enqueue(new Callback<List<Contrato>>() {
            @Override
            public void onResponse(Call<List<Contrato>> call, Response<List<Contrato>> response) {
                Log.d(TAG, "Response recibido. Code: " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Contrato> contratos = response.body();
                        Log.d(TAG, "Contratos obtenidos: " + contratos.size());
                        callback.onSuccess(contratos);
                    } else {
                        Log.d(TAG, "No hay contratos (array vacío)");
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.d(TAG, "Error body: " + errorBody);

                            if (errorBody.contains("No se encontraron contratos")) {
                                Log.d(TAG, "No hay contratos para este propietario");
                                callback.onSuccess(new ArrayList<>());
                                return;
                            }

                            Gson gson = new Gson();
                            try {
                                ErrorMessage errorMsg = gson.fromJson(errorBody, ErrorMessage.class);
                                if (errorMsg.message != null && errorMsg.message.contains("No se encontraron contratos")) {
                                    Log.d(TAG, "No hay contratos para este propietario");
                                    callback.onSuccess(new ArrayList<>());
                                    return;
                                }
                            } catch (JsonSyntaxException e) {
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error leyendo errorBody", e);
                        }
                    }

                    String errorMsg = "Error al obtener contratos: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Contrato>> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);

                if (t.getCause() != null) {
                    Log.e(TAG, "Causa: " + t.getCause().getMessage(), t.getCause());
                }

                callback.onError(errorMsg);
            }
        });
    }

    public void getContratoById(int id, ContratoCallback<Contrato> callback) {
        String authToken = preferencesHelper.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "ERROR: Token es null o vacío");
            callback.onError("Error: Token de autenticación no encontrado");
            return;
        }

        String token = "Bearer " + authToken;
        Log.d(TAG, "Obteniendo contrato con ID: " + id);

        contratoService.getContratoById(token, id).enqueue(new Callback<Contrato>() {
            @Override
            public void onResponse(Call<Contrato> call, Response<Contrato> response) {
                Log.d(TAG, "Response recibido. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Contrato contrato = response.body();
                    Log.d(TAG, "Contrato obtenido: " + contrato.getId());
                    callback.onSuccess(contrato);
                } else {
                    String errorMsg = "Error al obtener contrato: " + response.code();

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
            public void onFailure(Call<Contrato> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    public interface ContratoCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    private static class ErrorMessage {
        String message;
    }
}
