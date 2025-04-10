package com.realestate.models;

public class House extends Property {
    private int bedrooms;
    private int bathrooms;
    private double squareFootage;

    public House(int id, String address, double price, String description,
                 int bedrooms, int bathrooms, double squareFootage) {
        super(id, address, price, description);
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.squareFootage = squareFootage;
    }

    // Getters and Setters
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

    public double getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(double squareFootage) {
        this.squareFootage = squareFootage;
    }

    @Override
    public void displayDetails() {
        System.out.println("House Details: " + address + " - " + bedrooms + " beds, " +
                bathrooms + " baths, " + squareFootage + " sqft");
    }
}