package com.MartinRastrilla.inmobiliaria_2025.data.model;

import java.util.List;

public class UserResponse {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String profilePicRoute;
    private List<String> roles;
    private String phone;
    private String documentNumber;
    private String profilePic;

    public UserResponse() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicRoute() {
        return profilePicRoute;
    }

    public void setProfilePicRoute(String profilePicRoute) {
        this.profilePicRoute = profilePicRoute;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFirstRole() {
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0);
        }
        return null;
    }
}
