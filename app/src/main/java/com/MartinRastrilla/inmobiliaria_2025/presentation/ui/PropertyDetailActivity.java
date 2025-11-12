package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.presentation.adapter.PropertyImagesAdapter;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.InmuebleViewModel;
import com.MartinRastrilla.inmobiliaria_2025.utils.FormatterUtils;
import com.MartinRastrilla.inmobiliaria_2025.utils.ToastHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PropertyDetailActivity extends BaseActivity implements OnMapReadyCallback {
    private TextView tvTitle, tvAddress, tvRooms, tvPrice, tvMaxGuests, tvStatus, tvCreatedAt, tvImagesTitle, tvMapTitle;
    private Button btnToggleAvailability, btnEditProperty;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewImages;
    private CardView mapCard;
    private PropertyImagesAdapter imagesAdapter;
    private InmuebleViewModel viewModel;
    private int propertyId;
    private Inmueble currentProperty;
    private String baseUrl = "http://192.168.100.49:5275";
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_property_detail);

        propertyId = getIntent().getIntExtra("propertyId", -1);
        if (propertyId == -1) {
            ToastHelper.showError(this, "Error: ID de propiedad no válido");
            finish();
            return;
        }

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
        loadPropertyDetails();
        
        setActivityTitle("Detalle de Propiedad");
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvAddress = findViewById(R.id.tvAddress);
        tvRooms = findViewById(R.id.tvRooms);
        tvPrice = findViewById(R.id.tvPrice);
        tvMaxGuests = findViewById(R.id.tvMaxGuests);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvImagesTitle = findViewById(R.id.tvImagesTitle);
        tvMapTitle = findViewById(R.id.tvMapTitle);
        btnToggleAvailability = findViewById(R.id.btnToggleAvailability);
        btnEditProperty = findViewById(R.id.btnEditProperty);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        mapCard = findViewById(R.id.mapCard);
        
        setupImagesRecyclerView();
        setupMap();
    }

    private void setupImagesRecyclerView() {
        imagesAdapter = new PropertyImagesAdapter(new ArrayList<>(), baseUrl);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewImages.setAdapter(imagesAdapter);
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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
                ToastHelper.showError(this, error);
            }
        });

        viewModel.getInmuebleDetail().observe(this, inmueble -> {
            if (inmueble != null) {
                currentProperty = inmueble;
                displayPropertyDetails(inmueble);
                showPropertyLocation(inmueble);
            }
        });

        viewModel.getInmuebleToggled().observe(this, inmueble -> {
            if (inmueble != null) {
                currentProperty = inmueble;
                displayPropertyDetails(inmueble);
                showPropertyLocation(inmueble);
                ToastHelper.showSuccess(this, InmuebleViewModel.getToggleMessage(inmueble));
            }
        });
    }

    private void displayPropertyDetails(Inmueble inmueble) {
        tvTitle.setText(inmueble.getTitle());
        tvAddress.setText(inmueble.getAddress());

        // Habitaciones
        tvRooms.setText(FormatterUtils.formatRoomsText(inmueble.getRooms()));

        // Precio
        tvPrice.setText(FormatterUtils.formatPrice(inmueble.getPrice()));

        // Máximo de huéspedes
        if (inmueble.getMaxGuests() != null) {
            tvMaxGuests.setText(FormatterUtils.formatMaxGuestsText(inmueble.getMaxGuests()));
            tvMaxGuests.setVisibility(View.VISIBLE);
        } else {
            tvMaxGuests.setVisibility(View.GONE);
        }

        // Estado
        tvStatus.setTextColor(getResources().getColor(R.color.white));
        if (inmueble.isAvailable()) {
            tvStatus.setText("Disponible");
            tvStatus.setBackgroundResource(R.drawable.status_available_background);
            btnToggleAvailability.setText("Deshabilitar Propiedad");
        } else {
            tvStatus.setText("No Disponible");
            tvStatus.setBackgroundResource(R.drawable.status_unavailable_background);
            btnToggleAvailability.setText("Habilitar Propiedad");
        }

        // Fecha de creación
        if (inmueble.getCreatedAt() != null) {
            tvCreatedAt.setText(FormatterUtils.formatCreatedAtText(inmueble.getCreatedAt()));
        }

        // Mostrar imágenes si existen
        if (inmueble.getArchivosRoutes() != null && !inmueble.getArchivosRoutes().isEmpty()) {
            imagesAdapter = new PropertyImagesAdapter(inmueble.getArchivosRoutes(), baseUrl);
            recyclerViewImages.setAdapter(imagesAdapter);
            recyclerViewImages.setVisibility(View.VISIBLE);
            tvImagesTitle.setVisibility(View.VISIBLE);
        } else {
            recyclerViewImages.setVisibility(View.GONE);
            tvImagesTitle.setVisibility(View.GONE);
        }
    }

    private void showPropertyLocation(Inmueble inmueble) {
        if (googleMap == null || inmueble == null) {
            return;
        }

        String latString = inmueble.getLatitude();
        String lngString = inmueble.getLongitude();

        if (latString == null || lngString == null || latString.isEmpty() || lngString.isEmpty()) {
            hideMap();
            return;
        }

        try {
            double latitude = Double.parseDouble(latString);
            double longitude = Double.parseDouble(lngString);

            LatLng propertyLatLng = new LatLng(latitude, longitude);

            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(propertyLatLng)
                    .title(inmueble.getTitle())
                    .snippet(inmueble.getAddress()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(propertyLatLng, 15f));

            showMap();
        } catch (NumberFormatException ex) {
            hideMap();
        }
    }

    private void showMap() {
        if (mapCard != null) {
            mapCard.setVisibility(View.VISIBLE);
        }
        if (tvMapTitle != null) {
            tvMapTitle.setVisibility(View.VISIBLE);
        }
    }

    private void hideMap() {
        if (mapCard != null) {
            mapCard.setVisibility(View.GONE);
        }
        if (tvMapTitle != null) {
            tvMapTitle.setVisibility(View.GONE);
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
        String message = InmuebleViewModel.getToggleConfirmationMessage(currentProperty);

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
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (currentProperty != null) {
            showPropertyLocation(currentProperty);
        } else {
            hideMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (propertyId != -1) {
            loadPropertyDetails();
        }
    }
}