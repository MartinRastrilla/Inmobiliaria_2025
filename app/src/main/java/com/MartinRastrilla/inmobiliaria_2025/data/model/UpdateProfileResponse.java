package com.MartinRastrilla.inmobiliaria_2025.data.model;

public class UpdateProfileResponse {
    private String message;
    private UserResponse data;

    public UpdateProfileResponse() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponse getData() {
        return data;
    }

    public void setData(UserResponse data) {
        this.data = data;
    }
}
