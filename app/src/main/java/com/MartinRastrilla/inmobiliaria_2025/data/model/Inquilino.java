package com.MartinRastrilla.inmobiliaria_2025.data.model;

import com.google.gson.annotations.SerializedName;

public class Inquilino {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("documentNumber")
    private String documentNumber;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("isPaymentResponsible")
    private boolean isPaymentResponsible;

    public Inquilino() {}

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getDocumentNumber() { return documentNumber; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public boolean isPaymentResponsible() { return isPaymentResponsible; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setPaymentResponsible(boolean paymentResponsible) { isPaymentResponsible = paymentResponsible; }
}
