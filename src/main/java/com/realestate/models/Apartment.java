package com.realestate.models;

public class Apartment extends Property {
    private int unitNumber;
    private int bedrooms;
    private int bathrooms;
    private boolean hasParking;

    public Apartment(int id, String address, double price, String description,
                     int unitNumber, int bedrooms, int bathrooms, boolean hasParking) {
        super(id, address, price, description);
        this.unitNumber = unitNumber;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.hasParking = hasParking;
    }

    // Getters and Setters
    public int getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(int unitNumber) {
        this.unitNumber = unitNumber;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public boolean isHasParking() {
        return hasParking;
    }

    public void setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
    }

    @Override
    public void displayDetails() {
        System.out.println("Apartment Details: Unit " + unitNumber + " at " + address +
                " - " + bedrooms + " beds, " + bathrooms + " baths");
    }
}