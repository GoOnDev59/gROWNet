
package com.grownet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "internship_applications",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_student_internship_application",
        columnNames = {"student_id", "internship_id"}
    )
)
public class InternshipApplication {

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    public InternshipApplication() {}

    @PrePersist
    protected void onCreate() {
        if (appliedAt == null) appliedAt = LocalDateTime.now();
        if (status == null) status = Status.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Internship getInternship() { return internship; }
    public void setInternship(Internship internship) { this.internship = internship; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
