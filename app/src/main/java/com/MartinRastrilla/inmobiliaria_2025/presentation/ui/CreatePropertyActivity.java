package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.data.model.InmuebleRequest;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.InmuebleViewModel;

public class CreatePropertyActivity extends AppCompatActivity {
    private EditText etTitle, etAddress, etLatitude, etLongitude, etRooms, etPrice, etMaxGuests;
    private Button btnSave;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private InmuebleViewModel viewModel;
    private boolean isEditMode = false;
    private int propertyId = -1;
    private Inmueble currentProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_property);

        // Verificar si es modo edición
        isEditMode = getIntent().getBooleanExtra("isEditMode", false);
        propertyId = getIntent().getIntExtra("propertyId", -1);

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();

        if (isEditMode && propertyId != -1) {
            loadPropertyForEdit();
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvScreenTitle);
        etTitle = findViewById(R.id.etTitle);
        etAddress = findViewById(R.id.etAddress);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        etRooms = findViewById(R.id.etRooms);
        etPrice = findViewById(R.id.etPrice);
        etMaxGuests = findViewById(R.id.etMaxGuests);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        if (isEditMode) {
            tvTitle.setText("Editar Propiedad");
            btnSave.setText("Actualizar Propiedad");
        } else {
            tvTitle.setText("Crear Propiedad");
            btnSave.setText("Crear Propiedad");
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(InmuebleViewModel.class);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getInmuebleDetail().observe(this, inmueble -> {
            if (inmueble != null && isEditMode) {
                currentProperty = inmueble;
                fillFormWithPropertyData(inmueble);
            }
        });

        viewModel.getInmuebleCreated().observe(this, inmueble -> {
            if (inmueble != null) {
                String message = isEditMode ? "Propiedad actualizada exitosamente" : "Propiedad creada exitosamente";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fillFormWithPropertyData(Inmueble inmueble) {
        etTitle.setText(inmueble.getTitle());
        etAddress.setText(inmueble.getAddress());
        if (inmueble.getLatitude() != null) {
            etLatitude.setText(inmueble.getLatitude());
        }
        if (inmueble.getLongitude() != null) {
            etLongitude.setText(inmueble.getLongitude());
        }
        etRooms.setText(String.valueOf(inmueble.getRooms()));
        etPrice.setText(String.valueOf(inmueble.getPrice()));
        if (inmueble.getMaxGuests() != null) {
            etMaxGuests.setText(String.valueOf(inmueble.getMaxGuests()));
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveProperty();
            }
        });
    }

    private boolean validateInput() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("El título es requerido");
            etTitle.requestFocus();
            return false;
        }

        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("La dirección es requerida");
            etAddress.requestFocus();
            return false;
        }

        if (etRooms.getText().toString().trim().isEmpty()) {
            etRooms.setError("El número de habitaciones es requerido");
            etRooms.requestFocus();
            return false;
        }

        try {
            int rooms = Integer.parseInt(etRooms.getText().toString().trim());
            if (rooms <= 0) {
                etRooms.setError("El número de habitaciones debe ser mayor a 0");
                etRooms.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etRooms.setError("Número de habitaciones inválido");
            etRooms.requestFocus();
            return false;
        }

        if (etPrice.getText().toString().trim().isEmpty()) {
            etPrice.setError("El precio es requerido");
            etPrice.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(etPrice.getText().toString().trim());
            if (price <= 0) {
                etPrice.setError("El precio debe ser mayor a 0");
                etPrice.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Precio inválido");
            etPrice.requestFocus();
            return false;
        }

        // MaxGuests es opcional, pero si se completa debe ser válido
        if (!etMaxGuests.getText().toString().trim().isEmpty()) {
            try {
                int maxGuests = Integer.parseInt(etMaxGuests.getText().toString().trim());
                if (maxGuests <= 0) {
                    etMaxGuests.setError("El número de huéspedes debe ser mayor a 0");
                    etMaxGuests.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                etMaxGuests.setError("Número de huéspedes inválido");
                etMaxGuests.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void saveProperty() {
        InmuebleRequest request = new InmuebleRequest();
        request.setTitle(etTitle.getText().toString().trim());
        request.setAddress(etAddress.getText().toString().trim());

        String latitude = etLatitude.getText().toString().trim();
        if (!latitude.isEmpty()) {
            request.setLatitude(latitude);
        }

        String longitude = etLongitude.getText().toString().trim();
        if (!longitude.isEmpty()) {
            request.setLongitude(longitude);
        }

        request.setRooms(Integer.parseInt(etRooms.getText().toString().trim()));
        request.setPrice(Double.parseDouble(etPrice.getText().toString().trim()));

        String maxGuestsStr = etMaxGuests.getText().toString().trim();
        if (!maxGuestsStr.isEmpty()) {
            request.setMaxGuests(Integer.parseInt(maxGuestsStr));
        }

        if (isEditMode) {
            // TODO: Implementar actualización cuando tengas el endpoint PUT
            // Por ahora, solo permitimos crear
            Toast.makeText(this, "La edición se implementará próximamente", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.createInmueble(request);
        }
    }

    private void loadPropertyForEdit() {
        viewModel.loadInmuebleById(propertyId);
    }
}