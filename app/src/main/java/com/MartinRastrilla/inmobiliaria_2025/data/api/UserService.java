package com.MartinRastrilla.inmobiliaria_2025.data.api;

import com.MartinRastrilla.inmobiliaria_2025.data.model.ChangePasswordRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.model.UpdateProfileResponse;
import com.MartinRastrilla.inmobiliaria_2025.data.model.UserResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserService {

    @GET("api/User/me")
    Call<UserResponse> getCurrentUser(@Header("Authorization") String token);

    @Multipart
    @PUT("api/User/me")
    Call<UpdateProfileResponse> updateProfile(
            @Header("Authorization") String token,
            @Part("Name") RequestBody name,
            @Part("LastName") RequestBody lastName,
            @Part("Email") RequestBody email,
            @Part("Password") RequestBody password,
            @Part("Phone") RequestBody phone,
            @Part("DocumentNumber") RequestBody documentNumber,
            @Part("ProfilePicRoute") RequestBody profilePicRoute,
            @Part("Roles") RequestBody rolesJson,
            @Part MultipartBody.Part profilePic
    );

    @PUT("api/User/me/password")
    Call<UpdateProfileResponse> updatePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest request
    );
}
