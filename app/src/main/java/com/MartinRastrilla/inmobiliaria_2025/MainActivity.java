package com.MartinRastrilla.inmobiliaria_2025;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.BaseActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.ContractListActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.EditProfileActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.LoginActivity;
import com.MartinRastrilla.inmobiliaria_2025.presentation.ui.PropertyListActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends BaseActivity {
    private TextView tvWelcome, tvUserInfo;
    private Button btnLogout;
    private LinearLayout propertiesCard, paymentsCard, profileCard;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setupDrawer();
    }

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
    }

    private void setupUserInfo() {
        String email = preferencesHelper.getEmail();
        String name = preferencesHelper.getName();
        String lastname = preferencesHelper.getLastName();
        String fullName = name + " " + lastname;

        tvWelcome.setText("¡Bienvenido/a " + fullName + "!");
        tvUserInfo.setText("Email: " + email);
        updateNavHeader();
    }

    private void setupClickListeners() {
        profileCard.setOnClickListener(v -> {
            navigateToActivity(EditProfileActivity.class);
        });

        propertiesCard.setOnClickListener(v -> {
            navigateToActivity(PropertyListActivity.class);
        });

        paymentsCard.setOnClickListener(v -> {
            navigateToActivity(ContractListActivity.class);
        });

        btnLogout.setOnClickListener(v -> {
            performLogout();
        });
    }
}