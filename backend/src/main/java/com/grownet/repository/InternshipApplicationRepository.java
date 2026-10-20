package com.grownet.repository;

import com.grownet.entity.InternshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipApplicationRepository
        extends JpaRepository<InternshipApplication, Long> {

    boolean existsByStudentIdAndInternshipId(Long studentId, Long internshipId);

    List<InternshipApplication> findByStudentIdOrderByAppliedAtDesc(Long studentId);

    List<InternshipApplication> findByInternship_Recruiter_IdOrderByAppliedAtDesc(Long recruiterId);
}
