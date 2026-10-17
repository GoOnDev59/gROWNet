package com.grownet.repository;

import com.grownet.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByVerifiedTrue();

    List<Internship> findByRecruiterId(Long recruiterId);

    List<Internship> findByVerifiedTrueAndTitleContainingIgnoreCaseOrVerifiedTrueAndCompanyContainingIgnoreCase(
            String title, String company);

    List<Internship> findByVerifiedTrueAndLocationContainingIgnoreCase(
            String location);

    List<Internship> findByVerifiedTrueAndSkillsRequiredContainingIgnoreCase(
            String skill);
}