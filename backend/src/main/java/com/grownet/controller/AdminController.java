
package com.grownet.controller;

import com.grownet.entity.User;
import com.grownet.repository.InternshipRepository;
import com.grownet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;

    public AdminController(
            UserRepository userRepository,
            InternshipRepository internshipRepository) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
    }

    // Get pending recruiters without exposing password hashes
    @GetMapping("/pending-recruiters")
    public ResponseEntity<List<Map<String, Object>>> getPendingRecruiters() {

        List<Map<String, Object>> recruiters =
                userRepository.findByRoleAndApprovedFalse(User.Role.RECRUITER)
                        .stream()
                        .map(user -> {
                            Map<String, Object> recruiter = new LinkedHashMap<>();
                            recruiter.put("id", user.getId());
                            recruiter.put("fullName", user.getFullName());
                            recruiter.put("email", user.getEmail());
                            recruiter.put("college",
                                    user.getCollege() == null ? "" : user.getCollege());
                            recruiter.put("branch",
                                    user.getBranch() == null ? "" : user.getBranch());
                            recruiter.put("role", user.getRole().name());
                            recruiter.put("approved", user.isApproved());
                            return recruiter;
                        })
                        .toList();

        return ResponseEntity.ok(recruiters);
    }

    // Approve a recruiter
    @PutMapping("/recruiters/{id}/approve")
    public ResponseEntity<?> approveRecruiter(@PathVariable Long id) {

        return userRepository.findById(id)
                .filter(user -> user.getRole() == User.Role.RECRUITER)
                .map(user -> {
                    user.setApproved(true);
                    userRepository.save(user);

                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,
                                    "message", "Recruiter approved successfully."
                            )
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Recruiter not found."
                        )));
    }

    // Reject a recruiter
    @PutMapping("/recruiters/{id}/reject")
    public ResponseEntity<?> rejectRecruiter(@PathVariable Long id) {

        return userRepository.findById(id)
                .filter(user -> user.getRole() == User.Role.RECRUITER)
                .map(user -> {
                    user.setApproved(false);
                    userRepository.save(user);

                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,
                                    "message", "Recruiter rejected."
                            )
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Recruiter not found."
                        )));
    }

    // Get pending internships, excluding rejected ones
    @GetMapping("/pending-internships")
    public ResponseEntity<List<Map<String, Object>>> getPendingInternships() {

        List<Map<String, Object>> internships =
                internshipRepository.findByVerifiedFalseAndRejectedFalse()
                        .stream()
                        .map(internship -> {
                            var recruiter = internship.getRecruiter();

                            Map<String, Object> result = new LinkedHashMap<>();
                            result.put("id", internship.getId());
                            result.put("title", internship.getTitle());
                            result.put("company", internship.getCompany());
                            result.put("location",
                                    internship.getLocation() == null
                                            ? "" : internship.getLocation());
                            result.put("stipend",
                                    internship.getStipend() == null
                                            ? "" : internship.getStipend());
                            result.put("description",
                                    internship.getDescription() == null
                                            ? "" : internship.getDescription());
                            result.put("skillsRequired",
                                    internship.getSkillsRequired() == null
                                            ? "" : internship.getSkillsRequired());
                            result.put("deadline",
                                    internship.getDeadline() == null
                                            ? "" : internship.getDeadline().toString());
                            result.put("recruiterId", recruiter.getId());
                            result.put("recruiterName", recruiter.getFullName());
                            result.put("verified", internship.isVerified());

                            return result;
                        })
                        .toList();

        return ResponseEntity.ok(internships);
    }

    // Approve an internship
    @PutMapping("/internships/{id}/approve")
    public ResponseEntity<?> approveInternship(@PathVariable Long id) {

        return internshipRepository.findById(id)
                .map(internship -> {
                    internship.setVerified(true);
                    internship.setRejected(false);
                    internshipRepository.save(internship);

                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,
                                    "message", "Internship approved successfully."
                            )
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Internship not found."
                        )));
    }

    // Reject an internship
    @PutMapping("/internships/{id}/reject")
    public ResponseEntity<?> rejectInternship(@PathVariable Long id) {

        return internshipRepository.findById(id)
                .map(internship -> {
                    internship.setVerified(false);
                    internship.setRejected(true);
                    internshipRepository.save(internship);

                    return ResponseEntity.ok(
                            Map.of(
                                    "success", true,
                                    "message", "Internship rejected successfully."
                            )
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Internship not found."
                        )));
    }
}

