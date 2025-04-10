package com.realestate.controllers;

import com.realestate.models.*;
import com.realestate.services.AppointmentService;
import com.realestate.services.AgentService;
import com.realestate.services.PropertyService;
import com.realestate.services.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AppointmentController", value = "/appointment/*")
public class AppointmentController extends HttpServlet {
    private AppointmentService appointmentService = new AppointmentService();
    private UserService userService = new UserService();
    private AgentService agentService = new AgentService();
    private PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        // Check if user is logged in and is an agent
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"agent".equals(user.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        try {
            switch (action) {
                case "/list":
                    listAppointments(request, response);
                    break;
                case "/filter":
                    filterAppointments(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/");
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

        // Check if user is logged in and is an agent
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"agent".equals(user.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        try {
            switch (action) {
                case "/confirm":
                    confirmAppointment(request, response);
                    break;
                case "/complete":
                    completeAppointment(request, response);
                    break;
                case "/cancel":
                    cancelAppointment(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/");
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void listAppointments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User agent = (User) request.getSession().getAttribute("user");
        List<Appointment> appointments = appointmentService.getAppointmentsByAgent(agent.getId());
        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("/WEB-INF/views/appointmentList.jsp").forward(request, response);
    }

    private void filterAppointments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String status = request.getParameter("status");
        String date = request.getParameter("date");
        User agent = (User) request.getSession().getAttribute("user");
        
        List<Appointment> appointments = appointmentService.filterAppointments(agent.getId(), status, date);
        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("/WEB-INF/views/appointmentList.jsp").forward(request, response);
    }

    private void confirmAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int appointmentId = Integer.parseInt(request.getParameter("id"));
        appointmentService.updateAppointmentStatus(appointmentId, "confirmed");
        response.sendRedirect(request.getContextPath() + "/appointment/list");
    }

    private void completeAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int appointmentId = Integer.parseInt(request.getParameter("id"));
        appointmentService.updateAppointmentStatus(appointmentId, "completed");
        response.sendRedirect(request.getContextPath() + "/appointment/list");
    }

    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int appointmentId = Integer.parseInt(request.getParameter("id"));
        appointmentService.updateAppointmentStatus(appointmentId, "cancelled");
        response.sendRedirect(request.getContextPath() + "/appointment/list");
    }

    // Helper class to enhance appointment data with related objects
    private static class EnhancedAppointment {
        private Appointment appointment;
        private User client;
        private Agent agent;
        private Property property;

        // Getters and Setters
        public Appointment getAppointment() {
            return appointment;
        }

        public void setAppointment(Appointment appointment) {
            this.appointment = appointment;
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

        public Property getProperty() {
            return property;
        }

        public void setProperty(Property property) {
            this.property = property;
        }
    }
}