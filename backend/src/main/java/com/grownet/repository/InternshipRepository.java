
package com.grownet.repository;

import com.grownet.entity.Internship;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByVerifiedTrue();

    List<Internship> findByRecruiterId(Long recruiterId);

    @EntityGraph(attributePaths = {"recruiter"})
    List<Internship> findByVerifiedFalseAndRejectedFalse();
}

