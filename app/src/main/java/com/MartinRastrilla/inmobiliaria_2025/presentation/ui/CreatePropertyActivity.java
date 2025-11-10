package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.data.model.InmuebleRequest;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.InmuebleViewModel;
import com.MartinRastrilla.inmobiliaria_2025.utils.ToastHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CreatePropertyActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String TAG = "CreatePropertyActivity";
    private static final long SEARCH_DELAY_MS = 800L;
    private EditText etTitle, etAddress, etLatitude, etLongitude, etRooms, etPrice, etMaxGuests;
    private TextInputEditText etSearchAddress;
    private Button btnSave, btnSelectImages;
    private ProgressBar progressBar;
    private TextView tvTitle, tvMapTitle;
    private RecyclerView recyclerViewImages;
    private CardView cardMapContainer;
    private InmuebleViewModel viewModel;
    private boolean isEditMode = false;
    private int propertyId = -1;
    private Inmueble currentProperty;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private SelectedImagesAdapter imagesAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private PlacesClient placesClient;
    private AutocompleteSessionToken autocompleteSessionToken;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private LatLng selectedLatLng;
    private boolean suppressSearchTextChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_property);

        isEditMode = getIntent().getBooleanExtra("isEditMode", false);
        propertyId = getIntent().getIntExtra("propertyId", -1);

        initViews();
        initViewModel();
        setupImagePicker();
        setupObservers();
        setupClickListeners();
        setupImagesRecyclerView();
        initPlaces();
        setupMap();
        setupSearchField();

        if (isEditMode && propertyId != -1) {
            loadPropertyForEdit();
            setActivityTitle("Editar Propiedad");
        } else {
            setActivityTitle("Crear Propiedad");
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
        etSearchAddress = findViewById(R.id.etSearchAddress);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewImages = findViewById(R.id.recyclerViewSelectedImages);
        tvMapTitle = findViewById(R.id.tvMapTitle);
        cardMapContainer = findViewById(R.id.cardMapContainer);
        searchHandler = new Handler(Looper.getMainLooper());

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

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                selectedImageUris.add(imageUri);
                            }
                        } else if (result.getData().getData() != null) {
                            selectedImageUris.add(result.getData().getData());
                        }
                        imagesAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    private void setupImagesRecyclerView() {
        imagesAdapter = new SelectedImagesAdapter(selectedImageUris, this::removeImage);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewImages.setAdapter(imagesAdapter);
    }

    private void removeImage(int position) {
        selectedImageUris.remove(position);
        imagesAdapter.notifyDataSetChanged();
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                ToastHelper.showError(this, error);
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
                ToastHelper.showSuccess(this, message);
                finish();
            }
        });
    }

    private void fillFormWithPropertyData(Inmueble inmueble) {
        etTitle.setText(inmueble.getTitle());
        etAddress.setText(inmueble.getAddress());
        if (etSearchAddress != null) {
            suppressSearchTextChanges = true;
            etSearchAddress.setText(inmueble.getAddress());
            etSearchAddress.setSelection(etSearchAddress.getText() != null ? etSearchAddress.getText().length() : 0);
            suppressSearchTextChanges = false;
        }
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
        updateMapFromCoordinates();
    }

    private void setupClickListeners() {
        btnSelectImages.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickerLauncher.launch(Intent.createChooser(intent, "Selecciona imágenes"));
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveProperty();
            }
        });
    }

    private void initPlaces() {
        if (placesClient != null) {
            return;
        }

        String apiKey = getMapsApiKey();
        if (TextUtils.isEmpty(apiKey)) {
            Log.w(TAG, "No se encontró la API key de Google Maps en el Manifest.");
            ToastHelper.showWarning(this, "No se encontró la API key de Google Maps");
            return;
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this);
        autocompleteSessionToken = AutocompleteSessionToken.newInstance();
    }

    private String getMapsApiKey() {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            if (bundle != null) {
                return bundle.getString("com.google.android.geo.API_KEY");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error obteniendo la API key de Google Maps", e);
        }
        return null;
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupSearchField() {
        if (etSearchAddress == null) {
            return;
        }

        etSearchAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (suppressSearchTextChanges || searchHandler == null) {
                    return;
                }

                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                final String query = s.toString().trim();

                if (query.length() < 3) {
                    return;
                }

                searchRunnable = () -> performSearch(query);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void performSearch(String query) {
        if (placesClient == null || TextUtils.isEmpty(query)) {
            return;
        }

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(autocompleteSessionToken)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    if (!response.getAutocompletePredictions().isEmpty()) {
                        AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                        fetchPlaceDetails(prediction.getPlaceId());
                    } else {
                        ToastHelper.showInfo(this, "No se encontraron resultados para la dirección ingresada");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error buscando dirección", e);
                    ToastHelper.showError(this, "No se pudo obtener la ubicación");
                });
    }

    private void fetchPlaceDetails(String placeId) {
        if (placesClient == null) {
            return;
        }

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId,
                        Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
                .setSessionToken(autocompleteSessionToken)
                .build();

        placesClient.fetchPlace(request)
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    LatLng latLng = place.getLatLng();
                    if (latLng == null) {
                        ToastHelper.showInfo(this, "No se encontraron coordenadas para la dirección seleccionada");
                        return;
                    }

                    selectedLatLng = latLng;
                    String address = place.getAddress();
                    if (address != null) {
                        etAddress.setText(address);
                        if (etSearchAddress != null) {
                            suppressSearchTextChanges = true;
                            etSearchAddress.setText(address);
                            etSearchAddress.setSelection(address.length());
                            suppressSearchTextChanges = false;
                        }
                    }

                    updateMapPosition(latLng, address, place.getName());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obteniendo detalles del lugar", e);
                    ToastHelper.showError(this, "No se pudo obtener la ubicación seleccionada");
                });
    }

    private void updateMapPosition(LatLng latLng, String address, String title) {
        if (latLng == null) {
            hideMap();
            return;
        }

        selectedLatLng = latLng;

        if (cardMapContainer != null && cardMapContainer.getVisibility() != View.VISIBLE) {
            cardMapContainer.setVisibility(View.VISIBLE);
        }

        if (tvMapTitle != null && tvMapTitle.getVisibility() != View.VISIBLE) {
            tvMapTitle.setVisibility(View.VISIBLE);
        }

        if (googleMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(!TextUtils.isEmpty(title) ? title : "Ubicación seleccionada")
                    .snippet(address));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        }

        etLatitude.setText(String.format(Locale.US, "%.6f", latLng.latitude));
        etLongitude.setText(String.format(Locale.US, "%.6f", latLng.longitude));
    }

    private void updateMapFromCoordinates() {
        if (etLatitude == null || etLongitude == null) {
            return;
        }

        String latStr = etLatitude.getText().toString().trim();
        String lngStr = etLongitude.getText().toString().trim();

        if (TextUtils.isEmpty(latStr) || TextUtils.isEmpty(lngStr)) {
            hideMap();
            return;
        }

        try {
            double latitude = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(lngStr);
            updateMapPosition(new LatLng(latitude, longitude), etAddress.getText().toString(), null);
        } catch (NumberFormatException e) {
            hideMap();
        }
    }

    private void hideMap() {
        if (googleMap != null) {
            googleMap.clear();
        }
        selectedLatLng = null;
        if (cardMapContainer != null) {
            cardMapContainer.setVisibility(View.GONE);
        }
        if (tvMapTitle != null) {
            tvMapTitle.setVisibility(View.GONE);
        }
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
            ToastHelper.showInfo(this, "La edición se implementará próximamente");
        } else {
            viewModel.createInmueble(request, selectedImageUris);
        }
    }

    private void loadPropertyForEdit() {
        viewModel.loadInmuebleById(propertyId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
        }

        if (selectedLatLng != null) {
            updateMapPosition(selectedLatLng, etAddress.getText().toString(), null);
        } else {
            updateMapFromCoordinates();
        }
    }

    @Override
    protected void onDestroy() {
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroy();
    }

    private static class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ImageViewHolder> {
        private List<Uri> imageUris;
        private OnImageRemoveListener removeListener;

        interface OnImageRemoveListener {
            void onRemove(int position);
        }

        SelectedImagesAdapter(List<Uri> imageUris, OnImageRemoveListener removeListener) {
            this.imageUris = imageUris;
            this.removeListener = removeListener;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selected_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUris.get(position))
                    .centerCrop()
                    .into(holder.imageView);

            holder.btnRemove.setOnClickListener(v -> {
                if (removeListener != null) {
                    removeListener.onRemove(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageUris.size();
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            Button btnRemove;

            ImageViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivSelectedImage);
                btnRemove = itemView.findViewById(R.id.btnRemoveImage);
            }
        }
    }
}