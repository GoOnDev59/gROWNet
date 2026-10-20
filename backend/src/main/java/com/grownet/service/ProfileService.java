
package com.grownet.service;

import com.grownet.dto.Dtos;
import com.grownet.entity.User;
import com.grownet.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final UserRepository users;

    public ProfileService(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public Dtos.SafeUser get(String email) {
        return Dtos.SafeUser.from(find(email));
    }

    @Transactional
    public Dtos.SafeUser update(String email, Dtos.ProfileUpdateRequest r) {
        User u = find(email);

        if (r.fullName() != null) {
            if (r.fullName().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Name cannot be blank.");
            }
            u.setFullName(r.fullName().trim());
        }

        if (r.skills() != null) u.setSkills(r.skills());
        if (r.college() != null) u.setCollege(r.college());
        if (r.branch() != null) u.setBranch(r.branch());
        if (r.bio() != null) u.setBio(r.bio());

        if (r.mobileNumber() != null) {
            String mobile = r.mobileNumber().trim();

            if (mobile.isBlank()) {
                u.setMobileNumber(null);
            } else {
                if (users.findByMobileNumber(mobile)
                        .filter(existing -> !existing.getId().equals(u.getId()))
                        .isPresent()) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "This mobile number is already associated with another account.");
                }

                u.setMobileNumber(mobile);
            }
        }

        return Dtos.SafeUser.from(users.save(u));
    }

    private User find(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));
    }
}

