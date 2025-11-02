package com.MartinRastrilla.inmobiliaria_2025.data.model;

import com.google.gson.annotations.SerializedName;

public class ToggleResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private boolean data;

    public ToggleResponse() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}