package com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Inquilino;
import com.MartinRastrilla.inmobiliaria_2025.data.repository.InquilinoRepository;

public class InquilinoViewModel extends AndroidViewModel {
    private InquilinoRepository inquilinoRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Inquilino> inquilinoDetail = new MutableLiveData<>();

    public InquilinoViewModel(@NonNull Application application) {
        super(application);
        inquilinoRepository = new InquilinoRepository(application);
    }

    public void loadInquilinoById(int id) {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        inquilinoRepository.getInquilinoById(id, new InquilinoRepository.InquilinoCallback<Inquilino>() {
            @Override
            public void onSuccess(Inquilino data) {
                isLoading.postValue(false);
                inquilinoDetail.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Inquilino> getInquilinoDetail() {
        return inquilinoDetail;
    }
}