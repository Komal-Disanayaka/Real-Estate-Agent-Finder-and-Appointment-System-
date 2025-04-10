package com.realestate.utils;

import com.realestate.models.Agent;

public class BinarySearchTree {
    private Node root;

    private class Node {
        Agent agent;
        Node left, right;

        public Node(Agent agent) {
            this.agent = agent;
            left = right = null;
        }
    }

    public BinarySearchTree() {
        root = null;
    }

    public void insert(Agent agent) {
        root = insertRec(root, agent);
    }

    private Node insertRec(Node root, Agent agent) {
        if (root == null) {
            root = new Node(agent);
            return root;
        }

        if (agent.getName().compareTo(root.agent.getName()) < 0) {
            root.left = insertRec(root.left, agent);
        } else if (agent.getName().compareTo(root.agent.getName()) > 0) {
            root.right = insertRec(root.right, agent);
        }

        return root;
    }

    public Agent search(String name) {
        return searchRec(root, name);
    }

    private Agent searchRec(Node root, String name) {
        if (root == null || root.agent.getName().equals(name)) {
            return root != null ? root.agent : null;
        }

        if (name.compareTo(root.agent.getName()) < 0) {
            return searchRec(root.left, name);
        }

        return searchRec(root.right, name);
    }

    public void inOrder() {
        inOrderRec(root);
    }

    private void inOrderRec(Node root) {
        if (root != null) {
            inOrderRec(root.left);
            System.out.println(root.agent);
            inOrderRec(root.right);
        }
    }
}