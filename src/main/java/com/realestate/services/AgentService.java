package com.realestate.services;

import com.realestate.models.Agent;
import com.realestate.utils.BinarySearchTree;
import com.realestate.utils.FileHandler;
import com.realestate.utils.SelectionSort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AgentService {
    private static final Logger LOGGER = Logger.getLogger(AgentService.class.getName());
    private final List<Agent> agents;
    private BinarySearchTree agentBST;
    private int nextId = 1;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public AgentService() {
        agents = new ArrayList<>();
        agentBST = new BinarySearchTree();
        try {
            lock.writeLock().lock();
            List<Agent> loadedAgents = FileHandler.loadAgents();
            agents.addAll(loadedAgents);
            
            // If no agents exist, initialize with sample data
            if (agents.isEmpty()) {
                initializeSampleAgents();
            }
            
            for (Agent agent : agents) {
                agentBST.insert(agent);
                if (agent.getId() >= nextId) {
                    nextId = agent.getId() + 1;
                }
            }
            LOGGER.info("Successfully initialized AgentService with " + agents.size() + " agents");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Failed to load agents: " + e.getMessage());
            // Initialize with sample data if loading fails
            initializeSampleAgents();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void initializeSampleAgents() {
        List<Agent> sampleAgents = Arrays.asList(
            new Agent(1, "Jane Smith", "jane@example.com", "New York", "Luxury Homes", 4.8),
            new Agent(2, "Mike Johnson", "mike@example.com", "Los Angeles", "Commercial Properties", 4.5),
            new Agent(3, "Sarah Williams", "sarah@example.com", "Chicago", "Apartments", 4.2)
        );
        
        for (Agent agent : sampleAgents) {
            addAgent(agent);
        }
        LOGGER.info("Initialized system with " + sampleAgents.size() + " sample agents");
    }

    public boolean addAgent(Agent agent) {
        if (agent == null) {
            LOGGER.warning("Attempted to add null agent");
            throw new IllegalArgumentException("Agent cannot be null");
        }

        if (agent.getName() == null || agent.getName().trim().isEmpty()) {
            LOGGER.warning("Attempted to add agent with invalid name");
            throw new IllegalArgumentException("Agent name cannot be empty");
        }

        try {
            lock.writeLock().lock();
            
            // Check for duplicate name
            if (agentBST.search(agent.getName()) != null) {
                LOGGER.warning("Attempted to add agent with existing name: " + agent.getName());
                return false;
            }

            agent.setId(nextId++);
            agents.add(agent);
            agentBST.insert(agent);
            FileHandler.saveAgents(agents);
            LOGGER.info("Successfully added agent: " + agent.getName());
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save agent: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Agent getAgentById(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to get agent with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid agent ID");
        }

        try {
            lock.readLock().lock();
            return agents.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Agent searchAgentByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            LOGGER.warning("Attempted to search for agent with invalid name");
            throw new IllegalArgumentException("Agent name cannot be empty");
        }

        try {
            lock.readLock().lock();
            Agent agent = agentBST.search(name);
            if (agent == null) {
                LOGGER.info("No agent found with name: " + name);
            }
            return agent;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Agent> getAllAgents() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(agents);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Agent> getAgentsSortedByRating() {
        try {
            lock.readLock().lock();
            Agent[] agentArray = agents.toArray(new Agent[0]);
            SelectionSort.sortByRating(agentArray);
            return List.of(agentArray);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateAgent(Agent updatedAgent) {
        if (updatedAgent == null) {
            LOGGER.warning("Attempted to update null agent");
            throw new IllegalArgumentException("Updated agent cannot be null");
        }

        if (updatedAgent.getId() <= 0) {
            LOGGER.warning("Attempted to update agent with invalid ID: " + updatedAgent.getId());
            throw new IllegalArgumentException("Invalid agent ID");
        }

        try {
            lock.writeLock().lock();
            
            int index = -1;
            for (int i = 0; i < agents.size(); i++) {
                if (agents.get(i).getId() == updatedAgent.getId()) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                LOGGER.warning("Attempted to update non-existent agent: " + updatedAgent.getId());
                return false;
            }

            // Check if name is being changed and if it conflicts
            if (!agents.get(index).getName().equals(updatedAgent.getName()) &&
                agents.stream().anyMatch(a -> a.getId() != updatedAgent.getId() && 
                                           a.getName().equals(updatedAgent.getName()))) {
                LOGGER.warning("Attempted to update agent with conflicting name: " + updatedAgent.getName());
                return false;
            }

            agents.set(index, updatedAgent);
            
            // Rebuild BST
            agentBST = new BinarySearchTree();
            for (Agent agent : agents) {
                agentBST.insert(agent);
            }
            
            FileHandler.saveAgents(agents);
            LOGGER.info("Successfully updated agent: " + updatedAgent.getId());
            return true;
        } catch (IOException e) {
            LOGGER.severe("Failed to save updated agent: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteAgent(int id) {
        if (id <= 0) {
            LOGGER.warning("Attempted to delete agent with invalid ID: " + id);
            throw new IllegalArgumentException("Invalid agent ID");
        }

        try {
            lock.writeLock().lock();
            boolean removed = agents.removeIf(agent -> agent.getId() == id);
            
            if (removed) {
                // Rebuild BST
                agentBST = new BinarySearchTree();
                for (Agent agent : agents) {
                    agentBST.insert(agent);
                }
                
                FileHandler.saveAgents(agents);
                LOGGER.info("Successfully deleted agent: " + id);
                return true;
            } else {
                LOGGER.warning("Attempted to delete non-existent agent: " + id);
                return false;
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to save after agent deletion: " + e.getMessage());
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}