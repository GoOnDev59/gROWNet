
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

@Service
public class InternshipService {
    private final InternshipRepository internships;
    private final UserRepository users;

    public InternshipService(InternshipRepository internships, UserRepository users) {
        this.internships = internships;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<Dtos.InternshipResponse> list() {
        return list(null, null, null);
    }

    @Transactional(readOnly = true)
    public List<Dtos.InternshipResponse> list(String keyword, String location, String skill) {
        Stream<Internship> results = internships.findByVerifiedTrue().stream();

        if (keyword != null && !keyword.isBlank()) {
            String search = keyword.trim().toLowerCase();
            results = results.filter(i ->
                    contains(i.getTitle(), search) ||
                    contains(i.getCompany(), search) ||
                    contains(i.getDescription(), search));
        }

        if (location != null && !location.isBlank()) {
            String searchLocation = location.trim().toLowerCase();
            results = results.filter(i ->
                    contains(i.getLocation(), searchLocation));
        }

        if (skill != null && !skill.isBlank()) {
            String searchSkill = skill.trim().toLowerCase();
            results = results.filter(i ->
                    contains(i.getSkillsRequired(), searchSkill));
        }

        return results.map(Dtos.InternshipResponse::from).toList();
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }

    @Transactional(readOnly = true)
    public Dtos.InternshipResponse get(Long id) {
        Internship i = internships.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Internship not found."));
        if (!i.isVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Internship not found.");
        }
        return Dtos.InternshipResponse.from(i);
    }

    @Transactional
    public Dtos.InternshipResponse create(String email, Dtos.InternshipRequest r) {
        User recruiter = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Recruiter not found."));
        if (recruiter.getRole() != User.Role.RECRUITER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only recruiters can create internships.");
        }

        Internship i = new Internship();
        i.setTitle(r.title().trim());
        i.setCompany(r.company().trim());
        i.setLocation(r.location());
        i.setStipend(r.stipend());
        i.setDescription(r.description());
        i.setSkillsRequired(r.skillsRequired());
        i.setDeadline(r.deadline());
        i.setRecruiter(recruiter);
        i.setVerified(false);

        return Dtos.InternshipResponse.from(internships.save(i));
    }
}