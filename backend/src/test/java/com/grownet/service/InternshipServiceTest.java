
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternshipServiceTest {

    private InternshipRepository internships;
    private InternshipService service;

    @BeforeEach
    void setUp() {
        internships = mock(InternshipRepository.class);
        UserRepository users = mock(UserRepository.class);
        service = new InternshipService(internships, users);

        when(internships.findByVerifiedTrue()).thenReturn(List.of(
                internship(1L, "Java Developer", "TechCorp",
                        "Mumbai", "Java, Spring Boot"),
                internship(2L, "Python Intern", "DataWorks",
                        "Pune", "Python, SQL")
        ));
    }

    @Test
    void filtersByKeywordIgnoringCase() {
        List<Dtos.InternshipResponse> results =
                service.list("JAVA", null, null);

        assertEquals(1, results.size());
        assertEquals("Java Developer", results.get(0).title());
    }

    @Test
    void filtersByLocationIgnoringCase() {
        List<Dtos.InternshipResponse> results =
                service.list(null, "mumbai", null);

        assertEquals(1, results.size());
        assertEquals("TechCorp", results.get(0).company());
    }

    @Test
    void filtersBySkillIgnoringCase() {
        List<Dtos.InternshipResponse> results =
                service.list(null, null, "spring boot");

        assertEquals(1, results.size());
        assertEquals("Java Developer", results.get(0).title());
    }

    @Test
    void returnsOnlyVerifiedInternships() {
        List<Dtos.InternshipResponse> results =
                service.list(null, null, null);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(
                Dtos.InternshipResponse::verified));
    }

    private Internship internship(
            Long id, String title, String company,
            String location, String skills) {

        User recruiter = new User();
        recruiter.setId(100L);

        Internship internship = new Internship();
        internship.setId(id);
        internship.setTitle(title);
        internship.setCompany(company);
        internship.setLocation(location);
        internship.setSkillsRequired(skills);
        internship.setDescription("Test internship description");
        internship.setRecruiter(recruiter);
        internship.setVerified(true);

        return internship;
    }
}
