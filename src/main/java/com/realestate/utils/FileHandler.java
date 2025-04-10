package com.realestate.utils;

import com.realestate.models.*;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileHandler {
    private static final Logger LOGGER = Logger.getLogger(FileHandler.class.getName());
    private static final String DATA_DIR = "sample-data/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String AGENTS_FILE = DATA_DIR + "agents.txt";
    private static final String PROPERTIES_FILE = DATA_DIR + "properties.txt";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.txt";
    private static final String REVIEWS_FILE = DATA_DIR + "reviews.txt";

    // Initialize data directory
    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            LOGGER.info("Data directory initialized: " + DATA_DIR);
        } catch (IOException e) {
            LOGGER.severe("Failed to create data directory: " + e.getMessage());
            throw new RuntimeException("Failed to initialize data directory", e);
        }
    }

    // User file operations
    public static void saveUsers(List<User> users) throws IOException {
        if (users == null) {
            throw new IllegalArgumentException("Users list cannot be null");
        }
        try {
            saveToFile(users, USERS_FILE);
            LOGGER.info("Successfully saved " + users.size() + " users to file");
        } catch (IOException e) {
            LOGGER.severe("Failed to save users: " + e.getMessage());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() throws IOException, ClassNotFoundException {
        try {
            List<User> users = (List<User>) loadFromFile(USERS_FILE);
            LOGGER.info("Successfully loaded " + users.size() + " users from file");
            return users;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Failed to load users: " + e.getMessage());
            throw e;
        }
    }

    // Agent file operations
    public static void saveAgents(List<Agent> agents) throws IOException {
        saveToFile(agents, AGENTS_FILE);
    }

    @SuppressWarnings("unchecked")
    public static List<Agent> loadAgents() throws IOException, ClassNotFoundException {
        return (List<Agent>) loadFromFile(AGENTS_FILE);
    }

    // Property file operations
    public static void saveProperties(List<Property> properties) throws IOException {
        saveToFile(properties, PROPERTIES_FILE);
    }

    @SuppressWarnings("unchecked")
    public static List<Property> loadProperties() throws IOException, ClassNotFoundException {
        return (List<Property>) loadFromFile(PROPERTIES_FILE);
    }

    // Appointment file operations
    public static void saveAppointments(List<Appointment> appointments) throws IOException {
        // Implementation for saving appointments
        // This is a placeholder - implement actual saving logic if needed
    }

    public static List<Appointment> loadAppointments() throws IOException {
        List<Appointment> appointments = new ArrayList<>();
        Path filePath = Paths.get(DATA_DIR, "appointments.txt");
        
        if (!Files.exists(filePath)) {
            return appointments;
        }

        String content = Files.readString(filePath);
        if (content.trim().isEmpty()) {
            return appointments;
        }

        // Initialize sample data if file is empty
        if (content.trim().equals("[]")) {
            appointments.add(new Appointment(1, 1, 1, 1,
                    LocalDateTime.of(2023, 12, 15, 14, 30), "Property viewing", "Scheduled"));
            appointments.add(new Appointment(2, 1, 2, 2,
                    LocalDateTime.of(2023, 12, 16, 10, 0), "Initial consultation", "Completed"));
            saveAppointments(appointments);
            return appointments;
        }

        // Parse JSON array
        content = content.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1);
        }

        String[] appointmentObjects = content.split("\\},\\s*\\{");
        for (String appointmentStr : appointmentObjects) {
            if (!appointmentStr.trim().isEmpty()) {
                // Clean up the JSON object string
                if (!appointmentStr.startsWith("{")) appointmentStr = "{" + appointmentStr;
                if (!appointmentStr.endsWith("}")) appointmentStr = appointmentStr + "}";

                // Extract values
                int id = extractInt(appointmentStr, "id");
                int clientId = extractInt(appointmentStr, "clientId");
                int agentId = extractInt(appointmentStr, "agentId");
                int propertyId = extractInt(appointmentStr, "propertyId");
                String dateTimeStr = extractString(appointmentStr, "appointmentTime");
                String purpose = extractString(appointmentStr, "purpose");
                String status = extractString(appointmentStr, "status");

                // Parse the datetime string
                LocalDateTime appointmentTime = LocalDateTime.parse(dateTimeStr);

                // Create and add the appointment
                appointments.add(new Appointment(id, clientId, agentId, propertyId,
                        appointmentTime, purpose, status));
            }
        }

        return appointments;
    }

    private static int extractInt(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalArgumentException("Field " + field + " not found in JSON: " + json);
    }

    private static String extractString(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        throw new IllegalArgumentException("Field " + field + " not found in JSON: " + json);
    }

    // Review file operations
    public static void saveReviews(List<Review> reviews) throws IOException {
        saveToFile(reviews, REVIEWS_FILE);
    }

    @SuppressWarnings("unchecked")
    public static List<Review> loadReviews() throws IOException, ClassNotFoundException {
        return (List<Review>) loadFromFile(REVIEWS_FILE);
    }

    // Generic save method
    private static void saveToFile(Object data, String filename) throws IOException {
        Path filePath = Paths.get(filename);
        Path tempFile = Paths.get(filename + ".tmp");
        
        try {
            // Write to temporary file first
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(tempFile)))) {
                oos.writeObject(data);
            }
            
            // If write was successful, move temp file to actual file
            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // Clean up temp file if it exists
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException deleteError) {
                LOGGER.warning("Failed to delete temporary file: " + deleteError.getMessage());
            }
            throw e;
        }
    }

    // Generic load method
    private static Object loadFromFile(String filename) throws IOException, ClassNotFoundException {
        Path filePath = Paths.get(filename);
        
        if (!Files.exists(filePath)) {
            LOGGER.info("File does not exist, returning empty list: " + filename);
            return new ArrayList<>();
        }

        if (Files.size(filePath) == 0) {
            LOGGER.info("File is empty, returning empty list: " + filename);
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(filePath)))) {
            Object data = ois.readObject();
            if (data == null) {
                LOGGER.warning("Loaded null data from file: " + filename);
                return new ArrayList<>();
            }
            return data;
        }
    }

    // Helper method to initialize sample data if files are empty
    public static void initializeSampleData() {
        try {
            boolean dataInitialized = false;

            // Check and initialize users
            if (!Files.exists(Paths.get(USERS_FILE)) || Files.size(Paths.get(USERS_FILE)) == 0) {
                List<User> users = new ArrayList<>();
                users.add(new ClientUser(1, "john_doe", "password123", "john@example.com", "1234567890"));
                users.add(new AgentUser(2, "jane_smith", "password456", "jane@example.com", "9876543210",
                        "REA12345", "Luxury Homes", 5));
                saveUsers(users);
                dataInitialized = true;
            }

            // Check and initialize agents
            if (new File(AGENTS_FILE).length() == 0) {
                List<Agent> agents = new ArrayList<>();
                agents.add(new Agent(1, "Jane Smith", "jane@example.com", "New York", "Luxury Homes", 4.8));
                agents.add(new Agent(2, "Mike Johnson", "mike@example.com", "Los Angeles", "Commercial Properties", 4.5));
                agents.add(new Agent(3, "Sarah Williams", "sarah@example.com", "Chicago", "Apartments", 4.2));
                saveAgents(agents);
            }

            // Check and initialize properties
            if (new File(PROPERTIES_FILE).length() == 0) {
                List<Property> properties = new ArrayList<>();
                properties.add(new House(1, "123 Main St, New York", 750000.0,
                        "Beautiful 3-bedroom house with garden", 3, 2, 1800.0));
                properties.add(new Apartment(2, "456 Downtown Ave, Los Angeles", 450000.0,
                        "Modern apartment with city views", 502, 2, 1, true));
                properties.add(new Land(3, "789 Rural Rd, Texas", 150000.0,
                        "10-acre plot with water access", 10.0, "Residential"));
                saveProperties(properties);
            }

            // Check and initialize appointments
            if (new File(APPOINTMENTS_FILE).length() == 0) {
                List<Appointment> appointments = new ArrayList<>();
                appointments.add(new Appointment(1, 1, 1, 1,
                        java.time.LocalDateTime.of(2023, 12, 15, 14, 30), "Property viewing", "Scheduled"));
                appointments.add(new Appointment(2, 1, 2, 2,
                        java.time.LocalDateTime.of(2023, 12, 16, 10, 0), "Initial consultation", "Completed"));
                saveAppointments(appointments);
            }

            // Check and initialize reviews
            if (new File(REVIEWS_FILE).length() == 0) {
                List<Review> reviews = new ArrayList<>();
                reviews.add(new Review(1, 1, 1, 5,
                        "Jane was very professional and helped us find our dream home!"));
                reviews.add(new Review(2, 1, 2, 4,
                        "Mike knows the market well, but was sometimes hard to reach."));
                saveReviews(reviews);
            }

            if (dataInitialized) {
                LOGGER.info("Sample data initialized successfully");
            }

        } catch (IOException e) {
            LOGGER.severe("Failed to initialize sample data: " + e.getMessage());
            throw new RuntimeException("Failed to initialize sample data", e);
        }
    }

    // Utility method to clear all data (for testing)
    public static void clearAllData() {
        try {
            Files.deleteIfExists(Paths.get(USERS_FILE));
            Files.deleteIfExists(Paths.get(AGENTS_FILE));
            Files.deleteIfExists(Paths.get(PROPERTIES_FILE));
            Files.deleteIfExists(Paths.get(APPOINTMENTS_FILE));
            Files.deleteIfExists(Paths.get(REVIEWS_FILE));
            LOGGER.info("All data files cleared successfully");
        } catch (IOException e) {
            LOGGER.severe("Failed to clear data files: " + e.getMessage());
            throw new RuntimeException("Failed to clear data files", e);
        }
    }
}