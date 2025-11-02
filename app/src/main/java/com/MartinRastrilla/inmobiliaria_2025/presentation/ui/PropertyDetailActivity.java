package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.InmuebleViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class PropertyDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvAddress, tvRooms, tvPrice, tvMaxGuests, tvStatus, tvCreatedAt;
    private Button btnToggleAvailability, btnEditProperty;
    private ProgressBar progressBar;
    private InmuebleViewModel viewModel;
    private int propertyId;
    private Inmueble currentProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_property_detail);

        propertyId = getIntent().getIntExtra("propertyId", -1);
        if (propertyId == -1) {
            Toast.makeText(this, "Error: ID de propiedad no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
        loadPropertyDetails();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvAddress = findViewById(R.id.tvAddress);
        tvRooms = findViewById(R.id.tvRooms);
        tvPrice = findViewById(R.id.tvPrice);
        tvMaxGuests = findViewById(R.id.tvMaxGuests);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        btnToggleAvailability = findViewById(R.id.btnToggleAvailability);
        btnEditProperty = findViewById(R.id.btnEditProperty);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(InmuebleViewModel.class);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnToggleAvailability.setEnabled(!isLoading);
            btnEditProperty.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getInmuebleDetail().observe(this, inmueble -> {
            if (inmueble != null) {
                currentProperty = inmueble;
                displayPropertyDetails(inmueble);
            }
        });

        viewModel.getInmuebleToggled().observe(this, inmueble -> {
            if (inmueble != null) {
                currentProperty = inmueble;
                displayPropertyDetails(inmueble);
                Toast.makeText(this, "Estado actualizado correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPropertyDetails(Inmueble inmueble) {
        tvTitle.setText(inmueble.getTitle());
        tvAddress.setText(inmueble.getAddress());

        // Habitaciones
        String roomsText = inmueble.getRooms() + " habitación" + (inmueble.getRooms() > 1 ? "es" : "");
        tvRooms.setText(roomsText);

        // Precio
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        tvPrice.setText(formatter.format(inmueble.getPrice()));

        // Máximo de huéspedes
        if (inmueble.getMaxGuests() != null) {
            tvMaxGuests.setText(inmueble.getMaxGuests() + " huéspedes máximo");
            tvMaxGuests.setVisibility(View.VISIBLE);
        } else {
            tvMaxGuests.setVisibility(View.GONE);
        }

        // Estado
        if (inmueble.isAvailable()) {
            tvStatus.setText("Disponible");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnToggleAvailability.setText("Deshabilitar Propiedad");
        } else {
            tvStatus.setText("No Disponible");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnToggleAvailability.setText("Habilitar Propiedad");
        }

        // Fecha de creación
        if (inmueble.getCreatedAt() != null) {
            tvCreatedAt.setText("Creado: " + formatDate(inmueble.getCreatedAt()));
        }
    }

    private String formatDate(String dateString) {
        try {
            // Formato: "2025-01-15T10:30:00" -> "15/01/2025"
            String datePart = dateString.split("T")[0];
            String[] parts = datePart.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return dateString;
        }
    }

    private void setupClickListeners() {
        btnToggleAvailability.setOnClickListener(v -> {
            if (currentProperty != null) {
                showToggleConfirmationDialog();
            }
        });

        btnEditProperty.setOnClickListener(v -> {
            if (currentProperty != null) {
                Intent intent = new Intent(PropertyDetailActivity.this, CreatePropertyActivity.class);
                intent.putExtra("propertyId", currentProperty.getId());
                intent.putExtra("isEditMode", true);
                startActivity(intent);
            }
        });
    }

    private void showToggleConfirmationDialog() {
        String message = currentProperty.isAvailable()
                ? "¿Estás seguro de que deseas deshabilitar esta propiedad?"
                : "¿Estás seguro de que deseas habilitar esta propiedad?";

        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage(message)
                .setPositiveButton("Sí", (dialog, which) -> {
                    viewModel.toggleInmuebleAvailability(propertyId);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void loadPropertyDetails() {
        viewModel.loadInmuebleById(propertyId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar detalles por si se editó la propiedad
        if (propertyId != -1) {
            loadPropertyDetails();
        }
    }
}