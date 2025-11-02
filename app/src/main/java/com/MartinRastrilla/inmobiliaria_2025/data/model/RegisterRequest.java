package com.MartinRastrilla.inmobiliaria_2025.data.model;

import java.util.Arrays;
import java.util.List;

public class RegisterRequest {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private List<String> roles;
    private String phone;
    private String documentNumber;

    public RegisterRequest(String name, String lastName, String email, String password, String phone, String documentNumber) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.documentNumber = documentNumber;
        // Hardcodear rol Propietario
        this.roles = Arrays.asList("Propietario");
    }

    // Getters
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<String> getRoles() { return roles; }
    public String getPhone() { return phone; }
    public String getDocumentNumber() { return documentNumber; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
}
