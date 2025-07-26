package com.cab_app.cab.dto;

public class TripRequest {

    private double pickupLat;
    private double pickupLng;
    private double dropLat;
    private double dropLng;
    private String dropCity;

    // ✅ Getters
    public double getPickupLat() {
        return pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public double getDropLat() {
        return dropLat;
    }

    public double getDropLng() {
        return dropLng;
    }

    public String getDropCity() {
        return dropCity;
    }

    // ✅ Setters
    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public void setDropLat(double dropLat) {
        this.dropLat = dropLat;
    }

    public void setDropLng(double dropLng) {
        this.dropLng = dropLng;
    }

    public void setDropCity(String dropCity) {
        this.dropCity = dropCity;
    }
}
