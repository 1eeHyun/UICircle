package edu.uic.marketplace.service.notification;

import edu.uic.marketplace.dto.request.notification.UpdateEmailSubscriptionRequest;
import edu.uic.marketplace.dto.response.notification.EmailSubscriptionResponse;
import edu.uic.marketplace.model.notification.EmailSubscription;

import java.util.Optional;

/**
 * Email subscription management service interface
 */
public interface EmailSubscriptionService {
    
    /**
     * Get user's email subscription settings
     * @param userId User ID
     * @return Email subscription entity
     */
    Optional<EmailSubscription> findByUserId(Long userId);
    
    /**
     * Get email subscription response
     * @param userId User ID
     * @return Email subscription response
     */
    EmailSubscriptionResponse getEmailSubscription(Long userId);
    
    /**
     * Update email subscription settings
     * @param userId User ID
     * @param request Update request
     * @return Updated email subscription response
     */
    EmailSubscriptionResponse updateEmailSubscription(Long userId, UpdateEmailSubscriptionRequest request);
    
    /**
     * Create default email subscription for new user
     * @param userId User ID
     * @return Created email subscription
     */
    EmailSubscription createDefaultSubscription(Long userId);
    
    /**
     * Check if user is subscribed to new message emails
     * @param userId User ID
     * @return true if subscribed, false otherwise
     */
    boolean isSubscribedToNewMessages(Long userId);
    
    /**
     * Check if user is subscribed to price change emails
     * @param userId User ID
     * @return true if subscribed, false otherwise
     */
    boolean isSubscribedToPriceChanges(Long userId);
    
    /**
     * Check if user is subscribed to offer emails
     * @param userId User ID
     * @return true if subscribed, false otherwise
     */
    boolean isSubscribedToOffers(Long userId);
    
    /**
     * Check if user is subscribed to listing status emails
     * @param userId User ID
     * @return true if subscribed, false otherwise
     */
    boolean isSubscribedToListingStatus(Long userId);
}
