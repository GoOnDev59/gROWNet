package com.grownet.controller;

import com.grownet.dto.Dtos;
import com.grownet.service.InternshipApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class InternshipApplicationController {

    private final InternshipApplicationService applicationService;

    public InternshipApplicationController(
            InternshipApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/{internshipId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Dtos.InternshipApplicationResponse apply(
            Authentication authentication,
            @PathVariable Long internshipId) {
        return applicationService.apply(
                authentication.getName(), internshipId);
    }

    @GetMapping("/my")
    public List<Dtos.InternshipApplicationResponse> myApplications(
            Authentication authentication) {
        return applicationService.myApplications(authentication.getName());
    }
}
