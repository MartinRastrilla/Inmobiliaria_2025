package com.MartinRastrilla.inmobiliaria_2025.data.model;

public class InmuebleRequest {
    private String title;
    private String address;
    private String latitude;
    private String longitude;
    private int rooms;
    private double price;
    private Integer maxGuests;

    public  InmuebleRequest(){ }

    public InmuebleRequest(String title, String address, String latitude, String longitude,
                           int rooms, double price, Integer maxGuests) {
        this.title = title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rooms = rooms;
        this.price = price;
        this.maxGuests = maxGuests;
    }

    // Getters
    public String getTitle() { return title; }
    public String getAddress() { return address; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public int getRooms() { return rooms; }
    public double getPrice() { return price; }
    public Integer getMaxGuests() { return maxGuests; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public void setRooms(int rooms) { this.rooms = rooms; }
    public void setPrice(double price) { this.price = price; }
    public void setMaxGuests(Integer maxGuests) { this.maxGuests = maxGuests; }
}