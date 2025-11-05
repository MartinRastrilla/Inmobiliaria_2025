package com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Pago;
import com.MartinRastrilla.inmobiliaria_2025.data.repository.PagoRepository;

import java.util.List;

public class PagoViewModel extends AndroidViewModel {
    private PagoRepository pagoRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Pago>> pagosList = new MutableLiveData<>();

    public PagoViewModel(@NonNull Application application) {
        super(application);
        pagoRepository = new PagoRepository(application);
    }

    public void loadPagosByContratoId(int contratoId) {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        pagoRepository.getPagosByContratoId(contratoId, new PagoRepository.PagoCallback<List<Pago>>() {
            @Override
            public void onSuccess(List<Pago> data) {
                isLoading.postValue(false);
                pagosList.postValue(data);
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

    public LiveData<List<Pago>> getPagosList() {
        return pagosList;
    }
}