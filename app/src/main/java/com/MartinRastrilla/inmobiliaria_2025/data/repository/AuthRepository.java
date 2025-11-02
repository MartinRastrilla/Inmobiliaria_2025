package com.MartinRastrilla.inmobiliaria_2025.data.repository;

import android.content.Context;

import com.MartinRastrilla.inmobiliaria_2025.data.api.ApiClient;
import com.MartinRastrilla.inmobiliaria_2025.data.api.AuthService;
import com.MartinRastrilla.inmobiliaria_2025.data.model.LoginRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.LoginResponse;
import com.MartinRastrilla.inmobiliaria_2025.data.model.RegisterRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.UserResponse;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private AuthService authService;
    private PreferencesHelper preferencesHelper;

    private String getErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                // Parsear JSON simple
                if (errorBody.contains("\"message\"")) {
                    int start = errorBody.indexOf("\"message\"");
                    int messageStart = errorBody.indexOf("\"", start + 10) + 1;
                    int messageEnd = errorBody.indexOf("\"", messageStart);
                    return errorBody.substring(messageStart, messageEnd);
                }
            }
        } catch (Exception e) {
            // Si falla el parseo, mostrar código de error
        }
        return "Error " + response.code();
    }

    public AuthRepository(Context context) {
        this.authService = ApiClient.getAuthService();
        this.preferencesHelper = new PreferencesHelper(context);
    }

    public void login(String email, String password, AuthCallback callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();

                    preferencesHelper.saveAuthToken(token);

                    getUserProfile(token, callback);

                } else {
                    String errorMessage = getErrorMessage(response);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void register(String name, String lastName, String email, String password, String phone, String documentNumber, AuthCallback callback) {
        RegisterRequest registerRequest = new RegisterRequest(name, lastName, email, password, phone, documentNumber);

        authService.register(registerRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();

                    preferencesHelper.saveAuthToken(token);
                    getUserProfile(token, callback);
                } else {
                    String errorMessage = getErrorMessage(response);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void logout() {
        preferencesHelper.clearUserData();
    }

    public boolean isLoggedIn() {
        return preferencesHelper.isLoggedIn();
    }

    public String getAuthToken() {
        return preferencesHelper.getAuthToken();
    }

    public interface AuthCallback {
        void onSuccess(LoginResponse loginResponse);
        void onError(String error);
    }

    private void getUserProfile(String token, AuthCallback callback) {
        String authToken = "Bearer " + token;
        ApiClient.getUserService().getCurrentUser(authToken).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();

                    preferencesHelper.saveFullUserData(
                            preferencesHelper.getUserId() != null ? preferencesHelper.getUserId() : "",
                            userResponse.getName(),
                            userResponse.getLastName(),
                            userResponse.getEmail(),
                            userResponse.getPhone(),
                            userResponse.getDocumentNumber(),
                            userResponse.getFirstRole(),
                            userResponse.getProfilePicRoute()
                    );

                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setToken(token);

                    callback.onSuccess(loginResponse);
                } else {
                    callback.onError("Error al obtener datos del usuario: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Error al obtener datos del usuario: " + t.getMessage());
            }
        });
    }
}
