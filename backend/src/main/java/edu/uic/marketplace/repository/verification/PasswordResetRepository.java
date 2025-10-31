package edu.uic.marketplace.repository.verification;

import edu.uic.marketplace.model.verification.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    /**
     * Find password reset by token
     */
    Optional<PasswordReset> findByToken(String token);

    /**
     * Find password reset by user ID
     */
    Optional<PasswordReset> findByUser_UserId(Long userId);

    /**
     * Find password reset by user ID and token
     */
    Optional<PasswordReset> findByUser_UserIdAndToken(Long userId, String token);

    /**
     * Delete all password resets for a user
     */
    void deleteByUser_UserId(Long userId);

    /**
     * Delete expired password resets
     */
    void deleteByExpiresAtBefore(Instant expiryDate);

    /**
     * Check if user has an unused password reset request
     */
    boolean existsByUser_UserIdAndUsedAtIsNull(Long userId);
}
