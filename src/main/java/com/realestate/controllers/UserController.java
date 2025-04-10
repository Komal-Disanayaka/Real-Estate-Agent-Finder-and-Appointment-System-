package com.realestate.controllers;

import com.realestate.models.*;
import com.realestate.services.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;


@WebServlet(name = "UserController", value = "/user/*")
public class UserController extends HttpServlet {
    private UserService userService = new UserService();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        try {
            switch (action) {
                case "/register":
                    showRegisterForm(request, response);
                    break;
                case "/login":
                    showLoginForm(request, response);
                    break;
                case "/profile":
                    showProfile(request, response);
                    break;
                case "/list":
                    listUsers(request, response);
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
                case "/register":
                    registerUser(request, response);
                    break;
                case "/login":
                    loginUser(request, response);
                    break;
                case "/update":
                    updateUser(request, response);
                    break;
                case "/delete":
                    deleteUser(request, response);
                    break;
                default:
                    response.sendRedirect("../index.jsp");
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    private void showProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("../user/login");
            return;
        }
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This would typically be admin-only functionality
        // For simplicity, we're not implementing authentication/authorization here
        request.setAttribute("users", userService.listAllUsers());
        request.getRequestDispatcher("/WEB-INF/views/userList.jsp").forward(request, response);
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String userType = request.getParameter("userType");

            // Validate required fields
            if (username == null || username.trim().isEmpty()) {
                request.setAttribute("error", "Username is required");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                request.setAttribute("error", "Password is required");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Email is required");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            if (phone == null || phone.trim().isEmpty()) {
                request.setAttribute("error", "Phone number is required");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            User user;
            if ("agent".equals(userType)) {
                String licenseNumber = request.getParameter("licenseNumber");
                String specialization = request.getParameter("specialization");
                String yearsOfExperience = request.getParameter("yearsOfExperience");

                // Validate agent-specific fields
                if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
                    request.setAttribute("error", "License number is required for agents");
                    request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                    return;
                }

                if (specialization == null || specialization.trim().isEmpty()) {
                    request.setAttribute("error", "Specialization is required for agents");
                    request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                    return;
                }

                if (yearsOfExperience == null || yearsOfExperience.trim().isEmpty()) {
                    request.setAttribute("error", "Years of experience is required for agents");
                    request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                    return;
                }

                user = new AgentUser(0, username, password, email, phone, licenseNumber, specialization, Integer.parseInt(yearsOfExperience));
            } else {
                user = new ClientUser(0, username, password, email, phone);
            }

            if (userService.registerUser(user)) {
                response.sendRedirect("../user/login?registered=true");
            } else {
                request.setAttribute("error", "Registration failed. The username may already be taken. Please try a different username.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "An unexpected error occurred during registration. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userService.authenticate(username, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("../index.jsp");
        } else {
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("../user/login");
            return;
        }

        User user = userService.getUserById(currentUser.getId());
        if (user == null) {
            response.sendRedirect("../user/login");
            return;
        }

        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));

        if (user instanceof ClientUser) {
            ((ClientUser) user).setPreferences(request.getParameter("preferences"));
        } else if (user instanceof AgentUser) {
            ((AgentUser) user).setSpecialization(request.getParameter("specialization"));
            ((AgentUser) user).setYearsOfExperience(Integer.parseInt(request.getParameter("yearsOfExperience")));
        }

        if (userService.updateUser(user)) {
            session.setAttribute("user", user);
            request.setAttribute("success", "Profile updated successfully");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Failed to update profile");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("../user/login");
            return;
        }

        if (userService.deleteUser(user.getId())) {
            session.invalidate();
            response.sendRedirect("../index.jsp?accountDeleted=true");
        } else {
            request.setAttribute("error", "Failed to delete account");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
        }
    }
}