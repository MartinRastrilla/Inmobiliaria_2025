package com.MartinRastrilla.inmobiliaria_2025.data.repository;

import android.content.Context;
import android.util.Log;

import com.MartinRastrilla.inmobiliaria_2025.data.api.ApiClient;
import com.MartinRastrilla.inmobiliaria_2025.data.api.InmuebleService;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.data.model.InmuebleRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Result;
import com.MartinRastrilla.inmobiliaria_2025.data.model.ToggleResponse;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmuebleRepository {
    private static final String TAG = "InmuebleRepo";
    private InmuebleService inmuebleService;
    private PreferencesHelper preferencesHelper;

    public InmuebleRepository(Context context) {
        this.inmuebleService = ApiClient.getInmuebleService();
        this.preferencesHelper = new PreferencesHelper(context);
    }

    public void getInmuebles(InmuebleCallback<List<Inmueble>> callback) {
        String authToken = preferencesHelper.getAuthToken();
        
        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "ERROR: Token es null o vacío");
            callback.onError("Error: Token de autenticación no encontrado");
            return;
        }
        
        String token = "Bearer " + authToken;
        Log.d(TAG, "Intentando obtener inmuebles...");

        inmuebleService.getInmuebles(token).enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                Log.d(TAG, "Response recibido. Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Inmueble> inmuebles = response.body();
                    Log.d(TAG, "Inmuebles obtenidos: " + inmuebles.size());
                    callback.onSuccess(inmuebles);
                } else {
                    String errorMsg = "Error al obtener inmuebles: " + response.code();
                    
                    // Intentar leer el error body
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMsg += "\n" + errorBody;
                        } catch (IOException e) {
                            Log.e(TAG, "Error leyendo errorBody", e);
                        }
                    }
                    
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                
                // Log completo del stack trace
                if (t.getCause() != null) {
                    Log.e(TAG, "Causa: " + t.getCause().getMessage(), t.getCause());
                }
                
                callback.onError(errorMsg);
            }
        });
    }

    public void getInmuebleById(int id, InmuebleCallback<Inmueble> callback) {
        String authToken = preferencesHelper.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "ERROR: Token es null o vacío");
            callback.onError("Error: Token de autenticación no encontrado");
            return;
        }

        String token = "Bearer " + authToken;
        Log.d(TAG, "Obteniendo inmueble con ID: " + id);

        inmuebleService.getInmuebleById(token, id).enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                Log.d(TAG, "Response recibido. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Inmueble inmueble = response.body();  // ✅ Ahora es directamente Inmueble
                    Log.d(TAG, "Inmueble obtenido: " + inmueble.getTitle());
                    callback.onSuccess(inmueble);  // ✅ Pasar directamente
                } else {
                    String errorMsg = "Error al obtener inmueble: " + response.code();

                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMsg += "\n" + errorBody;
                        } catch (IOException e) {
                            Log.e(TAG, "Error leyendo errorBody", e);
                        }
                    }

                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Inmueble> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    public void createInmueble(InmuebleRequest request, InmuebleCallback<Inmueble> callback) {
        String token = "Bearer " + preferencesHelper.getAuthToken();

        inmuebleService.createInmueble(token, request).enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Inmueble inmueble = response.body();
                    callback.onSuccess(inmueble);
                } else {
                    String errorMsg = "Error al crear inmueble: " + response.code();

                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMsg += "\n" + errorBody;
                        } catch (IOException e) {
                            Log.e(TAG, "Error leyendo errorBody", e);
                        }
                    }

                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Inmueble> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void toggleInmuebleAvailability(int id, InmuebleCallback<Inmueble> callback) {
        String authToken = preferencesHelper.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "ERROR: Token es null o vacío");
            callback.onError("Error: Token de autenticación no encontrado");
            return;
        }

        String token = "Bearer " + authToken;
        Log.d(TAG, "Toggleando disponibilidad del inmueble ID: " + id);

        inmuebleService.toggleInmuebleAvailability(token, id).enqueue(new Callback<ToggleResponse>() {
            @Override
            public void onResponse(Call<ToggleResponse> call, Response<ToggleResponse> response) {
                Log.d(TAG, "Response recibido. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ToggleResponse toggleResponse = response.body();
                    Log.d(TAG, "Toggle exitoso: " + toggleResponse.getMessage());

                    // El problema es que el callback espera un Inmueble, pero solo tenemos un boolean
                    // Necesitamos recargar el inmueble después del toggle
                    // Por ahora, llamamos a getInmuebleById para obtener el inmueble actualizado
                    getInmuebleById(id, callback);
                } else {
                    String errorMsg = "Error al cambiar estado: " + response.code();

                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMsg += "\n" + errorBody;
                        } catch (IOException e) {
                            Log.e(TAG, "Error leyendo errorBody", e);
                        }
                    }

                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ToggleResponse> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    public interface InmuebleCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}
