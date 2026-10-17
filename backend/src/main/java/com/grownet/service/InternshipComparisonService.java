
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.repository.InternshipRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class InternshipComparisonService {

    private final InternshipRepository internships;

    public InternshipComparisonService(InternshipRepository internships) {
        this.internships = internships;
    }

    @Transactional(readOnly = true)
    public List<Dtos.InternshipResponse> compare(List<Long> ids) {
        if (ids == null || ids.size() < 2 || ids.size() > 4) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Compare between 2 and 4 internships."
            );
        }

        if (ids.stream().anyMatch(id -> id == null || id <= 0)
                || new HashSet<>(ids).size() != ids.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide distinct, valid internship IDs."
            );
        }

        Map<Long, Internship> found = new HashMap<>();

        internships.findAllById(ids).forEach(internship ->
                found.put(internship.getId(), internship));

        if (found.size() != ids.size()
                || ids.stream().anyMatch(id ->
                !found.get(id).isVerified())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "One or more verified internships were not found."
            );
        }

        return ids.stream()
                .map(found::get)
                .map(Dtos.InternshipResponse::from)
                .toList();
    }
}
