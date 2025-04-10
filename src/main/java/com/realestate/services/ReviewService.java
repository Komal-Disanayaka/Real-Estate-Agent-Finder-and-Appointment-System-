package com.realestate.services;

import com.realestate.models.Review;
import com.realestate.utils.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {
    private List<Review> reviews;
    private int nextId = 1;

    public ReviewService() {
        try {
            reviews = FileHandler.loadReviews();
            for (Review review : reviews) {
                if (review.getId() >= nextId) {
                    nextId = review.getId() + 1;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            reviews = new ArrayList<>();
        }
    }

    public boolean addReview(Review review) {
        review.setId(nextId++);
        reviews.add(review);
        try {
            FileHandler.saveReviews(reviews);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Review getReviewById(int id) {
        for (Review review : reviews) {
            if (review.getId() == id) {
                return review;
            }
        }
        return null;
    }

    public List<Review> getReviewsForAgent(int agentId) {
        List<Review> agentReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (review.getAgentId() == agentId) {
                agentReviews.add(review);
            }
        }
        return agentReviews;
    }

    public double getAverageRatingForAgent(int agentId) {
        List<Review> agentReviews = getReviewsForAgent(agentId);
        if (agentReviews.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (Review review : agentReviews) {
            total += review.getRating();
        }
        return (double) total / agentReviews.size();
    }

    public boolean updateReview(Review updatedReview) {
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId() == updatedReview.getId()) {
                reviews.set(i, updatedReview);
                try {
                    FileHandler.saveReviews(reviews);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean deleteReview(int id) {
        for (Review review : reviews) {
            if (review.getId() == id) {
                reviews.remove(review);
                try {
                    FileHandler.saveReviews(reviews);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return false;
    }
}