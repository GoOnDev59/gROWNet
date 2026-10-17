
package com.grownet;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingServiceTest {

    private final MatchingService service = new MatchingService();

    @Test
    void calculateReturns100WhenAllSkillsMatch() {
        MatchingService.MatchResult result = service.calculate(
                List.of("Java", "Spring Boot", "MySQL"),
                List.of("Java", "Spring Boot", "MySQL")
        );

        assertEquals(100, result.matchPercentage());
        assertEquals(
                List.of("Java", "Spring Boot", "MySQL"),
                result.matchedSkills()
        );
        assertTrue(result.missingSkills().isEmpty());
        assertEquals("Matched 3 of 3 required skills.", result.explanation());
    }

    @Test
    void calculateReturnsZeroWhenNoSkillsMatch() {
        MatchingService.MatchResult result = service.calculate(
                List.of("Python", "Django"),
                List.of("Java", "Spring Boot")
        );

        assertEquals(0, result.matchPercentage());
        assertTrue(result.matchedSkills().isEmpty());
        assertEquals(
                List.of("Java", "Spring Boot"),
                result.missingSkills()
        );
    }

    @Test
    void calculateReturnsCorrectPartialMatch() {
        MatchingService.MatchResult result = service.calculate(
                List.of("Java", "MySQL"),
                List.of("Java", "Spring Boot", "MySQL", "Docker")
        );

        assertEquals(50, result.matchPercentage());
        assertEquals(List.of("Java", "MySQL"), result.matchedSkills());
        assertEquals(
                List.of("Spring Boot", "Docker"),
                result.missingSkills()
        );
    }

    @Test
    void calculateMatchesSkillsIgnoringCase() {
        MatchingService.MatchResult result = service.calculate(
                List.of("java", "SPRING BOOT"),
                List.of("Java", "Spring Boot")
        );

        assertEquals(100, result.matchPercentage());
        assertEquals(
                List.of("Java", "Spring Boot"),
                result.matchedSkills()
        );
    }

    @Test
    void calculateTrimsWhitespaceAndNormalizesSpacing() {
        MatchingService.MatchResult result = service.calculate(
                List.of("  Java   Developer  ", " Spring Boot "),
                List.of("Java Developer", "Spring Boot")
        );

        assertEquals(100, result.matchPercentage());
        assertEquals(
                List.of("Java Developer", "Spring Boot"),
                result.matchedSkills()
        );
    }

    @Test
    void calculateIgnoresDuplicateSkills() {
        MatchingService.MatchResult result = service.calculate(
                List.of("Java", "java", "Java"),
                List.of("Java", "JAVA", "Spring Boot", "Spring Boot")
        );

        assertEquals(50, result.matchPercentage());
        assertEquals(List.of("Java"), result.matchedSkills());
        assertEquals(List.of("Spring Boot"), result.missingSkills());
    }

    @Test
    void calculateHandlesNullStudentSkills() {
        MatchingService.MatchResult result = service.calculate(
                null,
                List.of("Java", "MySQL")
        );

        assertEquals(0, result.matchPercentage());
        assertTrue(result.matchedSkills().isEmpty());
        assertEquals(
                List.of("Java", "MySQL"),
                result.missingSkills()
        );
    }

    @Test
    void calculateHandlesNullRequiredSkills() {
        MatchingService.MatchResult result = service.calculate(
                List.of("Java"),
                null
        );

        assertEquals(0, result.matchPercentage());
        assertTrue(result.matchedSkills().isEmpty());
        assertTrue(result.missingSkills().isEmpty());
        assertTrue(result.explanation().contains("No required skills"));
    }

    @Test
    void calculateIgnoresNullAndBlankSkillEntries() {
        MatchingService.MatchResult result = service.calculate(
                Arrays.asList("Java", null, "  ", "MySQL"),
                Arrays.asList("Java", null, "", "Spring Boot")
        );

        assertEquals(50, result.matchPercentage());
        assertEquals(List.of("Java"), result.matchedSkills());
        assertEquals(List.of("Spring Boot"), result.missingSkills());
    }

    @Test
    void calculateHandlesEmptyRequiredSkills() {
        MatchingService.MatchResult result = service.calculate(
                List.of("Java", "MySQL"),
                List.of()
        );

        assertEquals(0, result.matchPercentage());
        assertTrue(result.matchedSkills().isEmpty());
        assertTrue(result.missingSkills().isEmpty());
        assertTrue(result.explanation().contains("No required skills"));
    }
}
