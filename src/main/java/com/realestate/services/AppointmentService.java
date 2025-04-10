package com.realestate.services;

import com.realestate.models.Appointment;
import com.realestate.utils.FileHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.time.LocalDate;

public class AppointmentService {
    private static final Logger LOGGER = Logger.getLogger(AppointmentService.class.getName());
    private final List<Appointment> appointments;
    private int nextId = 1;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public AppointmentService() {
        appointments = new ArrayList<>();
        try {
            lock.writeLock().lock();
            List<Appointment> loadedAppointments = FileHandler.loadAppointments();
            appointments.addAll(loadedAppointments);
            
            for (Appointment appointment : appointments) {
                if (appointment.getId() >= nextId) {
                    nextId = appointment.getId() + 1;
                }
            }
            LOGGER.info("Successfully initialized AppointmentService with " + appointments.size() + " appointments");
        } catch (IOException e) {
            LOGGER.severe("Failed to load appointments: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean scheduleAppointment(Appointment appointment) {
        if (appointment == null) {
            LOGGER.warning("Attempted to schedule null appointment");
            throw new IllegalArgumentException("Appointment cannot be null");
        }

        if (appointment.getAgentId() <= 0) {
            LOGGER.warning("Attempted to schedule appointment with invalid agent ID: " + appointment.getAgentId());
            throw new IllegalArgumentException("Invalid agent ID");
        }

        if (appointment.getClientId() <= 0) {
            LOGGER.warning("Attempted to schedule appointment with invalid client ID: " + appointment.getClientId());
            throw new IllegalArgumentException("Invalid client ID");
        }

        if (appointment.getPropertyId() <= 0) {
            LOGGER.warning("Attempted to schedule appointment with invalid property ID: " + appointment.getPropertyId());
            throw new IllegalArgumentException("Invalid property ID");
        }

        if (appointment.getAppointmentTime() == null) {
            LOGGER.warning("Attempted to schedule appointment with null date/time");
            throw new IllegalArgumentException("Appointment date/time cannot be null");
        }

        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            LOGGER.warning("Attempted to schedule appointment in the past: " + appointment.getAppointmentTime());
            throw new IllegalArgumentException("Appointment cannot be scheduled in the past");
        }

        try {
            lock.writeLock().lock();
            
            // Check for scheduling conflicts
            boolean hasConflict = appointments.stream()
                .anyMatch(a -> a.getAgentId() == appointment.getAgentId() &&
                             a.getAppointmentTime().equals(appointment.getAppointmentTime()) &&
                             !a.getStatus().equalsIgnoreCase("Cancelled"));
            
            if (hasConflict) {
                LOGGER.warning("Attempted to schedule conflicting appointment for agent: " + appointment.getAgentId());
                return false;
            }

            appointment.setId(nextId++);
            appointment.setStatus("Scheduled");
            appointments.add(appointment);
            FileHandler.saveAppointments(appointments);
            LOGGER.info("Successfully scheduled appointment: " + appointment.getId());
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save appointment: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Appointment getAppointmentById(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to get appointment with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid appointment ID");
        }

        try {
            lock.readLock().lock();
            return appointments.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Appointment> getAppointmentsForUser(int userId, boolean isAgent) {
        if (userId <= 0) {
            LOGGER.warning("Attempted to get appointments with invalid user ID: " + userId);
            throw new IllegalArgumentException("Invalid user ID");
        }

        try {
            lock.readLock().lock();
            return appointments.stream()
                .filter(a -> (isAgent && a.getAgentId() == userId) ||
                           (!isAgent && a.getClientId() == userId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateAppointment(Appointment updatedAppointment) {
        if (updatedAppointment == null) {
            LOGGER.warning("Attempted to update null appointment");
            throw new IllegalArgumentException("Updated appointment cannot be null");
        }

        if (updatedAppointment.getId() <= 0) {
            LOGGER.warning("Attempted to update appointment with invalid ID: " + updatedAppointment.getId());
            throw new IllegalArgumentException("Invalid appointment ID");
        }

        if (updatedAppointment.getAppointmentTime() == null) {
            LOGGER.warning("Attempted to update appointment with null date/time");
            throw new IllegalArgumentException("Appointment date/time cannot be null");
        }

        if (updatedAppointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            LOGGER.warning("Attempted to update appointment to past date/time: " + updatedAppointment.getAppointmentTime());
            throw new IllegalArgumentException("Appointment cannot be scheduled in the past");
        }

        try {
            lock.writeLock().lock();
            
            int index = -1;
            for (int i = 0; i < appointments.size(); i++) {
                if (appointments.get(i).getId() == updatedAppointment.getId()) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                LOGGER.warning("Attempted to update non-existent appointment: " + updatedAppointment.getId());
                return false;
            }

            // Check for scheduling conflicts with other appointments
            boolean hasConflict = appointments.stream()
                .anyMatch(a -> a.getId() != updatedAppointment.getId() &&
                             a.getAgentId() == updatedAppointment.getAgentId() &&
                             a.getAppointmentTime().equals(updatedAppointment.getAppointmentTime()) &&
                             !a.getStatus().equalsIgnoreCase("Cancelled"));
            
            if (hasConflict) {
                LOGGER.warning("Attempted to update to conflicting appointment time for agent: " + updatedAppointment.getAgentId());
                return false;
            }

            appointments.set(index, updatedAppointment);
            FileHandler.saveAppointments(appointments);
            LOGGER.info("Successfully updated appointment: " + updatedAppointment.getId());
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save updated appointment: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean cancelAppointment(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to cancel appointment with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid appointment ID");
        }

        try {
            lock.writeLock().lock();
            
            Appointment appointment = appointments.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);

            if (appointment == null) {
                LOGGER.warning("Attempted to cancel non-existent appointment: " + id);
                return false;
            }

            if (appointment.getStatus().equalsIgnoreCase("Cancelled")) {
                LOGGER.warning("Attempted to cancel already cancelled appointment: " + id);
                return false;
            }

            appointment.setStatus("Cancelled");
            FileHandler.saveAppointments(appointments);
            LOGGER.info("Successfully cancelled appointment: " + id);
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save after appointment cancellation: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Appointment> getAppointmentsByAgent(int agentId) {
        return appointments.stream()
                .filter(a -> a.getAgentId() == agentId)
                .toList();
    }

    public List<Appointment> filterAppointments(int agentId, String status, String date) {
        return appointments.stream()
                .filter(a -> a.getAgentId() == agentId)
                .filter(a -> status == null || status.isEmpty() || a.getStatus().equals(status))
                .filter(a -> date == null || date.isEmpty() || 
                           a.getAppointmentTime().toLocalDate().equals(LocalDate.parse(date)))
                .toList();
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        return appointments.stream()
                .filter(a -> a.getId() == appointmentId)
                .findFirst()
                .map(a -> {
                    a.setStatus(status);
                    return true;
                })
                .orElse(false);
    }
}