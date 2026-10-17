package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.User;
import com.grownet.repository.UserRepository;
import com.grownet.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) { this.users = users; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService; }

    @Transactional public Dtos.SafeUser register(Dtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        User user = new User();
        user.setFullName(request.fullName().trim()); user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(User.Role.STUDENT);
        return Dtos.SafeUser.from(users.save(user));
    }

    @Transactional(readOnly = true) public Dtos.AuthResponse login(Dtos.LoginRequest request) {
        User user = users.findByEmailIgnoreCase(request.email().trim()).orElseThrow(() -> new BadCredentialsException("Invalid email or password."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) throw new BadCredentialsException("Invalid email or password.");
        return new Dtos.AuthResponse(jwtService.generateToken(user), "Bearer", Dtos.SafeUser.from(user));
    }
}
