package com.realestate.models;

public class Land extends Property {
    private double acreage;
    private String zoningType;

    public Land(int id, String address, double price, String description,
                double acreage, String zoningType) {
        super(id, address, price, description);
        this.acreage = acreage;
        this.zoningType = zoningType;
    }

    // Getters and Setters
    public double getAcreage() {
        return acreage;
    }

    public void setAcreage(double acreage) {
        this.acreage = acreage;
    }

    public String getZoningType() {
        return zoningType;
    }

    public void setZoningType(String zoningType) {
        this.zoningType = zoningType;
    }

    @Override
    public void displayDetails() {
        System.out.println("Land Details: " + address + " - " + acreage + " acres, " +
                zoningType + " zoning");
    }
}