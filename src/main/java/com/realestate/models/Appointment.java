package com.realestate.models;

import java.time.LocalDateTime;
import java.io.Serializable;

public class Appointment implements Serializable {
    private int id;
    private int clientId;
    private int agentId;
    private int propertyId;
    private LocalDateTime appointmentTime;
    private String purpose;
    private String status; // Scheduled, Completed, Cancelled

    public Appointment() {}

    public Appointment(int id, int clientId, int agentId, int propertyId,
                       LocalDateTime appointmentTime, String purpose, String status) {
        this.id = id;
        this.clientId = clientId;
        this.agentId = agentId;
        this.propertyId = propertyId;
        this.appointmentTime = appointmentTime;
        this.purpose = purpose;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}