package com.grownet.repository;

import com.grownet.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findTopByMobileNumberAndUsedFalseOrderByCreatedAtDesc(String mobileNumber);
}
