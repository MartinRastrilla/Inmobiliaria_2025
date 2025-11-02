package com.MartinRastrilla.inmobiliaria_2025.data.model;

public class ApiResult<T> {
    private T data;
    private String error;
    private boolean isSuccess;

    public ApiResult(T data) {
        this.data = data;
        this.isSuccess = true;
        this.error = null;
    }

    public ApiResult(String error) {
        this.data = null;
        this.isSuccess = false;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
