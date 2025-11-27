package edu.uic.marketplace.service.notification;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.notification.NotificationResponse;
import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;

import java.util.Optional;

/**
 * Notification management service interface
 */
public interface NotificationService {
    
    /**
     * Create notification
     * @param username User username to notify
     * @param type Notification type
     * @param entityType Entity type (e.g., "listing", "message")
     * @param entityId Entity ID
     * @param message Notification message
     * @return Created notification response
     */
    NotificationResponse createNotification(String username, NotificationType type, String entityType, String entityId, String message);
    
    /**
     * Get notification by ID
     * @param notificationId Notification ID
     * @return Notification entity
     */
    Optional<Notification> findById(Long notificationId);
    
    /**
     * Get user's notifications
     * @param username User username
     * @param page Page number
     * @param size Page size
     * @return Paginated notification responses
     */
    PageResponse<NotificationResponse> getUserNotifications(String username, Integer page, Integer size);
    
    /**
     * Get unread notifications
     * @param username User username
     * @param page Page number
     * @param size Page size
     * @return Paginated unread notification responses
     */
    PageResponse<NotificationResponse> getUnreadNotifications(String username, Integer page, Integer size);
    
    /**
     * Mark notification as read
     * @param notificationId Notification ID
     * @param username User username
     * @return Updated notification response
     */
    NotificationResponse markAsRead(String notificationId, String username);
    
    /**
     * Mark all notifications as read
     * @param username User username
     */
    void markAllAsRead(String username);
    
    /**
     * Delete notification
     * @param notificationId Notification ID
     * @param username User username
     */
    void deleteNotification(String notificationId, String username);
    
    /**
     * Delete all notifications for user
     * @param username User username
     */
    void deleteAllNotifications(String username);
    
    /**
     * Get unread notification count
     * @param username User username
     * @return Number of unread notifications
     */
    Long getUnreadCount(String username);
    
    /**
     * Send notification for new message
     * @param receiverUsername Receiver user username
     * @param senderUsername Sender user username
     * @param conversationId Conversation ID
     */
    void notifyNewMessage(String receiverUsername, String senderUsername, String conversationId);
    
    /**
     * Send notification for price offer
     * @param sellerUsername Seller user username
     * @param buyerUsername Buyer user username
     * @param listingPublicId Listing ID
     */
    void notifyNewOffer(String sellerUsername, String buyerUsername, String listingPublicId);

    /**
     * Send notification for price offer
     * @param sellerUsername Seller user username
     * @param buyerUsername Buyer user username
     * @param listingPublicId Listing ID
     */
    void notifyOfferCanceled(String sellerUsername, String buyerUsername, String listingPublicId);
    
    /**
     * Send notification for offer status change
     * @param buyerUsername Buyer user username
     * @param listingPublicId Listing ID
     * @param status Offer status (accepted/rejected)
     */
    void notifyOfferStatusChange(String buyerUsername, String listingPublicId, String status);

    /**
     * Send notification for listing favorited
     * @param sellerUsername Seller user username
     * @param followerUsername Follower user username
     * @param listingPublicId Offer status (accepted/rejected)
     */
    void notifyListingFavorited(String sellerUsername, String followerUsername, String listingPublicId);
}
