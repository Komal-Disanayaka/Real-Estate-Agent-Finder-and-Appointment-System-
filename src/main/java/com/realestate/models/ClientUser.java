package com.realestate.models;

public class ClientUser extends User {
    private String preferences;

    public ClientUser(int id, String username, String password, String email, String phone) {
        super(id, username, password, email, phone, "client");
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    @Override
    public void displayProfile() {
        System.out.println("Client Profile: " + username);
    }
}