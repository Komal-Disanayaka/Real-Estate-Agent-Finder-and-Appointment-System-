package com.realestate.services;

import com.realestate.models.*;
import com.realestate.utils.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PropertyService {
    private static final Logger LOGGER = Logger.getLogger(PropertyService.class.getName());
    private final List<Property> properties;
    private int nextId = 1;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PropertyService() {
        properties = new ArrayList<>();
        try {
            lock.writeLock().lock();
            List<Property> loadedProperties = FileHandler.loadProperties();
            properties.addAll(loadedProperties);
            
            // If no properties exist, initialize with sample data
            if (properties.isEmpty()) {
                initializeSampleProperties();
            }
            
            for (Property property : properties) {
                if (property.getId() >= nextId) {
                    nextId = property.getId() + 1;
                }
            }
            LOGGER.info("Successfully initialized PropertyService with " + properties.size() + " properties");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Failed to load properties: " + e.getMessage());
            // Initialize with sample data if loading fails
            initializeSampleProperties();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void initializeSampleProperties() {
        List<Property> sampleProperties = Arrays.asList(
            new House(1, "123 Main St, New York", 750000.0,
                    "Beautiful 3-bedroom house with garden", 3, 2, 1800.0),
            new Apartment(2, "456 Downtown Ave, Los Angeles", 450000.0,
                    "Modern apartment with city views", 502, 2, 1, true),
            new Land(3, "789 Rural Rd, Texas", 150000.0,
                    "10-acre plot with water access", 10.0, "Residential")
        );
        
        for (Property property : sampleProperties) {
            addProperty(property);
        }
        LOGGER.info("Initialized system with " + sampleProperties.size() + " sample properties");
    }

    public boolean addProperty(Property property) {
        if (property == null) {
            LOGGER.warning("Attempted to add null property");
            throw new IllegalArgumentException("Property cannot be null");
        }

        if (property.getAddress() == null || property.getAddress().trim().isEmpty()) {
            LOGGER.warning("Attempted to add property with invalid address");
            throw new IllegalArgumentException("Property address cannot be empty");
        }

        if (property.getPrice() <= 0) {
            LOGGER.warning("Attempted to add property with invalid price: " + property.getPrice());
            throw new IllegalArgumentException("Property price must be positive");
        }

        try {
            lock.writeLock().lock();
            
            // Check for duplicate address
            if (properties.stream().anyMatch(p -> p.getAddress().equals(property.getAddress()))) {
                LOGGER.warning("Attempted to add property with existing address: " + property.getAddress());
                return false;
            }

            property.setId(nextId++);
            properties.add(property);
            FileHandler.saveProperties(properties);
            LOGGER.info("Successfully added property: " + property.getAddress());
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save property: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Property getPropertyById(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to get property with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid property ID");
        }

        try {
            lock.readLock().lock();
            return properties.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Property> getAllProperties() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(properties);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateProperty(Property updatedProperty) {
        if (updatedProperty == null) {
            LOGGER.warning("Attempted to update null property");
            throw new IllegalArgumentException("Updated property cannot be null");
        }

        if (updatedProperty.getId() <= 0) {
            LOGGER.warning("Attempted to update property with invalid ID: " + updatedProperty.getId());
            throw new IllegalArgumentException("Invalid property ID");
        }

        if (updatedProperty.getAddress() == null || updatedProperty.getAddress().trim().isEmpty()) {
            LOGGER.warning("Attempted to update property with invalid address");
            throw new IllegalArgumentException("Property address cannot be empty");
        }

        if (updatedProperty.getPrice() <= 0) {
            LOGGER.warning("Attempted to update property with invalid price: " + updatedProperty.getPrice());
            throw new IllegalArgumentException("Property price must be positive");
        }

        try {
            lock.writeLock().lock();
            
            int index = -1;
            for (int i = 0; i < properties.size(); i++) {
                if (properties.get(i).getId() == updatedProperty.getId()) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                LOGGER.warning("Attempted to update non-existent property: " + updatedProperty.getId());
                return false;
            }

            // Check if address is being changed and if it conflicts
            if (!properties.get(index).getAddress().equals(updatedProperty.getAddress()) &&
                properties.stream().anyMatch(p -> p.getId() != updatedProperty.getId() && 
                                               p.getAddress().equals(updatedProperty.getAddress()))) {
                LOGGER.warning("Attempted to update property with conflicting address: " + updatedProperty.getAddress());
                return false;
            }

            properties.set(index, updatedProperty);
            FileHandler.saveProperties(properties);
            LOGGER.info("Successfully updated property: " + updatedProperty.getId());
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save updated property: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteProperty(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to delete property with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid property ID");
        }

        try {
            lock.writeLock().lock();
            boolean removed = properties.removeIf(property -> property.getId() == id);
            
            if (removed) {
                FileHandler.saveProperties(properties);
                LOGGER.info("Successfully deleted property: " + id);
                return true;
            } else {
                LOGGER.warning("Attempted to delete non-existent property: " + id);
                return false;
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to save after property deletion: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}