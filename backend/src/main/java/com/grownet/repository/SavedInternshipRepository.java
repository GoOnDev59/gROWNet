package com.grownet.repository;

import com.grownet.entity.SavedInternship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavedInternshipRepository extends JpaRepository<SavedInternship, Long> {
    boolean existsByUserIdAndInternshipId(Long userId, Long internshipId);
    List<SavedInternship> findByUserId(Long userId);
}
