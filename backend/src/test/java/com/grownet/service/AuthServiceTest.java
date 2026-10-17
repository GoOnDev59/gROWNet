
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.User;
import com.grownet.repository.UserRepository;
import com.grownet.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository users;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Dtos.RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new Dtos.RegisterRequest(
                " Mukesh Patil ",
                " MUKESH.TEST@example.com ",
                "TestPass@123"
        );
    }

    @Test
    void registerCreatesStudentWithNormalizedEmailAndHashedPassword() {
        when(users.existsByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("TestPass@123"))
                .thenReturn("hashed-password");
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        Dtos.SafeUser result = authService.register(registerRequest);

        assertEquals("Mukesh Patil", result.fullName());
        assertEquals("mukesh.test@example.com", result.email());
        assertEquals(User.Role.STUDENT, result.role());

        verify(passwordEncoder).encode("TestPass@123");
        verify(users).save(any(User.class));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(users.existsByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(users, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Mukesh Patil");
        user.setEmail("mukesh.test@example.com");
        user.setPassword("hashed-password");
        user.setRole(User.Role.STUDENT);

        Dtos.LoginRequest request = new Dtos.LoginRequest(
                "mukesh.test@example.com",
                "TestPass@123"
        );

        when(users.findByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("TestPass@123", "hashed-password"))
                .thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("test-jwt-token");

        Dtos.AuthResponse result = authService.login(request);

        assertEquals("test-jwt-token", result.token());
        assertEquals("Bearer", result.tokenType());
        assertEquals("mukesh.test@example.com", result.user().email());
    }

    @Test
    void loginRejectsUnknownEmail() {
        Dtos.LoginRequest request = new Dtos.LoginRequest(
                "unknown@example.com",
                "TestPass@123"
        );

        when(users.findByEmailIgnoreCase("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginRejectsIncorrectPassword() {
        User user = new User();
        user.setEmail("mukesh.test@example.com");
        user.setPassword("hashed-password");

        Dtos.LoginRequest request = new Dtos.LoginRequest(
                "mukesh.test@example.com",
                "WrongPass@123"
        );

        when(users.findByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass@123", "hashed-password"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any(User.class));
    }
}
