
package com.grownet.service;

import com.grownet.entity.PasswordResetOtp;
import com.grownet.repository.PasswordResetOtpRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetOtpService {

    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;

    public PasswordResetOtpService(
            PasswordResetOtpRepository otpRepository,
            PasswordEncoder passwordEncoder) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void generateOtp(String mobileNumber) {
        String normalizedMobile = mobileNumber.trim();

        // Invalidate older, unused OTPs for this number.
        otpRepository.findAll().stream()
                .filter(existing ->
                        existing.getMobileNumber().equals(normalizedMobile)
                                && !existing.isUsed())
                .forEach(existing -> {
                    existing.setUsed(true);
                    otpRepository.save(existing);
                });

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));

        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setMobileNumber(normalizedMobile);
        resetOtp.setOtpHash(passwordEncoder.encode(otp));
        resetOtp.setExpiresAt(
                LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        resetOtp.setAttempts(0);
        resetOtp.setUsed(false);

        otpRepository.save(resetOtp);

        // Development only: never enable OTP logging in production.
        System.out.println("========================================");
        System.out.println("GROWNET DEVELOPMENT OTP");
        System.out.println("Mobile: " + normalizedMobile);
        System.out.println("OTP: " + otp);
        System.out.println("Expires in: " + OTP_EXPIRY_MINUTES + " minutes");
        System.out.println("========================================");
    }

    /**
     * Validates an OTP without consuming it.
     * The password-reset operation must call this and mark the OTP
     * as used only after a successful password update.
     */
    @Transactional
    public boolean verifyOtp(String mobileNumber, String otp) {
        if (mobileNumber == null || otp == null
                || !otp.matches("\\d{6}")) {
            return false;
        }

        PasswordResetOtp resetOtp = otpRepository
                .findTopByMobileNumberAndUsedFalseOrderByCreatedAtDesc(
                        mobileNumber.trim())
                .orElse(null);

        if (resetOtp == null
                || resetOtp.getExpiresAt().isBefore(LocalDateTime.now())
                || resetOtp.getAttempts() >= MAX_ATTEMPTS) {
            return false;
        }

        resetOtp.setAttempts(resetOtp.getAttempts() + 1);
        boolean valid = passwordEncoder.matches(otp, resetOtp.getOtpHash());

        otpRepository.save(resetOtp);
        return valid;
    }

    /**
     * Consumes the most recent unused OTP after the password is reset.
     */
    @Transactional
    public void consumeOtp(String mobileNumber) {
        otpRepository
                .findTopByMobileNumberAndUsedFalseOrderByCreatedAtDesc(
                        mobileNumber.trim())
                .ifPresent(resetOtp -> {
                    resetOtp.setUsed(true);
                    otpRepository.save(resetOtp);
                });
    }
}

