package edu.uic.marketplace.service.notification;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.notification.NotificationResponse;
import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.notification.NotificationRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthValidator authValidator;

    @Override
    @Transactional
    public NotificationResponse createNotification(String username, NotificationType type, String entityType, String entityId, String message) {

        // Validate user
        User user = authValidator.validateUserByUsername(username);

        // Create a new notification
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(type.getDescription())
                .message(message)
                .linkUrl(generateLink(entityType, entityId))
                .entityType(entityType)
                .entityId(entityId)
                .createdAt(Instant.now())
                .build();

        // Save
        notificationRepository.save(notification);

        return NotificationResponse.from(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> findById(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getUserNotifications(String username, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationResponse> results = notificationRepository
                .findByUser_UsernameOrderByCreatedAtDesc(username, pageable)
                .map(NotificationResponse::from);

        return PageResponse.fromPage(results);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getUnreadNotifications(String username, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationResponse> results = notificationRepository
                .findByUser_UsernameAndReadAtIsNullOrderByCreatedAtDesc(username, pageable)
                .map(NotificationResponse::from);

        return PageResponse.fromPage(results);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(String notificationId, String username) {

        Notification notification = notificationRepository
                .findByPublicIdAndUser_Username(notificationId, username)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (notification.isUnread()) {
            notification.markAsRead();
        }

        return NotificationResponse.from(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        notificationRepository.markAllAsReadByUsername(username);
    }

    @Override
    @Transactional
    public void deleteNotification(String notificationId, String username) {

        Notification notification = notificationRepository
                .findByPublicIdAndUser_Username(notificationId, username)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(String username) {
        notificationRepository.deleteByUser_Username(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(String username) {
        return notificationRepository.countByUser_UsernameAndReadAtIsNull(username);
    }

    @Override
    @Transactional
    public void notifyNewMessage(String receiverUsername, String senderUsername, String conversationId) {

        User receiver = authValidator.validateUserByUsername(receiverUsername);
        User sender = authValidator.validateUserByUsername(senderUsername);

        String message = sender.getUsername() + " sent you a new message";

        createNotification(
                receiver.getUsername(),
                NotificationType.NEW_MESSAGE,
                "conversation",
                conversationId,
                message
        );
    }

    @Override
    @Transactional
    public void notifyNewOffer(String sellerUsername, String buyerUsername, String listingPublicId) {

        User seller = authValidator.validateUserByUsername(sellerUsername);
        User buyer = authValidator.validateUserByUsername(buyerUsername);

        String message = buyer.getUsername() + " made a price offer on your listing";

        createNotification(
                seller.getUsername(),
                NotificationType.PRICE_OFFER,
                "listing",
                listingPublicId,
                message
        );
    }

    @Override
    @Transactional
    public void notifyOfferStatusChange(String buyerUsername, String listingPublicId, String status) {

        User buyer = authValidator.validateUserByUsername(buyerUsername);

        String normalized = status == null ? "" : status.toUpperCase(Locale.ROOT);
        NotificationType type;
        String message;

        switch (normalized) {
            case "ACCEPTED" -> {
                type = NotificationType.OFFER_ACCEPTED;
                message = "Your offer has been accepted.";
            }
            case "REJECTED" -> {
                type = NotificationType.OFFER_REJECTED;
                message = "Your offer has been rejected.";
            }
            default -> {
                type = NotificationType.SYSTEM;
                message = "Your offer status has been updated: " + status;
            }
        }

        createNotification(
                buyer.getUsername(),
                type,
                "listing",
                listingPublicId,
                message
        );
    }

    @Override
    @Transactional
    public void notifyOfferCanceled(String sellerUsername, String buyerUsername, String listingPublicId) {

        User seller = authValidator.validateUserByUsername(sellerUsername);
        User buyer = authValidator.validateUserByUsername(buyerUsername);

        String message = buyer.getUsername() + " canceled their offer on your listing.";

        createNotification(
                seller.getUsername(),
                NotificationType.SYSTEM,
                "listing",
                listingPublicId,
                message
        );
    }

    @Override
    @Transactional
    public void notifyListingFavorited(String sellerUsername, String followerUsername, String listingPublicId) {

        User seller = authValidator.validateUserByUsername(sellerUsername);
        User follower = authValidator.validateUserByUsername(followerUsername);

        String message = follower.getUsername() + " favorited your listing.";

        createNotification(
                seller.getUsername(),
                NotificationType.LISTING_FAVORITED,
                "listing",
                listingPublicId,
                message
        );
    }

    // Helper methods

    /**
     * Generate a frontend link URL based on the entity type and entity ID.
     */
    private String generateLink(String entityType, String entityId) {

        if (entityType == null || entityId == null) {
            return null;
        }

        String type = entityType.toLowerCase(Locale.ROOT);

        return switch (type) {
            case "listing" -> "/listings/" + entityId;
            case "conversation" -> "/chat/" + entityId;
            case "review" -> "/reviews/" + entityId;
            default -> null;
        };
    }
}
