package com.realestate.services;

import com.realestate.models.*;
import com.realestate.utils.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final List<User> users;
    private int nextId = 1;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public UserService() {
        users = new ArrayList<>();
        try {
            lock.writeLock().lock();
            users.addAll(FileHandler.loadUsers());
            for (User user : users) {
                if (user.getId() >= nextId) {
                    nextId = user.getId() + 1;
                }
            }
            LOGGER.info("Successfully initialized UserService with " + users.size() + " users");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Failed to load users: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean registerUser(User user) {
        if (user == null) {
            LOGGER.warning("Attempted to register null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            LOGGER.warning("Attempted to register user with invalid username");
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            LOGGER.warning("Attempted to register user with invalid password");
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            LOGGER.warning("Attempted to register user with invalid email");
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            LOGGER.warning("Attempted to register user with invalid phone");
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        try {
            lock.writeLock().lock();
            
            // Check for duplicate username
            if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
                LOGGER.warning("Attempted to register user with existing username: " + user.getUsername());
                return false;
            }

            // Check for duplicate email
            if (users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
                LOGGER.warning("Attempted to register user with existing email: " + user.getEmail());
                return false;
            }

            user.setId(nextId++);
            users.add(user);
            
            try {
                FileHandler.saveUsers(users);
                LOGGER.info("Successfully registered user: " + user.getUsername());
                return true;
            } catch (IOException e) {
                LOGGER.severe("Failed to save user to file: " + e.getMessage());
                // Rollback the changes
                users.remove(user);
                nextId--;
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            LOGGER.warning("Attempted authentication with invalid credentials");
            return null;
        }

        try {
            lock.readLock().lock();
            User user = users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

            if (user != null) {
                LOGGER.info("Successful authentication for user: " + username);
            } else {
                LOGGER.warning("Failed authentication attempt for user: " + username);
            }
            return user;
        } finally {
            lock.readLock().unlock();
        }
    }

    public User getUserById(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to get user with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid user ID");
        }

        try {
            lock.readLock().lock();
            return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateUser(User updatedUser) {
        if (updatedUser == null) {
            LOGGER.warning("Attempted to update null user");
            throw new IllegalArgumentException("Updated user cannot be null");
        }

        if (updatedUser.getId() <= 0) {
            LOGGER.warning("Attempted to update user with invalid ID: " + updatedUser.getId());
            throw new IllegalArgumentException("Invalid user ID");
        }

        try {
            lock.writeLock().lock();
            
            int index = -1;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == updatedUser.getId()) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                LOGGER.warning("Attempted to update non-existent user: " + updatedUser.getId());
                return false;
            }

            // Check if username is being changed and if it conflicts
            if (!users.get(index).getUsername().equals(updatedUser.getUsername()) &&
                users.stream().anyMatch(u -> u.getId() != updatedUser.getId() && 
                                           u.getUsername().equals(updatedUser.getUsername()))) {
                LOGGER.warning("Attempted to update user with conflicting username: " + updatedUser.getUsername());
                return false;
            }

            users.set(index, updatedUser);
            FileHandler.saveUsers(users);
            LOGGER.info("Successfully updated user: " + updatedUser.getId());
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save updated user: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteUser(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to delete user with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid user ID");
        }

        try {
            lock.writeLock().lock();
            boolean removed = users.removeIf(user -> user.getId() == id);
            
            if (removed) {
                FileHandler.saveUsers(users);
                LOGGER.info("Successfully deleted user: " + id);
                return true;
            } else {
                LOGGER.warning("Attempted to delete non-existent user: " + id);
                return false;
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to save after user deletion: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns a list of all users in the system
     * @return List of all User objects
     */
    public List<User> listAllUsers() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(users); // Return a copy to prevent external modifications
        } finally {
            lock.readLock().unlock();
        }
    }
}