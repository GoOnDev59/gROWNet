package com.grownet.controller;

import com.grownet.dto.Dtos;
import com.grownet.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;
    public ProfileController(ProfileService profileService) { this.profileService = profileService; }
    @GetMapping public Dtos.SafeUser getProfile(Authentication authentication) { return profileService.get(authentication.getName()); }
    @PutMapping public Dtos.SafeUser updateProfile(Authentication authentication, @Valid @RequestBody Dtos.ProfileUpdateRequest request) { return profileService.update(authentication.getName(), request); }
}
