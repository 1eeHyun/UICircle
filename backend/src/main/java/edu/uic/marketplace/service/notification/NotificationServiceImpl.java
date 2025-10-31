package edu.uic.marketplace.service.notification;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.notification.NotificationResponse;
import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @Override
    public NotificationResponse createNotification(Long userId, NotificationType type, String entityType, Long entityId, String message) {
        return null;
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return Optional.empty();
    }

    @Override
    public PageResponse<NotificationResponse> getUserNotifications(Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public PageResponse<NotificationResponse> getUnreadNotifications(Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        return null;
    }

    @Override
    public void markAllAsRead(Long userId) {

    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {

    }

    @Override
    public void deleteAllNotifications(Long userId) {

    }

    @Override
    public Long getUnreadCount(Long userId) {
        return null;
    }

    @Override
    public void notifyNewMessage(Long receiverId, Long senderId, Long conversationId) {

    }

    @Override
    public void notifyNewOffer(Long sellerId, Long buyerId, Long listingId) {

    }

    @Override
    public void notifyOfferStatusChange(Long buyerId, Long listingId, String status) {

    }
}
