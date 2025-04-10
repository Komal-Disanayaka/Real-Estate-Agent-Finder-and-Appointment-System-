package com.realestate.controllers;

import com.realestate.models.Agent;
import com.realestate.services.AgentService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AgentController", value = "/agent/*")
public class AgentController extends HttpServlet {
    private AgentService agentService = new AgentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        try {
            switch (action) {
                case "/register":
                    showRegisterForm(request, response);
                    break;
                case "/search":
                    searchAgents(request, response);
                    break;
                case "/list":
                    listAgents(request, response);
                    break;
                case "/view":
                    viewAgent(request, response);
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
                case "/register":
                    registerAgent(request, response);
                    break;
                case "/update":
                    updateAgent(request, response);
                    break;
                case "/delete":
                    deleteAgent(request, response);
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
        request.getRequestDispatcher("/WEB-INF/views/agentRegister.jsp").forward(request, response);
    }

    private void searchAgents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String location = request.getParameter("location");
        String specialization = request.getParameter("specialization");

        if (name != null && !name.isEmpty()) {
            Agent agent = agentService.searchAgentByName(name);
            request.setAttribute("agents", agent != null ? List.of(agent) : List.of());
        } else {
            // For simplicity, we're just filtering the full list here
            // In a real application, you'd implement proper search functionality
            List<Agent> allAgents = agentService.getAllAgents();
            List<Agent> filteredAgents = new ArrayList<>();

            for (Agent agent : allAgents) {
                if ((location == null || location.isEmpty() || agent.getLocation().contains(location)) &&
                        (specialization == null || specialization.isEmpty() || agent.getSpecialization().contains(specialization))) {
                    filteredAgents.add(agent);
                }
            }

            request.setAttribute("agents", filteredAgents);
        }

        request.getRequestDispatcher("/WEB-INF/views/agentList.jsp").forward(request, response);
    }

    private void listAgents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sortBy = request.getParameter("sortBy");
        List<Agent> agents;

        if ("rating".equals(sortBy)) {
            agents = agentService.getAgentsSortedByRating();
        } else {
            agents = agentService.getAllAgents();
        }

        request.setAttribute("agents", agents);
        request.getRequestDispatcher("/WEB-INF/views/agentList.jsp").forward(request, response);
    }

    private void viewAgent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Agent agent = agentService.getAgentById(id);
        if (agent == null) {
            response.sendRedirect("../agent/list");
            return;
        }
        request.setAttribute("agent", agent);
        request.getRequestDispatcher("/WEB-INF/views/agentView.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Agent agent = agentService.getAgentById(id);
        if (agent == null) {
            response.sendRedirect("../agent/list");
            return;
        }
        request.setAttribute("agent", agent);
        request.getRequestDispatcher("/WEB-INF/views/agentEdit.jsp").forward(request, response);
    }

    private void registerAgent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String contact = request.getParameter("contact");
        String location = request.getParameter("location");
        String specialization = request.getParameter("specialization");
        double rating = Double.parseDouble(request.getParameter("rating"));

        Agent agent = new Agent(0, name, contact, location, specialization, rating);
        if (agentService.addAgent(agent)) {
            response.sendRedirect("../agent/list?registered=true");
        } else {
            request.setAttribute("error", "Failed to register agent");
            request.getRequestDispatcher("/WEB-INF/views/agentRegister.jsp").forward(request, response);
        }
    }

    private void updateAgent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String contact = request.getParameter("contact");
        String location = request.getParameter("location");
        String specialization = request.getParameter("specialization");
        double rating = Double.parseDouble(request.getParameter("rating"));

        Agent agent = new Agent(id, name, contact, location, specialization, rating);
        if (agentService.updateAgent(agent)) {
            response.sendRedirect("../agent/view?id=" + id + "&updated=true");
        } else {
            request.setAttribute("error", "Failed to update agent");
            request.setAttribute("agent", agent);
            request.getRequestDispatcher("/WEB-INF/views/agentEdit.jsp").forward(request, response);
        }
    }

    private void deleteAgent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        if (agentService.deleteAgent(id)) {
            response.sendRedirect("../agent/list?deleted=true");
        } else {
            request.setAttribute("error", "Failed to delete agent");
            request.getRequestDispatcher("/WEB-INF/views/agentView.jsp").forward(request, response);
        }
    }
}