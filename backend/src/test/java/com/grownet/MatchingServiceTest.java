package com.grownet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class MatchingServiceTest {

    private final MatchingService service = new MatchingService();

    @Test
    void calculatesPercentageAndMissingSkills() {
        var result = service.calculate(
                List.of("Java", "SQL", "HTML"),
                List.of("Java", "SQL", "HTML", "Spring Boot")
        );

        assertEquals(75, result.matchPercentage());
        assertEquals(List.of("Java", "SQL", "HTML"), result.matchedSkills());
        assertEquals(List.of("Spring Boot"), result.missingSkills());
    }

    @Test
    void ignoresCaseAndExtraSpaces() {
        var result = service.calculate(
                List.of(" java ", "SPRING   BOOT"),
                List.of("Java", "Spring Boot")
        );

        assertEquals(100, result.matchPercentage());
        assertEquals(2, result.matchedSkills().size());
    }

    @Test
    void ignoresDuplicateRequiredSkills() {
        var result = service.calculate(
                List.of("Java"),
                List.of("Java", "java", " Java ")
        );

        assertEquals(100, result.matchPercentage());
        assertEquals(1, result.matchedSkills().size());
    }

    @Test
    void handlesNoRequiredSkillsSafely() {
        var result = service.calculate(
                List.of("Java"),
                List.of()
        );

        assertEquals(0, result.matchPercentage());
        assertEquals(List.of(), result.matchedSkills());
        assertEquals(List.of(), result.missingSkills());
    }

    @Test
    void handlesNullLists() {
        var result = service.calculate(null, null);

        assertEquals(0, result.matchPercentage());
        assertEquals(List.of(), result.matchedSkills());
        assertEquals(List.of(), result.missingSkills());
    }
}