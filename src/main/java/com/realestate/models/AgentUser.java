package com.realestate.models;

public class AgentUser extends User {
    private String licenseNumber;
    private String specialization;
    private int yearsOfExperience;

    public AgentUser(int id, String username, String password, String email, String phone,
                     String licenseNumber, String specialization, int yearsOfExperience) {
        super(id, username, password, email, phone, "agent");
        this.licenseNumber = licenseNumber;
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    @Override
    public void displayProfile() {
        System.out.println("Agent Profile:");
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("License Number: " + getLicenseNumber());
        System.out.println("Specialization: " + getSpecialization());
        System.out.println("Years of Experience: " + getYearsOfExperience());
    }
}