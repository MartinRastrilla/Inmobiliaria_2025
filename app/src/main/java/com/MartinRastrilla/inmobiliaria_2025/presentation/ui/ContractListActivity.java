package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Contrato;
import com.MartinRastrilla.inmobiliaria_2025.presentation.adapter.ContratoAdapter;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.ContratoViewModel;
import com.MartinRastrilla.inmobiliaria_2025.utils.ToastHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ContractListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout tvEmptyState;
    private MaterialToolbar toolbar;
    private ContratoAdapter adapter;
    private ContratoViewModel viewModel;
    private List<Contrato> contratoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contract_list);

        initViews();
        setupToolbar();
        initViewModel();
        setupRecyclerView();
        setupObservers();
        loadContratos();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewContracts);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contratos y Pagos");
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ContratoViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new ContratoAdapter(contratoList, contrato -> {
            Intent intent = new Intent(ContractListActivity.this, ContractDetailActivity.class);
            intent.putExtra("contratoId", contrato.getId());
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

        viewModel.getContratosList().observe(this, contratos -> {
            if (contratos != null && !contratos.isEmpty()) {
                contratoList.clear();
                contratoList.addAll(contratos);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadContratos() {
        viewModel.loadContratos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContratos();
    }
}