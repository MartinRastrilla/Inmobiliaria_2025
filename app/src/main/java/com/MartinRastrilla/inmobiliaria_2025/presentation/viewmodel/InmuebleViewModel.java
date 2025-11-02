package com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.data.model.InmuebleRequest;
import com.MartinRastrilla.inmobiliaria_2025.data.repository.InmuebleRepository;

import java.util.List;

public class InmuebleViewModel extends AndroidViewModel {
    private InmuebleRepository inmuebleRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Inmueble>> inmueblesList = new MutableLiveData<>();
    private MutableLiveData<Inmueble> inmuebleDetail = new MutableLiveData<>();
    private MutableLiveData<Inmueble> inmuebleCreated = new MutableLiveData<>();
    private MutableLiveData<Inmueble> inmuebleToggled = new MutableLiveData<>();

    public InmuebleViewModel(@NonNull Application application) {
        super(application);
        inmuebleRepository = new InmuebleRepository(application);
    }

    public void loadInmuebles() {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        inmuebleRepository.getInmuebles(new InmuebleRepository.InmuebleCallback<List<Inmueble>>() {
            @Override
            public void onSuccess(List<Inmueble> data) {
                isLoading.postValue(false);
                inmueblesList.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public void loadInmuebleById(int id) {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        inmuebleRepository.getInmuebleById(id, new InmuebleRepository.InmuebleCallback<Inmueble>() {
            @Override
            public void onSuccess(Inmueble data) {
                isLoading.postValue(false);
                inmuebleDetail.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public void createInmueble(InmuebleRequest request, List<Uri> imageUris) {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        inmuebleRepository.createInmueble(request, imageUris, new InmuebleRepository.InmuebleCallback<Inmueble>() {
            @Override
            public void onSuccess(Inmueble data) {
                isLoading.postValue(false);
                inmuebleCreated.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public void toggleInmuebleAvailability(int id) {
        isLoading.postValue(true);
        errorMessage.postValue(null);

        inmuebleRepository.toggleInmuebleAvailability(id, new InmuebleRepository.InmuebleCallback<Inmueble>() {
            @Override
            public void onSuccess(Inmueble data) {
                isLoading.postValue(false);
                inmuebleToggled.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Getters para LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Inmueble>> getInmueblesList() {
        return inmueblesList;
    }

    public LiveData<Inmueble> getInmuebleDetail() {
        return inmuebleDetail;
    }

    public LiveData<Inmueble> getInmuebleCreated() {
        return inmuebleCreated;
    }

    public LiveData<Inmueble> getInmuebleToggled() {
        return inmuebleToggled;
    }
}
