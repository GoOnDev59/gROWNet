
package com.grownet.controller;

import com.grownet.entity.Internship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingControllerTest {

    @Mock
    private InternshipRepository internships;

    @Mock
    private UserRepository users;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MatchingController controller;

    @Test
    void returnsCorrectSkillMatchForStudent() {
        User student = new User();
        student.setId(1L);
        student.setEmail("student@test.com");
        student.setSkills("Java, SQL, Spring Boot");
        student.setRole(User.Role.STUDENT);

        Internship internship = new Internship();
        internship.setId(10L);
        internship.setSkillsRequired("Java, SQL, Python");
        internship.setVerified(true);

        when(authentication.getName()).thenReturn("student@test.com");
        when(users.findByEmailIgnoreCase("student@test.com"))
                .thenReturn(Optional.of(student));
        when(internships.findById(10L))
                .thenReturn(Optional.of(internship));

        MatchingController.MatchingResponse response =
                controller.getMatch(authentication, 10L);

        assertEquals(10L, response.internshipId());
        assertEquals(67, response.matchPercentage());
        assertEquals(2, response.matchedSkills().size());
        assertTrue(response.missingSkills().contains("Python"));
        assertTrue(response.explanation().contains("2 of 3"));
    }

    @Test
    void rejectsUnverifiedInternship() {
        User student = new User();
        student.setEmail("student@test.com");
        student.setRole(User.Role.STUDENT);

        Internship internship = new Internship();
        internship.setId(10L);
        internship.setVerified(false);

        when(authentication.getName()).thenReturn("student@test.com");
        when(users.findByEmailIgnoreCase("student@test.com"))
                .thenReturn(Optional.of(student));
        when(internships.findById(10L))
                .thenReturn(Optional.of(internship));

        assertThrows(ResponseStatusException.class,
                () -> controller.getMatch(authentication, 10L));
    }

    @Test
    void rejectsNonStudentUser() {
        User recruiter = new User();
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRole(User.Role.RECRUITER);

        when(authentication.getName()).thenReturn("recruiter@test.com");
        when(users.findByEmailIgnoreCase("recruiter@test.com"))
                .thenReturn(Optional.of(recruiter));

        assertThrows(ResponseStatusException.class,
                () -> controller.getMatch(authentication, 10L));

        verifyNoInteractions(internships);
    }
}
