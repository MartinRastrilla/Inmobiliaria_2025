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

    // Métodos estáticos para mensajes
    public static String getToggleMessage(Inmueble inmueble) {
        return inmueble.isAvailable()
                ? "La propiedad ahora está disponible"
                : "La propiedad no está disponible ahora";
    }

    public static String getToggleConfirmationMessage(Inmueble inmueble) {
        return inmueble.isAvailable()
                ? "¿Estás seguro de que deseas deshabilitar esta propiedad?"
                : "¿Estás seguro de que deseas habilitar esta propiedad?";
    }

    // Clase para resultado de validación
    public static class ValidationResult {
        private boolean isValid;
        private String titleError;
        private String addressError;
        private String roomsError;
        private String priceError;
        private String maxGuestsError;

        public ValidationResult() {
            this.isValid = true;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean valid) {
            isValid = valid;
        }

        public String getTitleError() {
            return titleError;
        }

        public void setTitleError(String titleError) {
            this.titleError = titleError;
            this.isValid = false;
        }

        public String getAddressError() {
            return addressError;
        }

        public void setAddressError(String addressError) {
            this.addressError = addressError;
            this.isValid = false;
        }

        public String getRoomsError() {
            return roomsError;
        }

        public void setRoomsError(String roomsError) {
            this.roomsError = roomsError;
            this.isValid = false;
        }

        public String getPriceError() {
            return priceError;
        }

        public void setPriceError(String priceError) {
            this.priceError = priceError;
            this.isValid = false;
        }

        public String getMaxGuestsError() {
            return maxGuestsError;
        }

        public void setMaxGuestsError(String maxGuestsError) {
            this.maxGuestsError = maxGuestsError;
            this.isValid = false;
        }
    }

    // Método estático para validar inputs de propiedad
    public static ValidationResult validatePropertyInputs(
            String title, String address, String roomsStr, String priceStr, String maxGuestsStr) {
        ValidationResult result = new ValidationResult();

        if (title == null || title.trim().isEmpty()) {
            result.setTitleError("El título es requerido");
        }

        if (address == null || address.trim().isEmpty()) {
            result.setAddressError("La dirección es requerida");
        }

        if (roomsStr == null || roomsStr.trim().isEmpty()) {
            result.setRoomsError("El número de habitaciones es requerido");
        } else {
            try {
                int rooms = Integer.parseInt(roomsStr.trim());
                if (rooms <= 0) {
                    result.setRoomsError("El número de habitaciones debe ser mayor a 0");
                }
            } catch (NumberFormatException e) {
                result.setRoomsError("Número de habitaciones inválido");
            }
        }

        if (priceStr == null || priceStr.trim().isEmpty()) {
            result.setPriceError("El precio es requerido");
        } else {
            try {
                double price = Double.parseDouble(priceStr.trim());
                if (price <= 0) {
                    result.setPriceError("El precio debe ser mayor a 0");
                }
            } catch (NumberFormatException e) {
                result.setPriceError("Precio inválido");
            }
        }

        if (maxGuestsStr != null && !maxGuestsStr.trim().isEmpty()) {
            try {
                int maxGuests = Integer.parseInt(maxGuestsStr.trim());
                if (maxGuests <= 0) {
                    result.setMaxGuestsError("El número de huéspedes debe ser mayor a 0");
                }
            } catch (NumberFormatException e) {
                result.setMaxGuestsError("Número de huéspedes inválido");
            }
        }

        return result;
    }

    // Método para crear inmueble desde inputs
    public void createInmuebleFromInputs(
            String title, String address, String latitude, String longitude,
            String roomsStr, String priceStr, String maxGuestsStr, List<Uri> imageUris) {
        InmuebleRequest request = new InmuebleRequest();
        request.setTitle(title.trim());
        request.setAddress(address.trim());

        if (latitude != null && !latitude.trim().isEmpty()) {
            request.setLatitude(latitude.trim());
        }

        if (longitude != null && !longitude.trim().isEmpty()) {
            request.setLongitude(longitude.trim());
        }

        request.setRooms(Integer.parseInt(roomsStr.trim()));
        request.setPrice(Double.parseDouble(priceStr.trim()));

        if (maxGuestsStr != null && !maxGuestsStr.trim().isEmpty()) {
            request.setMaxGuests(Integer.parseInt(maxGuestsStr.trim()));
        }

        createInmueble(request, imageUris);
    }
}
