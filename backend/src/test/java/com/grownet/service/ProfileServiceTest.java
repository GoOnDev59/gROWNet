
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.User;
import com.grownet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository users;

    @InjectMocks
    private ProfileService profileService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Mukesh Patil");
        user.setEmail("mukesh.test@example.com");
        user.setRole(User.Role.STUDENT);
        user.setSkills("Java");
        user.setCollege("Test College");
        user.setBranch("Computer Science");
        user.setBio("Aspiring developer");
    }

    @Test
    void getReturnsExistingUserProfile() {
        when(users.findByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(Optional.of(user));

        Dtos.SafeUser result =
                profileService.get("mukesh.test@example.com");

        assertEquals("Mukesh Patil", result.fullName());
        assertEquals("mukesh.test@example.com", result.email());
        assertEquals("Java", result.skills());
        assertEquals("Test College", result.college());
    }

    @Test
    void getReturnsNotFoundForUnknownUser() {
        when(users.findByEmailIgnoreCase("unknown@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileService.get("unknown@example.com")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateChangesOnlyProvidedFields() {
        when(users.findByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(Optional.of(user));
        when(users.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        Dtos.ProfileUpdateRequest request =
                new Dtos.ProfileUpdateRequest(
                        "  Mukesh Updated  ",
                        "Java, Spring Boot, MySQL",
                        null,
                        null,
                        "Backend developer",
                        null
                );

        Dtos.SafeUser result = profileService.update(
                "mukesh.test@example.com", request
        );

        assertEquals("Mukesh Updated", result.fullName());
        assertEquals("Java, Spring Boot, MySQL", result.skills());
        assertEquals("Test College", result.college());
        assertEquals("Computer Science", result.branch());
        assertEquals("Backend developer", result.bio());

        verify(users).save(user);
    }

    @Test
    void updateRejectsBlankName() {
        when(users.findByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(Optional.of(user));

        Dtos.ProfileUpdateRequest request =
                new Dtos.ProfileUpdateRequest(
                        "   ", null, null, null, null, null
                );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileService.update(
                        "mukesh.test@example.com", request
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(users, never()).save(any(User.class));
    }

    @Test
    void updateReturnsNotFoundForUnknownUser() {
        when(users.findByEmailIgnoreCase("unknown@example.com"))
                .thenReturn(Optional.empty());

        Dtos.ProfileUpdateRequest request =
                new Dtos.ProfileUpdateRequest(
                        "New Name", null, null, null, null, null
                );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileService.update(
                        "unknown@example.com", request
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(users, never()).save(any(User.class));
    }
}
