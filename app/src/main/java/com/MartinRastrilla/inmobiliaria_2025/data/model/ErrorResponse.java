package com.MartinRastrilla.inmobiliaria_2025.data.model;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
