package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.presentation.viewmodel.UserViewModel;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;
import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etLastName, etEmail, etPhone, etDocumentNumber;
    private ImageView ivProfilePic;
    private Button btnSave, btnChangePassword, btnSelectPhoto;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;
    private PreferencesHelper preferencesHelper;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        initViewModel();
        loadUserData();
        setupObservers();
        setupClickListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDocumentNumber = findViewById(R.id.etDocumentNumber);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        progressBar = findViewById(R.id.progressBar);
        preferencesHelper = new PreferencesHelper(this);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void loadUserData() {
        etName.setText(preferencesHelper.getName() != null ? preferencesHelper.getName() : "");
        etLastName.setText(preferencesHelper.getLastName() != null ? preferencesHelper.getLastName() : "");
        etEmail.setText(preferencesHelper.getEmail() != null ? preferencesHelper.getEmail() : "");
        etPhone.setText(preferencesHelper.getPhone() != null ? preferencesHelper.getPhone() : "");
        etDocumentNumber.setText(preferencesHelper.getDocumentNumber() != null ? preferencesHelper.getDocumentNumber() : "");

        String profilePicRoute = preferencesHelper.getProfilePicRoute();
        if (profilePicRoute != null && !profilePicRoute.isEmpty()) {
            String baseUrl = "http://192.168.100.49:5275";
            String imageUrl = baseUrl + profilePicRoute;
            Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(ivProfilePic);
        }
    }

    private void setupObservers() {
        userViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        userViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        userViewModel.getUpdateResult().observe(this, loginResponse -> {
            if (loginResponse != null) {
                Toast.makeText(this, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show();

                String name = etName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String documentNumber = etDocumentNumber.getText().toString().trim();
                String profilePicRoute = preferencesHelper.getProfilePicRoute();

                preferencesHelper.saveFullUserData(
                        preferencesHelper.getUserId() != null ? preferencesHelper.getUserId() : "",
                        name,
                        lastName,
                        email,
                        phone,
                        documentNumber,
                        preferencesHelper.getRole(),
                        profilePicRoute
                );

                if (profilePicRoute != null && !profilePicRoute.isEmpty()) {
                    String baseUrl = "http://192.168.100.49:5275";
                    String imageUrl = baseUrl + profilePicRoute;
                    Glide.with(this)
                            .load(imageUrl)
                            .circleCrop()
                            .into(ivProfilePic);
                }
                finish();
            }
        });
    }

    private void setupClickListeners() {
        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                String name = etName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String documentNumber = etDocumentNumber.getText().toString().trim();
                String password = "";
                List<String> roles = Arrays.asList(preferencesHelper.getRole());

                userViewModel.updateProfile(
                        name, lastName, email, password,
                        phone, documentNumber, roles, selectedImageUri
                );
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });
    }

    private boolean validateInput() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Nombre es requerido");
            etName.requestFocus();
            return false;
        }

        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.setError("Apellido es requerido");
            etLastName.requestFocus();
            return false;
        }

        String email = etEmail.getText().toString().trim();
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


        return true;
    }

    private void showChangePasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Cambiar Contraseña");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        builder.setView(dialogView);
        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validatePasswordChange(newPassword, confirmPassword)) {
                userViewModel.updatePassword(currentPassword, newPassword, confirmPassword);
            }
        });
        builder.setNegativeButton("Cancelar", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.white));
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.white));
    }

    private boolean validatePasswordChange(String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(".*\\d.*")) {
            Toast.makeText(this, "La contraseña debe tener al menos 1 número", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            Toast.makeText(this, "La contraseña debe tener al menos 1 letra mayúscula", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.matches(".*[a-z].*")) {
            Toast.makeText(this, "La contraseña debe tener al menos 1 letra minúscula", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(ivProfilePic);
        }
    }
}