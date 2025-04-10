package com.realestate.models;

import java.time.LocalDate;

public class Review {
    private int id;
    private int clientId;
    private int agentId;
    private int rating;
    private String comment;
    private LocalDate reviewDate;

    public Review(int id, int clientId, int agentId, int rating, String comment) {
        this.id = id;
        this.clientId = clientId;
        this.agentId = agentId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = LocalDate.now();
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
}