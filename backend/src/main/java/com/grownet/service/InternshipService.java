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

@Service
public class InternshipService {
    private final InternshipRepository internships;
    private final UserRepository users;
    public InternshipService(InternshipRepository internships, UserRepository users) { this.internships = internships; this.users = users; }
    @Transactional(readOnly = true) public List<Dtos.InternshipResponse> list() { return internships.findByVerifiedTrue().stream().map(Dtos.InternshipResponse::from).toList(); }
    @Transactional(readOnly = true) public Dtos.InternshipResponse get(Long id) {
        Internship i = internships.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found."));
        if (!i.isVerified()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found.");
        return Dtos.InternshipResponse.from(i);
    }
    @Transactional public Dtos.InternshipResponse create(String email, Dtos.InternshipRequest r) {
        User recruiter = users.findByEmailIgnoreCase(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter not found."));
        if (recruiter.getRole() != User.Role.RECRUITER) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recruiters can create internships.");
        Internship i = new Internship();
        i.setTitle(r.title().trim()); i.setCompany(r.company().trim()); i.setLocation(r.location()); i.setStipend(r.stipend());
        i.setDescription(r.description()); i.setSkillsRequired(r.skillsRequired()); i.setDeadline(r.deadline()); i.setRecruiter(recruiter); i.setVerified(false);
        return Dtos.InternshipResponse.from(internships.save(i));
    }
}
