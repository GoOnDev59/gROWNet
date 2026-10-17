package com.grownet.controller;

import com.grownet.dto.Dtos;
import com.grownet.service.SavedInternshipService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-internships")
public class SavedInternshipController {

    private final SavedInternshipService savedInternshipService;

    public SavedInternshipController(
            SavedInternshipService savedInternshipService) {
        this.savedInternshipService = savedInternshipService;
    }

    @PostMapping("/{internshipId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Dtos.MessageResponse save(
            Authentication authentication,
            @PathVariable Long internshipId) {

        savedInternshipService.save(
                authentication.getName(), internshipId);

        return new Dtos.MessageResponse("Internship saved successfully.");
    }

    @DeleteMapping("/{internshipId}")
    public Dtos.MessageResponse unsave(
            Authentication authentication,
            @PathVariable Long internshipId) {

        savedInternshipService.unsave(
                authentication.getName(), internshipId);

        return new Dtos.MessageResponse("Internship removed from saved list.");
    }

    @GetMapping
    public List<Dtos.InternshipResponse> listSaved(
            Authentication authentication) {

        return savedInternshipService.listSaved(authentication.getName());
    }
}
