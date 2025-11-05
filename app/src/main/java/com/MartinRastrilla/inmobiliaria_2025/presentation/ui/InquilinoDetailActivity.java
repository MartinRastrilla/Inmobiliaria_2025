package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.data.model.Inquilino;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.InquilinoViewModel;

public class InquilinoDetailActivity extends AppCompatActivity {
    private TextView tvInquilinoName, tvInquilinoLastName, tvInquilinoDocument, tvInquilinoPhone, tvInquilinoEmail;
    private ProgressBar progressBar;
    private InquilinoViewModel viewModel;
    private int inquilinoId;
    private Inquilino currentInquilino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inquilino_detail);

        inquilinoId = getIntent().getIntExtra("inquilinoId", -1);
        if (inquilinoId == -1) {
            Toast.makeText(this, "Error: ID de inquilino no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initViewModel();
        setupObservers();
        loadInquilinoDetails();
    }

    private void initViews() {
        tvInquilinoName = findViewById(R.id.tvInquilinoName);
        tvInquilinoLastName = findViewById(R.id.tvInquilinoLastName);
        tvInquilinoDocument = findViewById(R.id.tvInquilinoDocument);
        tvInquilinoPhone = findViewById(R.id.tvInquilinoPhone);
        tvInquilinoEmail = findViewById(R.id.tvInquilinoEmail);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(InquilinoViewModel.class);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.getInquilinoDetail().observe(this, inquilino -> {
            if (inquilino != null) {
                currentInquilino = inquilino;
                displayInquilinoDetails(inquilino);
            }
        });
    }

    private void displayInquilinoDetails(Inquilino inquilino) {
        tvInquilinoName.setText(inquilino.getName() != null ? inquilino.getName() : "");
        tvInquilinoLastName.setText(inquilino.getLastName() != null ? inquilino.getLastName() : "");

        if (inquilino.getDocumentNumber() != null && !inquilino.getDocumentNumber().isEmpty()) {
            tvInquilinoDocument.setText(inquilino.getDocumentNumber());
            tvInquilinoDocument.setVisibility(View.VISIBLE);
        } else {
            tvInquilinoDocument.setVisibility(View.GONE);
        }

        if (inquilino.getPhone() != null && !inquilino.getPhone().isEmpty()) {
            tvInquilinoPhone.setText(inquilino.getPhone());
            tvInquilinoPhone.setVisibility(View.VISIBLE);
        } else {
            tvInquilinoPhone.setVisibility(View.GONE);
        }

        if (inquilino.getEmail() != null && !inquilino.getEmail().isEmpty()) {
            tvInquilinoEmail.setText(inquilino.getEmail());
            tvInquilinoEmail.setVisibility(View.VISIBLE);
        } else {
            tvInquilinoEmail.setVisibility(View.GONE);
        }
    }

    private void loadInquilinoDetails() {
        viewModel.loadInquilinoById(inquilinoId);
    }
}