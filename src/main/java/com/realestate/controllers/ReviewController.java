package com.realestate.controllers;

import com.realestate.models.*;
import com.realestate.services.ReviewService;
import com.realestate.services.AgentService;
import com.realestate.services.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ReviewController", value = "/review/*")
public class ReviewController extends HttpServlet {
    private ReviewService reviewService = new ReviewService();
    private UserService userService = new UserService();
    private AgentService agentService = new AgentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        try {
            switch (action) {
                case "/add":
                    showAddForm(request, response);
                    break;
                case "/list":
                    listReviews(request, response);
                    break;
                case "/view":
                    viewReview(request, response);
                    break;
                case "/edit":
                    showEditForm(request, response);
                    break;
                default:
                    response.sendRedirect("../index.jsp");
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        try {
            switch (action) {
                case "/add":
                    addReview(request, response);
                    break;
                case "/update":
                    updateReview(request, response);
                    break;
                case "/delete":
                    deleteReview(request, response);
                    break;
                default:
                    response.sendRedirect("../index.jsp");
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"client".equals(user.getUserType())) {
            response.sendRedirect("../user/login");
            return;
        }

        int agentId = Integer.parseInt(request.getParameter("agentId"));
        Agent agent = agentService.getAgentById(agentId);
        if (agent == null) {
            response.sendRedirect("../agent/list");
            return;
        }

        request.setAttribute("agent", agent);
        request.getRequestDispatcher("/WEB-INF/views/reviewAdd.jsp").forward(request, response);
    }

    private void listReviews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String agentIdParam = request.getParameter("agentId");
        if (agentIdParam != null) {
            int agentId = Integer.parseInt(agentIdParam);
            Agent agent = agentService.getAgentById(agentId);
            if (agent != null) {
                List<Review> reviews = reviewService.getReviewsForAgent(agentId);
                List<EnhancedReview> enhancedReviews = new ArrayList<>();

                for (Review review : reviews) {
                    EnhancedReview enhanced = new EnhancedReview();
                    enhanced.setReview(review);
                    enhanced.setClient(userService.getUserById(review.getClientId()));
                    enhanced.setAgent(agent);
                    enhancedReviews.add(enhanced);
                }

                request.setAttribute("reviews", enhancedReviews);
                request.setAttribute("agent", agent);
                request.setAttribute("averageRating", reviewService.getAverageRatingForAgent(agentId));
                request.getRequestDispatcher("/WEB-INF/views/reviewList.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect("../agent/list");
    }

    private void viewReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            response.sendRedirect("../review/list");
            return;
        }

        EnhancedReview enhanced = new EnhancedReview();
        enhanced.setReview(review);
        enhanced.setClient(userService.getUserById(review.getClientId()));
        enhanced.setAgent(agentService.getAgentById(review.getAgentId()));

        request.setAttribute("review", enhanced);
        request.getRequestDispatcher("/WEB-INF/views/reviewView.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"client".equals(user.getUserType())) {
            response.sendRedirect("../user/login");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        Review review = reviewService.getReviewById(id);
        if (review == null || review.getClientId() != user.getId()) {
            response.sendRedirect("../review/list");
            return;
        }

        EnhancedReview enhanced = new EnhancedReview();
        enhanced.setReview(review);
        enhanced.setClient(user);
        enhanced.setAgent(agentService.getAgentById(review.getAgentId()));

        request.setAttribute("review", enhanced);
        request.getRequestDispatcher("/WEB-INF/views/reviewEdit.jsp").forward(request, response);
    }

    private void addReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"client".equals(user.getUserType())) {
            response.sendRedirect("../user/login");
            return;
        }

        int agentId = Integer.parseInt(request.getParameter("agentId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        Review review = new Review(0, user.getId(), agentId, rating, comment);
        if (reviewService.addReview(review)) {
            response.sendRedirect("../review/list?agentId=" + agentId + "&added=true");
        } else {
            request.setAttribute("error", "Failed to add review");
            Agent agent = agentService.getAgentById(agentId);
            request.setAttribute("agent", agent);
            request.getRequestDispatcher("/WEB-INF/views/reviewAdd.jsp").forward(request, response);
        }
    }

    private void updateReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"client".equals(user.getUserType())) {
            response.sendRedirect("../user/login");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        Review existingReview = reviewService.getReviewById(id);
        if (existingReview == null || existingReview.getClientId() != user.getId()) {
            response.sendRedirect("../review/list");
            return;
        }

        existingReview.setRating(rating);
        existingReview.setComment(comment);

        if (reviewService.updateReview(existingReview)) {
            response.sendRedirect("../review/view?id=" + id + "&updated=true");
        } else {
            request.setAttribute("error", "Failed to update review");
            EnhancedReview enhanced = new EnhancedReview();
            enhanced.setReview(existingReview);
            enhanced.setClient(user);
            enhanced.setAgent(agentService.getAgentById(existingReview.getAgentId()));

            request.setAttribute("review", enhanced);
            request.getRequestDispatcher("/WEB-INF/views/reviewEdit.jsp").forward(request, response);
        }
    }

    private void deleteReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("../user/login");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        Review review = reviewService.getReviewById(id);

        // Only allow deletion if user is admin or the review author
        if (review == null || (!"admin".equals(user.getUserType()) && review.getClientId() != user.getId())) {
            response.sendRedirect("../review/list");
            return;
        }

        int agentId = review.getAgentId();
        if (reviewService.deleteReview(id)) {
            response.sendRedirect("../review/list?agentId=" + agentId + "&deleted=true");
        } else {
            request.setAttribute("error", "Failed to delete review");
            request.getRequestDispatcher("/WEB-INF/views/reviewView.jsp").forward(request, response);
        }
    }

    // Helper class to enhance review data with related objects
    private static class EnhancedReview {
        private Review review;
        private User client;
        private Agent agent;

        // Getters and Setters
        public Review getReview() {
            return review;
        }

        public void setReview(Review review) {
            this.review = review;
        }

        public User getClient() {
            return client;
        }

        public void setClient(User client) {
            this.client = client;
        }

        public Agent getAgent() {
            return agent;
        }

        public void setAgent(Agent agent) {
            this.agent = agent;
        }
    }
}