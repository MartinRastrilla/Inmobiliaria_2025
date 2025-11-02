package com.MartinRastrilla.inmobiliaria_2025.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_DOCUMENT_NUMBER = "document_number";
    private static final String PREF_NAME = "inmobiliaria_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_PROFILE_PIC_ROUTE = "profile_pic_route";

    public void setProfilePicRoute(String route) {
        editor.putString(KEY_PROFILE_PIC_ROUTE, route);
        editor.apply();
    }

    public String getProfilePicRoute() {
        return sharedPreferences.getString(KEY_PROFILE_PIC_ROUTE, null);
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void saveRefreshToken(String refreshToken) {
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public void saveUserData(String userId, String email, String role) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void saveFullUserData(String userId, String name, String lastName, String email, String phone, String documentNumber, String role, String profilePicRoute) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_DOCUMENT_NUMBER, documentNumber);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_PROFILE_PIC_ROUTE, profilePicRoute);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getName() {
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_NAME, null);
    }

    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    public String getDocumentNumber() {
        return sharedPreferences.getString(KEY_DOCUMENT_NUMBER, null);
    }

    public void clearUserData() {
        editor.clear();
        editor.apply();
    }
}
