
package com.grownet.controller;

import com.grownet.dto.Dtos;
import com.grownet.service.InternshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @GetMapping
    public List<Dtos.InternshipResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skill) {
        return internshipService.list(keyword, location, skill);
    }

    @GetMapping("/{id}")
    public Dtos.InternshipResponse get(@PathVariable Long id) {
        return internshipService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dtos.InternshipResponse create(
            Authentication authentication,
            @Valid @RequestBody Dtos.InternshipRequest request) {
        return internshipService.create(authentication.getName(), request);
    }
}
