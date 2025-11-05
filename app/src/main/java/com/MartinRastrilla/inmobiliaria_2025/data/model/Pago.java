package com.MartinRastrilla.inmobiliaria_2025.data.model;

import com.google.gson.annotations.SerializedName;

public class Pago {
    @SerializedName("id")
    private int id;

    @SerializedName("amount")
    private double amount;

    @SerializedName("paymentDate")
    private String paymentDate;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("contrato")
    private Contrato contrato;

    @SerializedName("inquilino")
    private Inquilino inquilino;

    public Pago() {}

    // Getters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public Contrato getContrato() { return contrato; }
    public Inquilino getInquilino() { return inquilino; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setContrato(Contrato contrato) { this.contrato = contrato; }
    public void setInquilino(Inquilino inquilino) { this.inquilino = inquilino; }

    public String getPaymentMethodText() {
        if (paymentMethod == null) return "Desconocido";

        switch (paymentMethod) {
            case "Cash":
                return "Efectivo";
            case "BankTransfer":
                return "Transferencia Bancaria";
            case "CreditCard":
                return "Tarjeta de Crédito";
            case "DebitCard":
                return "Tarjeta de Débito";
            default:
                return paymentMethod;
        }
    }
}
