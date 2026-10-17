package com.grownet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MatchingService {

    public record MatchResult(
            int matchPercentage,
            List<String> matchedSkills,
            List<String> missingSkills,
            String explanation) {
    }

    public MatchResult calculate(List<String> studentSkills,
                                 List<String> requiredSkills) {

        Map<String, String> required = new LinkedHashMap<>();

        if (requiredSkills != null) {
            for (String skill : requiredSkills) {
                String display = clean(skill);
                if (!display.isEmpty()) {
                    required.putIfAbsent(normalize(display), display);
                }
            }
        }

        Set<String> student = new LinkedHashSet<>();

        if (studentSkills != null) {
            for (String skill : studentSkills) {
                String display = clean(skill);
                if (!display.isEmpty()) {
                    student.add(normalize(display));
                }
            }
        }

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (Map.Entry<String, String> entry : required.entrySet()) {
            if (student.contains(entry.getKey())) {
                matched.add(entry.getValue());
            } else {
                missing.add(entry.getValue());
            }
        }

        if (required.isEmpty()) {
            return new MatchResult(
                    0,
                    List.of(),
                    List.of(),
                    "No required skills were provided, so a meaningful match cannot be calculated."
            );
        }

        int percentage = (int) Math.round(
                matched.size() * 100.0 / required.size()
        );

        String explanation = "Matched " + matched.size() + " of "
                + required.size() + " required skills.";

        return new MatchResult(
                percentage,
                List.copyOf(matched),
                List.copyOf(missing),
                explanation
        );
    }

    private String clean(String skill) {
        return skill == null ? "" : skill.trim().replaceAll("\\s+", " ");
    }

    private String normalize(String skill) {
        return clean(skill).toLowerCase(Locale.ROOT);
    }
}
