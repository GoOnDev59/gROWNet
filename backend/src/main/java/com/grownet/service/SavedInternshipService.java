package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.Internship;
import com.grownet.entity.SavedInternship;
import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.SavedInternshipRepository;
import com.grownet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SavedInternshipService {

    private final SavedInternshipRepository savedInternships;
    private final InternshipRepository internships;
    private final UserRepository users;

    public SavedInternshipService(
            SavedInternshipRepository savedInternships,
            InternshipRepository internships,
            UserRepository users) {
        this.savedInternships = savedInternships;
        this.internships = internships;
        this.users = users;
    }

    @Transactional
    public void save(String email, Long internshipId) {
        User user = findStudent(email);

        Internship internship = internships.findById(internshipId)
                .filter(Internship::isVerified)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Internship not found."));

        if (!savedInternships.existsByUserIdAndInternshipId(
                user.getId(), internshipId)) {
            SavedInternship saved = new SavedInternship();
            saved.setUser(user);
            saved.setInternship(internship);
            savedInternships.save(saved);
        }
    }

    @Transactional
    public void unsave(String email, Long internshipId) {
        User user = findStudent(email);

        List<SavedInternship> saved = savedInternships.findByUserId(user.getId());

        SavedInternship matching = saved.stream()
                .filter(item -> item.getInternship().getId().equals(internshipId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Saved internship not found."));

        savedInternships.delete(matching);
    }

    @Transactional(readOnly = true)
    public List<Dtos.InternshipResponse> listSaved(String email) {
        User user = findStudent(email);

        return savedInternships.findByUserId(user.getId()).stream()
                .map(SavedInternship::getInternship)
                .filter(Internship::isVerified)
                .map(Dtos.InternshipResponse::from)
                .toList();
    }

    private User findStudent(String email) {
        User user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));

        if (user.getRole() != User.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only students can manage saved internships.");
        }

        return user;
    }
}