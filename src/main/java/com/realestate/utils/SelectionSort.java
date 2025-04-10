package com.realestate.utils;

import com.realestate.models.Agent;

public class SelectionSort {
    public static void sortByRating(Agent[] agents) {
        int n = agents.length;

        for (int i = 0; i < n-1; i++) {
            int max_idx = i;
            for (int j = i+1; j < n; j++) {
                if (agents[j].getRating() > agents[max_idx].getRating()) {
                    max_idx = j;
                }
            }

            // Swap the found maximum element with the first element
            Agent temp = agents[max_idx];
            agents[max_idx] = agents[i];
            agents[i] = temp;
        }
    }
}