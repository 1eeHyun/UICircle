package edu.uic.marketplace.repository.notification;

import edu.uic.marketplace.model.notification.EmailSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailSubscriptionRepository extends JpaRepository<EmailSubscription, Long> {
    
    /**
     * Find email subscription by user
     */
    Optional<EmailSubscription> findByUser_UserId(Long userId);
    
    /**
     * Check if subscription exists for user
     */
    boolean existsByUser_UserId(Long userId);
    
    /**
     * Delete subscription by user
     */
    void deleteByUser_UserId(Long userId);
}
