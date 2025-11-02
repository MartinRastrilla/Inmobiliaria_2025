package com.MartinRastrilla.inmobiliaria_2025.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Inmueble {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("rooms")
    private int rooms;

    @SerializedName("price")
    private double price;

    @SerializedName("maxGuests")
    private Integer maxGuests;

    @SerializedName("available")
    private boolean available;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("archivosRoutes")
    private List<String> archivosRoutes;

    public Inmueble() {}

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAddress() { return address; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public int getRooms() { return rooms; }
    public double getPrice() { return price; }
    public Integer getMaxGuests() { return maxGuests; }
    public boolean isAvailable() { return available; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public List<String> getArchivosRoutes() { return archivosRoutes; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public void setRooms(int rooms) { this.rooms = rooms; }
    public void setPrice(double price) { this.price = price; }
    public void setMaxGuests(Integer maxGuests) { this.maxGuests = maxGuests; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setArchivosRoutes(List<String> archivosRoutes) { this.archivosRoutes = archivosRoutes; }
}