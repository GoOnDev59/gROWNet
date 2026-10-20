package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.entity.InternshipApplication;
import com.grownet.entity.User;
import com.grownet.repository.InternshipApplicationRepository;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InternshipApplicationService {

    private final InternshipApplicationRepository applications;
    private final InternshipRepository internships;
    private final UserRepository users;

    public InternshipApplicationService(
            InternshipApplicationRepository applications,
            InternshipRepository internships,
            UserRepository users) {
        this.applications = applications;
        this.internships = internships;
        this.users = users;
    }

    @Transactional
    public Dtos.InternshipApplicationResponse apply(String email, Long internshipId) {
        User student = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));

        if (student.getRole() != User.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only students can apply for internships.");
        }

        Internship internship = internships.findById(internshipId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Internship not found."));

        if (!internship.isVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Internship not found.");
        }

        if (applications.existsByStudentIdAndInternshipId(
                student.getId(), internshipId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You have already applied for this internship.");
        }

        InternshipApplication application = new InternshipApplication();
        application.setStudent(student);
        application.setInternship(internship);

        return toResponse(applications.save(application));
    }

    @Transactional(readOnly = true)
    public List<Dtos.InternshipApplicationResponse> myApplications(String email) {
        User student = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));

        if (student.getRole() != User.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only students can view their applications.");
        }

        return applications.findByStudentIdOrderByAppliedAtDesc(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Dtos.InternshipApplicationResponse toResponse(
            InternshipApplication application) {
        return new Dtos.InternshipApplicationResponse(
                application.getId(),
                application.getInternship().getId(),
                application.getInternship().getTitle(),
                application.getInternship().getCompany(),
                application.getStudent().getFullName(),
                application.getStudent().getEmail(),
                application.getStatus().name(),
                application.getAppliedAt()
        );
    }
}
