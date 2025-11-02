package com.MartinRastrilla.inmobiliaria_2025;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.EditProfileActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.LoginActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.PropertyListActivity;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;

public class MainActivity extends AppCompatActivity {
    private TextView tvWelcome, tvUserInfo;
    private Button btnLogout;
    private LinearLayout propertiesCard, paymentsCard, profileCard;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        initViews();
        setupUserInfo();
        setupClickListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        btnLogout = findViewById(R.id.btnLogout);
        propertiesCard = findViewById(R.id.propertiesCard);
        paymentsCard = findViewById(R.id.paymentsCard);
        profileCard = findViewById(R.id.profileCard);
        preferencesHelper = new PreferencesHelper(this);
    }

    private void setupUserInfo() {
        String email = preferencesHelper.getEmail();
        String name = preferencesHelper.getName();
        String lastname = preferencesHelper.getLastName();

        tvWelcome.setText("¡Bienvenido/a " + name + " " + lastname + "!");
        tvUserInfo.setText("Email: " + email);
    }

    private void setupClickListeners() {
        profileCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        propertiesCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PropertyListActivity.class);
            startActivity(intent);
        });

        paymentsCard.setOnClickListener(v -> {
            // TODO: Implementar navegación a Pagos y Contratos
        });

        btnLogout.setOnClickListener(v -> {
            preferencesHelper.clearUserData();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}