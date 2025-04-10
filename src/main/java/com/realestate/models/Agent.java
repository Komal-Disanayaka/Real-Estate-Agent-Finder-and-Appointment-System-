package com.realestate.models;

import java.io.Serializable;

public class Agent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String contact;
    private String location;
    private String specialization;
    private double rating;

    public Agent(int id, String name, String contact, String location, String specialization, double rating) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.location = location;
        this.specialization = specialization;
        this.rating = rating;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", specialization='" + specialization + '\'' +
                ", rating=" + rating +
                '}';
    }
}