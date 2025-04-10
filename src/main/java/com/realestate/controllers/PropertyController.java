package com.realestate.controllers;

import com.realestate.models.*;
import com.realestate.services.PropertyService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PropertyController", value = "/property/*")
public class PropertyController extends HttpServlet {
    private PropertyService propertyService = new PropertyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/";

        try {
            switch (action) {
                case "/add":
                    showAddForm(request, response);
                    break;
                case "/search":
                    searchProperties(request, response);
                    break;
                case "/list":
                    listProperties(request, response);
                    break;
                case "/view":
                    viewProperty(request, response);
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
                    addProperty(request, response);
                    break;
                case "/update":
                    updateProperty(request, response);
                    break;
                case "/delete":
                    deleteProperty(request, response);
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
        request.getRequestDispatcher("/WEB-INF/views/propertyAdd.jsp").forward(request, response);
    }

    private void searchProperties(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String location = request.getParameter("location");
        String type = request.getParameter("type");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");

        double minPrice = minPriceStr != null && !minPriceStr.isEmpty() ? Double.parseDouble(minPriceStr) : 0;
        double maxPrice = maxPriceStr != null && !maxPriceStr.isEmpty() ? Double.parseDouble(maxPriceStr) : Double.MAX_VALUE;

        List<Property> allProperties = propertyService.getAllProperties();
        List<Property> filteredProperties = new ArrayList<>();

        for (Property property : allProperties) {
            boolean matches = true;

            if (location != null && !location.isEmpty() && !property.getAddress().contains(location)) {
                matches = false;
            }

            if (type != null && !type.isEmpty()) {
                if (type.equals("house") && !(property instanceof House)) {
                    matches = false;
                } else if (type.equals("apartment") && !(property instanceof Apartment)) {
                    matches = false;
                } else if (type.equals("land") && !(property instanceof Land)) {
                    matches = false;
                }
            }

            if (property.getPrice() < minPrice || property.getPrice() > maxPrice) {
                matches = false;
            }

            if (matches) {
                filteredProperties.add(property);
            }
        }

        request.setAttribute("properties", filteredProperties);
        request.getRequestDispatcher("/WEB-INF/views/propertyList.jsp").forward(request, response);
    }

    private void listProperties(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("properties", propertyService.getAllProperties());
        request.getRequestDispatcher("/WEB-INF/views/propertyList.jsp").forward(request, response);
    }

    private void viewProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Property property = propertyService.getPropertyById(id);
        if (property == null) {
            response.sendRedirect("../property/list");
            return;
        }
        request.setAttribute("property", property);
        request.getRequestDispatcher("/WEB-INF/views/propertyView.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Property property = propertyService.getPropertyById(id);
        if (property == null) {
            response.sendRedirect("../property/list");
            return;
        }
        request.setAttribute("property", property);
        request.getRequestDispatcher("/WEB-INF/views/propertyEdit.jsp").forward(request, response);
    }

    private void addProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String address = request.getParameter("address");
        double price = Double.parseDouble(request.getParameter("price"));
        String description = request.getParameter("description");

        Property property;
        switch (type) {
            case "house":
                int bedrooms = Integer.parseInt(request.getParameter("bedrooms"));
                int bathrooms = Integer.parseInt(request.getParameter("bathrooms"));
                double squareFootage = Double.parseDouble(request.getParameter("squareFootage"));
                property = new House(0, address, price, description, bedrooms, bathrooms, squareFootage);
                break;
            case "apartment":
                int unitNumber = Integer.parseInt(request.getParameter("unitNumber"));
                int aptBedrooms = Integer.parseInt(request.getParameter("bedrooms"));
                int aptBathrooms = Integer.parseInt(request.getParameter("bathrooms"));
                boolean hasParking = "on".equals(request.getParameter("hasParking"));
                property = new Apartment(0, address, price, description, unitNumber, aptBedrooms, aptBathrooms, hasParking);
                break;
            case "land":
                double acreage = Double.parseDouble(request.getParameter("acreage"));
                String zoningType = request.getParameter("zoningType");
                property = new Land(0, address, price, description, acreage, zoningType);
                break;
            default:
                property = new House(0, address, price, description, 0, 0, 0);
        }

        if (propertyService.addProperty(property)) {
            response.sendRedirect("../property/list?added=true");
        } else {
            request.setAttribute("error", "Failed to add property");
            request.getRequestDispatcher("/WEB-INF/views/propertyAdd.jsp").forward(request, response);
        }
    }

    private void updateProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String address = request.getParameter("address");
        double price = Double.parseDouble(request.getParameter("price"));
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        Property existingProperty = propertyService.getPropertyById(id);
        if (existingProperty == null) {
            response.sendRedirect("../property/list");
            return;
        }

        existingProperty.setAddress(address);
        existingProperty.setPrice(price);
        existingProperty.setDescription(description);
        existingProperty.setStatus(status);

        if (existingProperty instanceof House) {
            ((House) existingProperty).setBedrooms(Integer.parseInt(request.getParameter("bedrooms")));
            ((House) existingProperty).setBathrooms(Integer.parseInt(request.getParameter("bathrooms")));
            ((House) existingProperty).setSquareFootage(Double.parseDouble(request.getParameter("squareFootage")));
        } else if (existingProperty instanceof Apartment) {
            ((Apartment) existingProperty).setUnitNumber(Integer.parseInt(request.getParameter("unitNumber")));
            ((Apartment) existingProperty).setBedrooms(Integer.parseInt(request.getParameter("bedrooms")));
            ((Apartment) existingProperty).setBathrooms(Integer.parseInt(request.getParameter("bathrooms")));
            ((Apartment) existingProperty).setHasParking("on".equals(request.getParameter("hasParking")));
        } else if (existingProperty instanceof Land) {
            ((Land) existingProperty).setAcreage(Double.parseDouble(request.getParameter("acreage")));
            ((Land) existingProperty).setZoningType(request.getParameter("zoningType"));
        }

        if (propertyService.updateProperty(existingProperty)) {
            response.sendRedirect("../property/view?id=" + id + "&updated=true");
        } else {
            request.setAttribute("error", "Failed to update property");
            request.setAttribute("property", existingProperty);
            request.getRequestDispatcher("/WEB-INF/views/propertyEdit.jsp").forward(request, response);
        }
    }

    private void deleteProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        if (propertyService.deleteProperty(id)) {
            response.sendRedirect("../property/list?deleted=true");
        } else {
            request.setAttribute("error", "Failed to delete property");
            request.getRequestDispatcher("/WEB-INF/views/propertyView.jsp").forward(request, response);
        }
    }
}