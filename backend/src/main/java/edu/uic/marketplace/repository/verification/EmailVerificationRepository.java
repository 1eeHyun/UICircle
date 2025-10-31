package edu.uic.marketplace.repository.verification;

import edu.uic.marketplace.model.verification.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    
    /**
     * Find verification by token
     */
    Optional<EmailVerification> findByToken(String token);
    
    /**
     * Find verification by user
     */
    Optional<EmailVerification> findByUser_UserId(Long userId);
    
    /**
     * Find latest verification by user
     */
    Optional<EmailVerification> findFirstByUser_UserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find expired tokens
     */
    List<EmailVerification> findByExpiresAtBeforeAndVerifiedAtIsNull(Instant expiresAt);
    
    /**
     * Delete verifications by user
     */
    void deleteByUser_UserId(Long userId);
    
    /**
     * Delete expired tokens
     */
    void deleteByExpiresAtBefore(Instant expiresAt);
}
