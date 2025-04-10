package com.realestate.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/user/logout")
public class LogoutServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LogoutServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get the current session
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                try {
                    // Remove user attribute
                    session.removeAttribute("user");
                    // Invalidate the session
                    session.invalidate();
                } catch (IllegalStateException e) {
                    // Session was already invalidated
                    logger.log(Level.INFO, "Session was already invalidated", e);
                }
            }
            
            // Redirect to the home page with a logged out message
            response.sendRedirect(request.getContextPath() + "/?loggedOut=true");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during logout process", e);
            throw new ServletException("Error processing logout request", e);
        }
    }
} 