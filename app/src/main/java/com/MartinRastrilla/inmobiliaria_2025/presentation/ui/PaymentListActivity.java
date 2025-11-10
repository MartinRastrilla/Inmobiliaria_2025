package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Pago;
import com.MartinRastrilla.inmobiliaria_2025.presentation.adapter.PagoAdapter;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.PagoViewModel;
import com.MartinRastrilla.inmobiliaria_2025.utils.ToastHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PaymentListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout tvEmptyState;
    private MaterialToolbar toolbar;
    private PagoAdapter adapter;
    private PagoViewModel viewModel;
    private List<Pago> pagoList = new ArrayList<>();
    private int contratoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_list);

        contratoId = getIntent().getIntExtra("contratoId", -1);
        if (contratoId == -1) {
            ToastHelper.showError(this, "Error: ID de contrato no válido");
            finish();
            return;
        }

        initViews();
        setupToolbar();
        initViewModel();
        setupRecyclerView();
        setupObservers();
        loadPagos();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewPayments);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pagos");
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(PagoViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new PagoAdapter(pagoList);
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
                finish();
            }
        });

        viewModel.getPagosList().observe(this, pagos -> {
            if (pagos != null && !pagos.isEmpty()) {
                pagoList.clear();
                pagoList.addAll(pagos);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadPagos() {
        viewModel.loadPagosByContratoId(contratoId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPagos();
    }
}