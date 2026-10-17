
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @Mock
    private InternshipRepository internships;

    @Mock
    private UserRepository users;

    @InjectMocks
    private InternshipService internshipService;

    private Internship internship;

    @BeforeEach
    void setUp() {
        User recruiter = new User();
        recruiter.setId(2L);
        recruiter.setFullName("Test Recruiter");
        recruiter.setEmail("recruiter@example.com");
        recruiter.setRole(User.Role.RECRUITER);

        internship = new Internship();
        internship.setId(1L);
        internship.setTitle("Java Developer Intern");
        internship.setCompany("GrowTech");
        internship.setLocation("Mumbai");
        internship.setStipend("15000");
        internship.setDescription("Build Java backend applications");
        internship.setSkillsRequired("Java, Spring Boot");
        internship.setVerified(true);
        internship.setRecruiter(recruiter);
    }

    @Test
    void listReturnsVerifiedInternships() {
        when(internships.findByVerifiedTrue())
                .thenReturn(List.of(internship));

        List<Dtos.InternshipResponse> result =
                internshipService.list();

        assertEquals(1, result.size());
        assertEquals("Java Developer Intern", result.get(0).title());
        assertEquals("GrowTech", result.get(0).company());
    }

    @Test
    void listFiltersByKeywordIgnoringCase() {
        when(internships.findByVerifiedTrue())
                .thenReturn(List.of(internship));

        List<Dtos.InternshipResponse> result =
                internshipService.list("JAVA", null, null);

        assertEquals(1, result.size());
        assertEquals("Java Developer Intern", result.get(0).title());

        List<Dtos.InternshipResponse> noMatches =
                internshipService.list("Designer", null, null);

        assertTrue(noMatches.isEmpty());
    }

    @Test
    void listFiltersByLocationAndSkill() {
        when(internships.findByVerifiedTrue())
                .thenReturn(List.of(internship));

        List<Dtos.InternshipResponse> byLocation =
                internshipService.list(null, "mumbai", null);

        assertEquals(1, byLocation.size());

        List<Dtos.InternshipResponse> bySkill =
                internshipService.list(null, null, "spring boot");

        assertEquals(1, bySkill.size());

        List<Dtos.InternshipResponse> noMatches =
                internshipService.list(null, "Delhi", null);

        assertTrue(noMatches.isEmpty());
    }

    @Test
    void getReturnsVerifiedInternship() {
        when(internships.findById(1L))
                .thenReturn(Optional.of(internship));

        Dtos.InternshipResponse result =
                internshipService.get(1L);

        assertEquals("Java Developer Intern", result.title());
        assertEquals("GrowTech", result.company());
    }

    @Test
    void getRejectsMissingInternship() {
        when(internships.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> internshipService.get(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getHidesUnverifiedInternship() {
        internship.setVerified(false);

        when(internships.findById(1L))
                .thenReturn(Optional.of(internship));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> internshipService.get(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createRejectsStudent() {
        User student = new User();
        student.setEmail("mukesh.test@example.com");
        student.setRole(User.Role.STUDENT);

        when(users.findByEmailIgnoreCase("mukesh.test@example.com"))
                .thenReturn(Optional.of(student));

        Dtos.InternshipRequest request =
                new Dtos.InternshipRequest(
                        "Java Intern",
                        "GrowTech",
                        "Mumbai",
                        "15000",
                        "Backend development",
                        "Java",
                        null
                );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> internshipService.create(
                        "mukesh.test@example.com", request
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(internships, never()).save(any(Internship.class));
    }

    @Test
    void createRejectsUnknownRecruiter() {
        when(users.findByEmailIgnoreCase("unknown@example.com"))
                .thenReturn(Optional.empty());

        Dtos.InternshipRequest request =
                new Dtos.InternshipRequest(
                        "Java Intern",
                        "GrowTech",
                        "Mumbai",
                        "15000",
                        "Backend development",
                        "Java",
                        null
                );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> internshipService.create(
                        "unknown@example.com", request
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(internships, never()).save(any(Internship.class));
    }
}
