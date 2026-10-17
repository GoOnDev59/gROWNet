package com.grownet.controller;

import com.grownet.MatchingService;
import com.grownet.entity.Internship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService = new MatchingService();
    private final InternshipRepository internships;
    private final UserRepository users;

    public MatchingController(
            InternshipRepository internships,
            UserRepository users) {
        this.internships = internships;
        this.users = users;
    }

    public record MatchingResponse(
            Long internshipId,
            int matchPercentage,
            List<String> matchedSkills,
            List<String> missingSkills,
            String explanation) {
    }

    @GetMapping("/{internshipId}")
    public MatchingResponse getMatch(
            Authentication authentication,
            @PathVariable Long internshipId) {

        User student = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));

        if (student.getRole() != User.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can access skill matching.");
        }

        Internship internship = internships.findById(internshipId)
                .filter(Internship::isVerified)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Internship not found."));

        List<String> studentSkills = splitSkills(student.getSkills());
        List<String> requiredSkills =
                splitSkills(internship.getSkillsRequired());

        MatchingService.MatchResult result =
                matchingService.calculate(studentSkills, requiredSkills);

        return new MatchingResponse(
                internship.getId(),
                result.matchPercentage(),
                result.matchedSkills(),
                result.missingSkills(),
                result.explanation());
    }

    private List<String> splitSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return List.of();
        }

        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .toList();
    }
}
