package com.MartinRastrilla.inmobiliaria_2025.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.MartinRastrilla.inmobiliaria_2025.data.api.ApiClient;
import com.MartinRastrilla.inmobiliaria_2025.data.api.InmuebleService;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.data.model.InmuebleRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Result;
import com.MartinRastrilla.inmobiliaria_2025.data.model.ToggleResponse;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmuebleRepository {
    private static final String TAG = "InmuebleRepo";
    private InmuebleService inmuebleService;
    private PreferencesHelper preferencesHelper;
    private Context context;
    private static final DecimalFormat PRICE_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        PRICE_FORMAT = new DecimalFormat("0.##", symbols);
        PRICE_FORMAT.setGroupingUsed(false);
    }

    public InmuebleRepository(Context context) {
        this.inmuebleService = ApiClient.getInmuebleService();
        this.preferencesHelper = new PreferencesHelper(context);
        this.context = context;
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
                    String errorMsg = extractErrorMessage(response);
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                
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
                    Inmueble inmueble = response.body();
                    Log.d(TAG, "Inmueble obtenido: " + inmueble.getTitle());
                    callback.onSuccess(inmueble);
                } else {
                    String errorMsg = extractErrorMessage(response);
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

    public void createInmueble(InmuebleRequest request, List<Uri> imageUris, InmuebleCallback<Inmueble> callback) {
        String token = "Bearer " + preferencesHelper.getAuthToken();

        try {
            RequestBody titlePart = createPartFromString(request.getTitle());
            RequestBody addressPart = createPartFromString(request.getAddress());
            RequestBody latitudePart = createPartFromString(request.getLatitude() != null ? request.getLatitude() : "");
            RequestBody longitudePart = createPartFromString(request.getLongitude() != null ? request.getLongitude() : "");
            RequestBody roomsPart = createPartFromString(String.valueOf(request.getRooms()));
            String priceValue = PRICE_FORMAT.format(request.getPrice());
            RequestBody pricePart = createPartFromString(priceValue);
            RequestBody maxGuestsPart = createPartFromString(request.getMaxGuests() != null ? String.valueOf(request.getMaxGuests()) : "");

            List<MultipartBody.Part> imageParts = new ArrayList<>();
            if (imageUris != null && !imageUris.isEmpty()) {
                for (Uri imageUri : imageUris) {
                    File imageFile = uriToFile(imageUri);
                    if (imageFile != null && imageFile.exists()) {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("Images", imageFile.getName(), requestFile);
                        imageParts.add(imagePart);
                    }
                }
            }

            inmuebleService.createInmueble(
                    token,
                    titlePart,
                    addressPart,
                    latitudePart,
                    longitudePart,
                    roomsPart,
                    pricePart,
                    maxGuestsPart,
                    imageParts
            ).enqueue(new Callback<Inmueble>() {
                @Override
                public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Inmueble inmueble = response.body();
                        callback.onSuccess(inmueble);
                    } else {
                        String errorMsg = extractErrorMessage(response);
                        callback.onError(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<Inmueble> call, Throwable t) {
                    callback.onError("Error de conexión: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error preparando request", e);
            callback.onError("Error preparando la solicitud: " + e.getMessage());
        }
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }
            File tempFile = File.createTempFile("image_", ".jpg", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (IOException e) {
            Log.e(TAG, "Error convirtiendo URI a File", e);
            return null;
        }
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
                    getInmuebleById(id, callback);
                } else {
                    String errorMsg = extractErrorMessage(response);
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

    private <T> String extractErrorMessage(Response<T> response) {
        String defaultMsg = "Error: " + response.code();
        
        if (response.errorBody() != null) {
            try {
                String errorBody = response.errorBody().string();
                Log.d(TAG, "Error body: " + errorBody);
                
                // Intentar parsear el JSON para extraer el mensaje
                Gson gson = new Gson();
                try {
                    ErrorMessage errorMessage = gson.fromJson(errorBody, ErrorMessage.class);
                    if (errorMessage.message != null && !errorMessage.message.isEmpty()) {
                        return errorMessage.message;
                    }
                } catch (JsonSyntaxException e) {
                    Log.d(TAG, "No se pudo parsear el JSON, usando mensaje por defecto");
                }
            } catch (IOException e) {
                Log.e(TAG, "Error leyendo errorBody", e);
            }
        }
        
        return defaultMsg;
    }

    public interface InmuebleCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    private static class ErrorMessage {
        String message;
    }
}
