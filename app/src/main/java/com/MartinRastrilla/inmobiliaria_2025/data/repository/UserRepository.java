package com.MartinRastrilla.inmobiliaria_2025.data.repository;

import android.content.Context;
import android.net.Uri;

import com.MartinRastrilla.inmobiliaria_2025.data.api.ApiClient;
import com.MartinRastrilla.inmobiliaria_2025.data.api.UserService;
import com.MartinRastrilla.inmobiliaria_2025.data.model.ChangePasswordRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.LoginResponse;
import com.MartinRastrilla.inmobiliaria_2025.data.model.UpdateProfileResponse;
import com.MartinRastrilla.inmobiliaria_2025.data.model.UserResponse;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private UserService userService;
    private PreferencesHelper preferencesHelper;
    private Context context;

    public UserRepository(Context context) {
        this.userService = ApiClient.getUserService();
        this.preferencesHelper = new PreferencesHelper(context);
        this.context = context;
    }

    public void updateProfile(
            String name, String lastName, String email, String password,
            String phone, String documentNumber, List<String> roles,
            Uri imageUri,
            UserCallback callback
    ) {
        String token = "Bearer " + preferencesHelper.getAuthToken();

        RequestBody namePart = createPartFromString(name);
        RequestBody lastNamePart = createPartFromString(lastName);
        RequestBody emailPart = createPartFromString(email);
        RequestBody passwordPart = password != null && !password.isEmpty()
                ? createPartFromString(password)
                : createPartFromString("");

        RequestBody phonePart = phone != null && !phone.isEmpty()
                ? createPartFromString(phone)
                : createPartFromString("");

        RequestBody documentNumberPart = documentNumber != null && !documentNumber.isEmpty()
                ? createPartFromString(documentNumber)
                : createPartFromString("");

        RequestBody profilePicRoutePart = createPartFromString("");

        RequestBody rolesPart = createPartFromString("");

        MultipartBody.Part photoPart = null;
        if (imageUri != null) {
            File imageFile = uriToFile(imageUri);
            if (imageFile != null && imageFile.exists()) {
                RequestBody requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        imageFile
                );
                photoPart = MultipartBody.Part.createFormData(
                        "ProfilePic",
                        imageFile.getName(),
                        requestFile
                );
            }
        }

        userService.updateProfile(
                token, namePart, lastNamePart, emailPart, passwordPart,
                phonePart, documentNumberPart, profilePicRoutePart, rolesPart, photoPart
        ).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UpdateProfileResponse updateResponse = response.body();
                    UserResponse userData = updateResponse.getData();

                    preferencesHelper.saveFullUserData(
                            preferencesHelper.getUserId() != null ? preferencesHelper.getUserId() : "",
                            userData.getName(),
                            userData.getLastName(),
                            userData.getEmail(),
                            userData.getPhone(),
                            userData.getDocumentNumber(),
                            userData.getFirstRole(),
                            userData.getProfilePicRoute()
                    );

                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setToken(preferencesHelper.getAuthToken());

                    callback.onSuccess(loginResponse);
                } else {
                    callback.onError("Error al actualizar perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void updatePassword(
            String currentPassword, String newPassword, String confirmPassword,
            UserCallback callback
    ) {
        String token = "Bearer " + preferencesHelper.getAuthToken();

        ChangePasswordRequest request = new ChangePasswordRequest(
                currentPassword,
                newPassword,
                confirmPassword
        );

        userService.updatePassword(token, request).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UpdateProfileResponse updateResponse = response.body();
                    UserResponse userData = updateResponse.getData();

                    preferencesHelper.saveFullUserData(
                            preferencesHelper.getUserId() != null ? preferencesHelper.getUserId() : "",
                            userData.getName(),
                            userData.getLastName(),
                            userData.getEmail(),
                            userData.getPhone(),
                            userData.getDocumentNumber(),
                            userData.getFirstRole(),
                            userData.getProfilePicRoute()
                    );

                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setToken(preferencesHelper.getAuthToken());

                    callback.onSuccess(loginResponse);
                } else {
                    callback.onError("Error al actualizar contraseña: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void getCurrentUser(UserCallback callback) {
        String token = "Bearer " + preferencesHelper.getAuthToken();

        userService.getCurrentUser(token).enqueue(new Callback<UserResponse>() {
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
                    loginResponse.setToken(preferencesHelper.getAuthToken());

                    callback.onSuccess(loginResponse);
                } else {
                    callback.onError("Error al obtener perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }
    private File uriToFile(Uri uri) {
        if (uri == null) {
            return null;
        }

        try {
            File tempFile = File.createTempFile("profile_", ".jpg", context.getCacheDir());
            tempFile.deleteOnExit();

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public interface UserCallback {
        void onSuccess(LoginResponse reponse);
        void onError(String error);
    }
}
