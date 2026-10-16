package com.grownet.controller;

import com.grownet.dto.Dtos;
import com.grownet.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public Dtos.MessageResponse register(@Valid @RequestBody Dtos.RegisterRequest request) { authService.register(request); return new Dtos.MessageResponse("Registration successful. You can now log in."); }
    @PostMapping("/login") public Dtos.AuthResponse login(@Valid @RequestBody Dtos.LoginRequest request) { return authService.login(request); }
}
                        