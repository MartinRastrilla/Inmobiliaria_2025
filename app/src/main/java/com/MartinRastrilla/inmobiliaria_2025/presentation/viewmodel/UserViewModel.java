package com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.MartinRastrilla.inmobiliaria_2025.data.model.LoginResponse;
import com.MartinRastrilla.inmobiliaria_2025.data.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> updateResult = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void updateProfile(
            String name, String lastName, String email, String password,
            String phone, String documentNumber, List<String> roles, Uri imageUri
    ) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.updateProfile(
                name, lastName, email, password,
                phone, documentNumber, roles, imageUri,
                new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(LoginResponse reponse) {
                        isLoading.setValue(false);
                        updateResult.setValue(reponse);
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.setValue(false);
                        errorMessage.setValue(error);
                    }
                }
        );
    }

    public void updatePassword(String currentPassword, String newPassword, String confirmPassword) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.updatePassword(
                currentPassword, newPassword, confirmPassword,
                new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        isLoading.setValue(false);
                        updateResult.setValue(response);
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.setValue(false);
                        errorMessage.setValue(error);
                    }
                }
        );
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<LoginResponse> getUpdateResult() {
        return updateResult;
    }
}
