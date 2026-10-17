package com.grownet;

import java.util.List;

public class ReadinessService {

    public record ReadinessResult(
            int score,
            int completedItems,
            int totalItems,
            String explanation) {
    }

    public ReadinessResult calculate(List<Boolean> checklist) {
        if (checklist == null || checklist.isEmpty()) {
            return new ReadinessResult(
                    0, 0, 0,
                    "No checklist items are available yet."
            );
        }

        int total = 0;
        int completed = 0;

        for (Boolean item : checklist) {
            if (item != null) {
                total++;
                if (item) {
                    completed++;
                }
            }
        }

        if (total == 0) {
            return new ReadinessResult(
                    0, 0, 0,
                    "No applicable checklist items are available."
            );
        }

        int score = (int) Math.round(completed * 100.0 / total);

        return new ReadinessResult(
                score,
                completed,
                total,
                "Completed " + completed + " of " + total
                        + " applicable checklist items."
        );
    }
}