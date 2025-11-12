package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

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
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inmueble;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inquilino;
import com.MartinRastrilla.inmobiliaria_2025.presentation.adapter.InquilinoAdapter;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.InquilinoDetailActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.PaymentListActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.ContratoViewModel;
import com.MartinRastrilla.inmobiliaria_2025.utils.FormatterUtils;
import com.MartinRastrilla.inmobiliaria_2025.utils.ToastHelper;

import java.util.ArrayList;

public class ContractDetailActivity extends BaseActivity {
    private TextView tvContractId, tvStartEndDate, tvTotalPrice, tvMonthlyPrice, tvStatus, tvCreatedAt;
    private TextView tvInmuebleName, tvInmuebleAddress;
    private TextView tvInquilinosTitle, tvPaymentsTitle;
    private CardView llInmuebleContainer, cvPayments;
    private RecyclerView recyclerViewInquilinos;
    private ProgressBar progressBar;
    private ContratoViewModel viewModel;
    private InquilinoAdapter inquilinoAdapter;
    private int contratoId;
    private Contrato currentContrato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contract_detail);

        contratoId = getIntent().getIntExtra("contratoId", -1);
        if (contratoId == -1) {
            ToastHelper.showError(this, "Error: ID de contrato no válido");
            finish();
            return;
        }

        initViews();
        initViewModel();
        setupRecyclerView();
        setupObservers();
        setupClickListeners();
        loadContratoDetails();
        
        setActivityTitle("Detalle de Contrato");
    }

    private void initViews() {
        tvContractId = findViewById(R.id.tvContractId);
        tvStartEndDate = findViewById(R.id.tvStartEndDate);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvMonthlyPrice = findViewById(R.id.tvMonthlyPrice);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvInmuebleName = findViewById(R.id.tvInmuebleName);
        tvInmuebleAddress = findViewById(R.id.tvInmuebleAddress);
        tvInquilinosTitle = findViewById(R.id.tvInquilinosTitle);
        tvPaymentsTitle = findViewById(R.id.tvPaymentsTitle);
        cvPayments = findViewById(R.id.cvPayments);
        llInmuebleContainer = findViewById(R.id.llInmuebleContainer);
        recyclerViewInquilinos = findViewById(R.id.recyclerViewInquilinos);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ContratoViewModel.class);
    }

    private void setupRecyclerView() {
        inquilinoAdapter = new InquilinoAdapter(new ArrayList<>(), inquilino -> {
            Intent intent = new Intent(ContractDetailActivity.this, InquilinoDetailActivity.class);
            intent.putExtra("inquilinoId", inquilino.getId());
            startActivity(intent);
        });
        recyclerViewInquilinos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInquilinos.setAdapter(inquilinoAdapter);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                ToastHelper.showError(this, error);
                finish();
            }
        });

        viewModel.getContratoDetail().observe(this, contrato -> {
            if (contrato != null) {
                currentContrato = contrato;
                displayContratoDetails(contrato);
            }
        });
    }

    private void setupClickListeners() {
        llInmuebleContainer.setOnClickListener(v -> {
            if (currentContrato != null && currentContrato.getInmueble() != null) {
                Intent intent = new Intent(ContractDetailActivity.this, PropertyDetailActivity.class);
                intent.putExtra("propertyId", currentContrato.getInmueble().getId());
                startActivity(intent);
            }
        });

        cvPayments.setOnClickListener(v -> {
            if (currentContrato != null) {
                Intent intent = new Intent(ContractDetailActivity.this, PaymentListActivity.class);
                intent.putExtra("contratoId", currentContrato.getId());
                startActivity(intent);
            }
        });
    }

    private void displayContratoDetails(Contrato contrato) {
        tvContractId.setText(FormatterUtils.formatContractId(contrato.getId()));

        String startDate = FormatterUtils.formatDate(contrato.getStartDate());
        String endDate = FormatterUtils.formatDate(contrato.getEndDate());
        tvStartEndDate.setText(startDate + " - " + endDate);

        tvTotalPrice.setText(FormatterUtils.formatPrice(contrato.getTotalPrice()));

        if (contrato.getMonthlyPrice() != null && contrato.getMonthlyPrice() > 0) {
            tvMonthlyPrice.setText(FormatterUtils.formatMonthlyPriceText(contrato.getMonthlyPrice()));
            tvMonthlyPrice.setVisibility(View.VISIBLE);
        } else {
            tvMonthlyPrice.setVisibility(View.GONE);
        }

        String statusText = contrato.getStatusText();
        tvStatus.setText(statusText);
        tvStatus.setTextColor(getResources().getColor(R.color.white));

        int statusCode = contrato.getStatusCode();
        switch (statusCode) {
            case 0: // Pendiente - Amarillo
                tvStatus.setBackgroundResource(R.drawable.status_pending_background);
                break;
            case 1: // Activo - Verde
                tvStatus.setBackgroundResource(R.drawable.status_finalized_background);
                break;
            case 2: // Expirado - Naranja
                tvStatus.setBackgroundResource(R.drawable.status_expired_background);
                break;
            case 3: // Cancelado - Rojo
                tvStatus.setBackgroundResource(R.drawable.status_cancelled_background);
                break;
            default:
                tvStatus.setBackgroundResource(R.drawable.status_pending_background);
                break;
        }

        if (contrato.getCreatedAt() != null) {
            tvCreatedAt.setText(FormatterUtils.formatCreatedAtText(contrato.getCreatedAt()));
        }

        if (contrato.getInmueble() != null) {
            Inmueble inmueble = contrato.getInmueble();
            tvInmuebleName.setText(inmueble.getTitle());
            tvInmuebleAddress.setText(inmueble.getAddress());
            llInmuebleContainer.setVisibility(View.VISIBLE);
        } else {
            llInmuebleContainer.setVisibility(View.GONE);
        }

        if (contrato.getInquilinos() != null && !contrato.getInquilinos().isEmpty()) {
            inquilinoAdapter = new InquilinoAdapter(contrato.getInquilinos(), inquilino -> {
                Intent intent = new Intent(ContractDetailActivity.this, InquilinoDetailActivity.class);
                intent.putExtra("inquilinoId", inquilino.getId());
                startActivity(intent);
            });
            recyclerViewInquilinos.setAdapter(inquilinoAdapter);
            recyclerViewInquilinos.setVisibility(View.VISIBLE);
            tvInquilinosTitle.setVisibility(View.VISIBLE);
        } else {
            recyclerViewInquilinos.setVisibility(View.GONE);
            tvInquilinosTitle.setVisibility(View.GONE);
        }

        cvPayments.setVisibility(View.VISIBLE);
    }

    private void loadContratoDetails() {
        viewModel.loadContratoById(contratoId);
    }
}