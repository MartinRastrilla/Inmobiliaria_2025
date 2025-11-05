package com.MartinRastrilla.inmobiliaria_2025.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Contrato {
    @SerializedName("id")
    private int id;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("monthlyPrice")
    private Double monthlyPrice;

    @SerializedName("status")
    private String status; // "Pending", "Active", "Expired", "Cancelled"

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("inmueble")
    private Inmueble inmueble;

    @SerializedName("inquilinos")
    private List<Inquilino> inquilinos;

    public Contrato() {}

    // Getters
    public int getId() { return id; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public Double getMonthlyPrice() { return monthlyPrice; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public Inmueble getInmueble() { return inmueble; }
    public List<Inquilino> getInquilinos() { return inquilinos; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setMonthlyPrice(Double monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setInmueble(Inmueble inmueble) { this.inmueble = inmueble; }
    public void setInquilinos(List<Inquilino> inquilinos) { this.inquilinos = inquilinos; }

    public String getStatusText() {
        if (status == null) return "Desconocido";
        
        switch (status.toLowerCase()) {
            case "pending":
                return "Pendiente";
            case "active":
                return "Activo";
            case "expired":
                return "Expirado";
            case "cancelled":
                return "Cancelado";
            default:
                return status;
        }
    }

    public int getStatusCode() {
        if (status == null) return 0;

        switch (status.toLowerCase()) {
            case "pending":
                return 0; // Pendiente - Amarillo
            case "active":
                return 1; // Activo - Verde
            case "expired":
                return 2; // Expirado - Naranja
            case "cancelled":
                return 3; // Cancelado - Rojo
            default:
                return 0; // Default a Pendiente
        }
    }
}
