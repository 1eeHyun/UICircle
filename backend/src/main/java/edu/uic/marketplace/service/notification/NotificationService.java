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
     * @param userId User ID to notify
     * @param type Notification type
     * @param entityType Entity type (e.g., "listing", "message")
     * @param entityId Entity ID
     * @param message Notification message
     * @return Created notification response
     */
    NotificationResponse createNotification(Long userId, NotificationType type, String entityType, Long entityId, String message);
    
    /**
     * Get notification by ID
     * @param notificationId Notification ID
     * @return Notification entity
     */
    Optional<Notification> findById(Long notificationId);
    
    /**
     * Get user's notifications
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated notification responses
     */
    PageResponse<NotificationResponse> getUserNotifications(Long userId, Integer page, Integer size);
    
    /**
     * Get unread notifications
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated unread notification responses
     */
    PageResponse<NotificationResponse> getUnreadNotifications(Long userId, Integer page, Integer size);
    
    /**
     * Mark notification as read
     * @param notificationId Notification ID
     * @param userId User ID
     * @return Updated notification response
     */
    NotificationResponse markAsRead(Long notificationId, Long userId);
    
    /**
     * Mark all notifications as read
     * @param userId User ID
     */
    void markAllAsRead(Long userId);
    
    /**
     * Delete notification
     * @param notificationId Notification ID
     * @param userId User ID
     */
    void deleteNotification(Long notificationId, Long userId);
    
    /**
     * Delete all notifications for user
     * @param userId User ID
     */
    void deleteAllNotifications(Long userId);
    
    /**
     * Get unread notification count
     * @param userId User ID
     * @return Number of unread notifications
     */
    Long getUnreadCount(Long userId);
    
    /**
     * Send notification for new message
     * @param receiverId Receiver user ID
     * @param senderId Sender user ID
     * @param conversationId Conversation ID
     */
    void notifyNewMessage(Long receiverId, Long senderId, Long conversationId);
    
    /**
     * Send notification for price offer
     * @param sellerId Seller user ID
     * @param buyerId Buyer user ID
     * @param listingId Listing ID
     */
    void notifyNewOffer(Long sellerId, Long buyerId, Long listingId);
    
    /**
     * Send notification for offer status change
     * @param buyerId Buyer user ID
     * @param listingId Listing ID
     * @param status Offer status (accepted/rejected)
     */
    void notifyOfferStatusChange(Long buyerId, Long listingId, String status);
}
