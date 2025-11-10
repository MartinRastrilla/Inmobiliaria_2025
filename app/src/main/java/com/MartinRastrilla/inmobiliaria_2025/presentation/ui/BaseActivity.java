package com.MartinRastrilla.inmobiliaria_2025.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.MartinRastrilla.inmobiliaria_2025.R;
import com.MartinRastrilla.inmobiliaria_2025.utils.PreferencesHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected MaterialToolbar toolbar;
    protected PreferencesHelper preferencesHelper;
    protected TextView navUserName, navUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesHelper = new PreferencesHelper(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        View drawerView = LayoutInflater.from(this).inflate(R.layout.activity_base_with_drawer, null);
        drawerLayout = drawerView.findViewById(R.id.drawerLayout);
        navigationView = drawerView.findViewById(R.id.navigationView);
        toolbar = drawerView.findViewById(R.id.toolbar);
        FrameLayout contentContainer = drawerView.findViewById(R.id.contentContainer);
        
        View contentView = LayoutInflater.from(this).inflate(layoutResID, contentContainer, false);
        contentContainer.addView(contentView);
        
        super.setContentView(drawerView);
        
        setupDrawer();
    }

    protected void setupDrawer() {
        MaterialToolbar contentToolbar = findToolbarInContent();
        if (contentToolbar != null) {
            toolbar = contentToolbar;
            View baseToolbar = findViewById(R.id.appBarLayout);
            if (baseToolbar != null) {
                baseToolbar.setVisibility(View.GONE);
            }
        }
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            navUserName = headerView.findViewById(R.id.navUserName);
            navUserEmail = headerView.findViewById(R.id.navUserEmail);
            updateNavHeader();
        }
        
        setupNavigationDrawer();
    }
    
    private MaterialToolbar findToolbarInContent() {
        FrameLayout contentContainer = findViewById(R.id.contentContainer);
        if (contentContainer != null) {
            for (int i = 0; i < contentContainer.getChildCount(); i++) {
                View child = contentContainer.getChildAt(i);
                MaterialToolbar toolbar = findToolbarRecursive(child);
                if (toolbar != null) {
                    return toolbar;
                }
            }
        }
        return null;
    }
    
    private MaterialToolbar findToolbarRecursive(View view) {
        if (view instanceof MaterialToolbar) {
            return (MaterialToolbar) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                MaterialToolbar toolbar = findToolbarRecursive(group.getChildAt(i));
                if (toolbar != null) {
                    return toolbar;
                }
            }
        }
        return null;
    }

    protected void updateNavHeader() {
        if (preferencesHelper != null && navUserName != null && navUserEmail != null) {
            String name = preferencesHelper.getName();
            String lastname = preferencesHelper.getLastName();
            String email = preferencesHelper.getEmail();
            String fullName = name + " " + lastname;
            
            navUserName.setText(fullName);
            navUserEmail.setText(email);
        }
    }

    protected void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_properties) {
                if (!(this instanceof PropertyListActivity)) {
                    navigateToActivity(PropertyListActivity.class);
                }
            } else if (itemId == R.id.nav_contracts) {
                if (!(this instanceof ContractListActivity)) {
                    navigateToActivity(ContractListActivity.class);
                }
            } else if (itemId == R.id.nav_profile) {
                if (!(this instanceof EditProfileActivity)) {
                    navigateToActivity(EditProfileActivity.class);
                }
            } else if (itemId == R.id.nav_logout) {
                performLogout();
            }
            
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    protected void navigateToActivity(Class<?> activityClass) {
        if (this.getClass().equals(activityClass)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        
        if (!isMainActivity(activityClass)) {
            finish();
        }
    }

    private boolean isMainActivity(Class<?> activityClass) {
        return activityClass.equals(PropertyListActivity.class) || 
               activityClass.equals(ContractListActivity.class) || 
               activityClass.equals(EditProfileActivity.class) ||
               activityClass.equals(com.MartinRastrilla.inmobiliaria_2025.MainActivity.class);
    }

    protected void performLogout() {
        preferencesHelper.clearUserData();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    protected void setActivityTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}

