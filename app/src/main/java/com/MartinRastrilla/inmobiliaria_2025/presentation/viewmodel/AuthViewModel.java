package com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.MartinRastrilla.inmobiliaria_2025.data.model.LoginResponse;
import com.MartinRastrilla.inmobiliaria_2025.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                isLoading.setValue(false);
                loginResult.setValue(loginResponse);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void register(String name, String lastName, String email, String password, String phone, String documentNumber) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.register(name, lastName, email, password, phone, documentNumber, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                isLoading.setValue(false);
                loginResult.setValue(loginResponse);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void logout() {
        authRepository.logout();
    }

    // Getters para LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }
}
