package com.grownet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User {
    public enum Role { STUDENT, RECRUITER, ADMIN }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank @Email @Size(max = 254)
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 1500)
    private String skills;

    @Column(length = 150)
    private String college;

    @Column(length = 150)
    private String branch;

    @Column(length = 2000)
    private String bio;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Role role = Role.STUDENT;
    @Column(nullable = false)
    private boolean approved = false;

    public User() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isApproved() { return approved; }
public void setApproved(boolean approved) { this.approved = approved; }
}
