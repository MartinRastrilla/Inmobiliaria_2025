package com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Contrato;
import com.MartinRastrilla.inmobiliaria_2025.data.repository.ContratoRepository;

import java.util.List;

public class ContratoViewModel extends AndroidViewModel {
    private ContratoRepository contratoRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Contrato>> contratosList = new MutableLiveData<>();
    private MutableLiveData<Contrato> contratoDetail = new MutableLiveData<>();

    public ContratoViewModel(@NonNull Application application) {
        super(application);
        contratoRepository = new ContratoRepository(application);
    }

    public void loadContratos() {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        contratoRepository.getContratos(new ContratoRepository.ContratoCallback<List<Contrato>>() {
            @Override
            public void onSuccess(List<Contrato> data) {
                isLoading.postValue(false);
                contratosList.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public void loadContratoById(int id) {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        contratoRepository.getContratoById(id, new ContratoRepository.ContratoCallback<Contrato>() {
            @Override
            public void onSuccess(Contrato data) {
                isLoading.postValue(false);
                contratoDetail.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Getters para LiveData
    public LiveData<Contrato> getContratoDetail() {
        return contratoDetail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Contrato>> getContratosList() {
        return contratosList;
    }
}
