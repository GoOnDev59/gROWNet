package com.grownet.controller;

import com.grownet.ReadinessService;
import com.grownet.entity.User;
import com.grownet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/readiness")
public class ReadinessController {

    private final UserRepository users;
    private final ReadinessService readinessService = new ReadinessService();

    public ReadinessController(UserRepository users) {
        this.users = users;
    }

    public record ChecklistItem(String name, boolean completed, String suggestion) {}

    public record ReadinessResponse(
            int readinessScore,
            int completedItems,
            int totalItems,
            List<ChecklistItem> checklist,
            String explanation) {}

    @GetMapping
    public ReadinessResponse getReadiness(Authentication authentication) {
        User student = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));

        if (student.getRole() != User.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can access placement readiness.");
        }

        boolean profileComplete = hasText(student.getCollege())
                && hasText(student.getBranch())
                && hasText(student.getBio());
        boolean skillsAdded = hasText(student.getSkills());

        List<ChecklistItem> checklist = List.of(
                new ChecklistItem("Complete profile", profileComplete,
                        profileComplete ? "Profile details completed."
                                : "Add your college, branch and bio."),
                new ChecklistItem("Add skills", skillsAdded,
                        skillsAdded ? "Skills added to your profile."
                                : "Add your technical or professional skills.")
        );

        ReadinessService.ReadinessResult result =
                readinessService.calculate(
                        checklist.stream()
                                .map(ChecklistItem::completed)
                                .toList());

        return new ReadinessResponse(
                result.score(),
                result.completedItems(),
                result.totalItems(),
                checklist,
                result.explanation());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
