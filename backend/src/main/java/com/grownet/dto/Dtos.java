package com.grownet.dto;

import com.grownet.DeadlineService;
import com.grownet.entity.Internship;
import com.grownet.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Dtos {

    private Dtos() {}

    public record RegisterRequest(
            @NotBlank @Size(max = 100) String fullName,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 8, max = 72) String password) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

    public record SafeUser(
            Long id,
            String fullName,
            String email,
            User.Role role,
            String skills,
            String college,
            String branch,
            String bio,
            String mobileNumber) {

        public static SafeUser from(User u) {
            return new SafeUser(
                    u.getId(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getRole(),
                    u.getSkills(),
                    u.getCollege(),
                    u.getBranch(),
                    u.getBio(),
                    u.getMobileNumber()
            );
        }
    }

    public record AuthResponse(
            String token,
            String tokenType,
            SafeUser user) {}

    public record MessageResponse(String message) {}

    public record ForgotPasswordRequest(
            @NotBlank @Size(max = 20) String mobileNumber) {}

    public record VerifyOtpRequest(
            @NotBlank @Size(max = 20) String mobileNumber,
            @NotBlank @Size(min = 6, max = 6) String otp) {}

    public record ResetPasswordRequest(
            @NotBlank @Size(max = 20) String mobileNumber,
            @NotBlank @Size(min = 6, max = 6) String otp,
            @NotBlank @Size(min = 8, max = 72) String newPassword) {}

    public record ProfileUpdateRequest(
            @Size(max = 100) String fullName,
            @Size(max = 1500) String skills,
            @Size(max = 150) String college,
            @Size(max = 150) String branch,
            @Size(max = 2000) String bio,
            @Size(max = 20) String mobileNumber) {}

    public record InternshipRequest(
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 180) String company,
            @Size(max = 180) String location,
            @Size(max = 100) String stipend,
            @Size(max = 5000) String description,
            @Size(max = 1500) String skillsRequired,
            LocalDate deadline) {}

    public record InternshipResponse(
            Long id,
            String title,
            String company,
            String location,
            String stipend,
            String description,
            String skillsRequired,
            LocalDate deadline,
            Long recruiterId,
            boolean verified,
            String deadlineStatus) {

        public static InternshipResponse from(Internship i) {
            String status =
                    new DeadlineService().categorize(i.getDeadline()).name();

            return new InternshipResponse(
                    i.getId(),
                    i.getTitle(),
                    i.getCompany(),
                    i.getLocation(),
                    i.getStipend(),
                    i.getDescription(),
                    i.getSkillsRequired(),
                    i.getDeadline(),
                    i.getRecruiter().getId(),
                    i.isVerified(),
                    status
            );
        }
    }

    public record InternshipApplicationResponse(
            Long id,
            Long internshipId,
            String internshipTitle,
            String company,
            String studentName,
            String studentEmail,
            String status,
            LocalDateTime appliedAt) {}
}
