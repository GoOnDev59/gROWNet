
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.entity.SavedInternship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.SavedInternshipRepository;
import com.grownet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SavedInternshipServiceTest {

    private SavedInternshipRepository savedInternships;
    private InternshipRepository internships;
    private UserRepository users;
    private SavedInternshipService service;

    private User student;

    @BeforeEach
    void setUp() {
        savedInternships = mock(SavedInternshipRepository.class);
        internships = mock(InternshipRepository.class);
        users = mock(UserRepository.class);

        service = new SavedInternshipService(
                savedInternships,
                internships,
                users
        );

        student = mock(User.class);
        when(student.getId()).thenReturn(1L);
        when(student.getRole()).thenReturn(User.Role.STUDENT);

        when(users.findByEmailIgnoreCase("student@example.com"))
                .thenReturn(Optional.of(student));
    }

    private Internship internship(Long id, boolean verified) {
        Internship item = mock(Internship.class);
        User recruiter = mock(User.class);

        when(item.getId()).thenReturn(id);
        when(item.isVerified()).thenReturn(verified);
        when(item.getRecruiter()).thenReturn(recruiter);
        when(recruiter.getId()).thenReturn(100L + id);

        return item;
    }

    private SavedInternship savedItem(Internship internship) {
        SavedInternship saved = mock(SavedInternship.class);
        when(saved.getInternship()).thenReturn(internship);
        return saved;
    }

    @Test
    void savesVerifiedInternship() {
        Internship item = internship(10L, true);

        when(internships.findById(10L))
                .thenReturn(Optional.of(item));

        when(savedInternships.existsByUserIdAndInternshipId(1L, 10L))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> service.save("student@example.com", 10L)
        );

        verify(savedInternships).save(any(SavedInternship.class));
    }

    @Test
    void doesNotSaveDuplicateInternship() {
        Internship item = internship(10L, true);

        when(internships.findById(10L))
                .thenReturn(Optional.of(item));

        when(savedInternships.existsByUserIdAndInternshipId(1L, 10L))
                .thenReturn(true);

        service.save("student@example.com", 10L);

        verify(savedInternships, never())
                .save(any(SavedInternship.class));
    }

    @Test
    void rejectsSavingMissingInternship() {
        when(internships.findById(10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.save("student@example.com", 10L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(savedInternships, never())
                .save(any(SavedInternship.class));
    }

    @Test
    void rejectsSavingUnverifiedInternship() {
        Internship item = internship(10L, false);

        when(internships.findById(10L))
                .thenReturn(Optional.of(item));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.save("student@example.com", 10L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(savedInternships, never())
                .save(any(SavedInternship.class));
    }

    @Test
    void rejectsUnknownUser() {
        when(users.findByEmailIgnoreCase("unknown@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.save("unknown@example.com", 10L)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void rejectsRecruiterManagingSavedInternships() {
        User recruiter = mock(User.class);

        when(recruiter.getRole()).thenReturn(User.Role.RECRUITER);

        when(users.findByEmailIgnoreCase("recruiter@example.com"))
                .thenReturn(Optional.of(recruiter));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.save("recruiter@example.com", 10L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void unsavesPreviouslySavedInternship() {
        Internship item = internship(10L, true);
        SavedInternship saved = savedItem(item);

        when(savedInternships.findByUserId(1L))
                .thenReturn(List.of(saved));

        assertDoesNotThrow(
                () -> service.unsave("student@example.com", 10L)
        );

        verify(savedInternships).delete(saved);
    }

    @Test
    void returnsNotFoundWhenUnsaveTargetIsNotSaved() {
        Internship item = internship(20L, true);
        SavedInternship saved = savedItem(item);

        when(savedInternships.findByUserId(1L))
                .thenReturn(List.of(saved));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.unsave("student@example.com", 10L)
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(savedInternships, never())
                .delete(any(SavedInternship.class));
    }

    @Test
    void listsOnlyVerifiedSavedInternships() {
        Internship verified = internship(10L, true);
        Internship unverified = internship(20L, false);

        // Create the saved-item mocks before stubbing the repository.
        SavedInternship verifiedSaved = savedItem(verified);
        SavedInternship unverifiedSaved = savedItem(unverified);

        when(savedInternships.findByUserId(1L))
                .thenReturn(List.of(verifiedSaved, unverifiedSaved));

        List<Dtos.InternshipResponse> result =
                service.listSaved("student@example.com");

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
    }

    @Test
    void returnsEmptyListWhenNoInternshipsAreSaved() {
        when(savedInternships.findByUserId(1L))
                .thenReturn(List.of());

        List<Dtos.InternshipResponse> result =
                service.listSaved("student@example.com");

        assertTrue(result.isEmpty());
    }
}
