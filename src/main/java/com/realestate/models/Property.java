package com.realestate.models;

import java.io.Serializable;

public abstract class Property implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected int id;
    protected String address;
    protected double price;
    protected String description;
    protected String status; // Available, Sold, Pending

    public Property(int id, String address, double price, String description) {
        this.id = id;
        this.address = address;
        this.price = price;
        this.description = description;
        this.status = "Available";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract void displayDetails();
}