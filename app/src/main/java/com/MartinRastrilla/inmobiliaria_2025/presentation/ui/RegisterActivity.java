package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
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

import com.MartinRastrilla.inmobiliaria_2025.MainActivity;
import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etLastName, etEmail, etPassword, etConfirmPassword, etPhone, etDocumentNumber;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        progressBar = findViewById(R.id.progressBar);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etDocumentNumber = findViewById(R.id.etDocumentNumber);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnRegister.setEnabled(!isLoading);
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getLoginResult().observe(this, loginResponse -> {
            if (loginResponse != null) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String documentNumber = etDocumentNumber.getText().toString().trim();

            if (validateInput(name, lastName, email, password, confirmPassword, phone, documentNumber)) {
                authViewModel.register(name, lastName, email, password, phone, documentNumber);
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInput(String name, String lastName, String email, String password, String confirmPassword, String phone, String documentNumber) {
        if (name.isEmpty()) {
            etName.setError("Nombre es requerido");
            etName.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Apellido es requerido");
            etLastName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email es requerido");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Contraseña es requerida");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirmar contraseña es requerido");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Teléfono es requerido");
            etPhone.requestFocus();
            return false;
        }

        if (documentNumber.isEmpty()) {
            etDocumentNumber.setError("DNI es requerido");
            etDocumentNumber.requestFocus();
            return false;
        }

        return true;
    }
}