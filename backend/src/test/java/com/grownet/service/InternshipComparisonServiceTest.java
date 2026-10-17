
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternshipComparisonServiceTest {

    private InternshipRepository internships;
    private InternshipComparisonService service;

    @BeforeEach
    void setUp() {
        internships = mock(InternshipRepository.class);
        service = new InternshipComparisonService(internships);
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

    @Test
    void comparesTwoVerifiedInternshipsInRequestedOrder() {
        Internship first = internship(1L, true);
        Internship second = internship(2L, true);

        when(internships.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(second, first));

        List<Dtos.InternshipResponse> result =
                service.compare(List.of(1L, 2L));

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        verify(internships).findAllById(List.of(1L, 2L));
    }

    @Test
    void comparesFourVerifiedInternships() {
        List<Long> ids = List.of(1L, 2L, 3L, 4L);

        Internship first = internship(1L, true);
        Internship second = internship(2L, true);
        Internship third = internship(3L, true);
        Internship fourth = internship(4L, true);

        when(internships.findAllById(ids))
                .thenReturn(List.of(first, second, third, fourth));

        List<Dtos.InternshipResponse> result = service.compare(ids);

        assertEquals(4, result.size());
    }

    @Test
    void rejectsNullIds() {
        assertBadRequest(null);
    }

    @Test
    void rejectsFewerThanTwoIds() {
        assertBadRequest(List.of(1L));
    }

    @Test
    void rejectsMoreThanFourIds() {
        assertBadRequest(List.of(1L, 2L, 3L, 4L, 5L));
    }

    @Test
    void rejectsDuplicateIds() {
        assertBadRequest(List.of(1L, 1L));
    }

    @Test
    void rejectsZeroOrNegativeIds() {
        assertBadRequest(List.of(0L, 2L));
        assertBadRequest(List.of(-1L, 2L));
    }

    @Test
    void rejectsNullIdWithinList() {
        assertBadRequest(Arrays.asList(1L, null));
    }

    @Test
    void returnsNotFoundWhenAnInternshipIsMissing() {
        Internship first = internship(1L, true);

        when(internships.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(first));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.compare(List.of(1L, 2L))
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void returnsNotFoundWhenAnInternshipIsUnverified() {
        Internship first = internship(1L, true);
        Internship second = internship(2L, false);

        when(internships.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(first, second));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.compare(List.of(1L, 2L))
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    private void assertBadRequest(List<Long> ids) {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.compare(ids)
        );

        assertEquals(400, exception.getStatusCode().value());
    }
}
