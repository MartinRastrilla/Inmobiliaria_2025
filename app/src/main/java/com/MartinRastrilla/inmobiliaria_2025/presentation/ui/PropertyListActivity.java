package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.presentation.adapter.PropertyAdapter;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.InmuebleViewModel;
import com.MartinRastrilla.inmobiliaria_2025.utils.ToastHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PropertyListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout tvEmptyState;
    private FloatingActionButton fabAddProperty;
    private MaterialToolbar toolbar;
    private PropertyAdapter adapter;
    private InmuebleViewModel viewModel;
    private List<Inmueble> propertyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_property_list);

        initViews();
        setupToolbar();
        initViewModel();
        setupRecyclerView();
        setupObservers();
        loadProperties();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProperties);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddProperty = findViewById(R.id.fabAddProperty);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mis Propiedades");
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(InmuebleViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new PropertyAdapter(propertyList, inmueble -> {
            Intent intent = new Intent(PropertyListActivity.this, PropertyDetailActivity.class);
            intent.putExtra("propertyId", inmueble.getId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                ToastHelper.showError(this, error);
            }
        });

        viewModel.getInmueblesList().observe(this, properties -> {
            if (properties != null && !properties.isEmpty()) {
                propertyList.clear();
                propertyList.addAll(properties);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadProperties() {
        viewModel.loadInmuebles();
    }

    private void setupClickListeners() {
        fabAddProperty.setOnClickListener(v -> {
            Intent intent = new Intent(PropertyListActivity.this, CreatePropertyActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProperties();
    }
}