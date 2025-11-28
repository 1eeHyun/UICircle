package edu.uic.marketplace.service.notification;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.notification.NotificationResponse;
import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.notification.NotificationRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AuthValidator authValidator;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User createUser(Long id, String username) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        return user;
    }

    private Notification createNotification(Long id, String publicId, User user) {
        return Notification.builder()
                .notificationId(id)
                .publicId(publicId)
                .user(user)
                .type(NotificationType.SYSTEM)
                .title("System Notification")
                .message("Test message")
                .entityType("listing")
                .entityId("listing-123")
                .linkUrl("/listings/listing-123")
                .readAt(null)
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("createNotification")
    class CreateNotificationTests {

        @Test
        @DisplayName("should create notification for valid user")
        void createNotification_success() {

            // given
            String username = "seller1";
            String entityType = "listing";
            String entityId = "abc-123";
            String message = "New offer";

            User user = createUser(1L, username);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(notificationRepository.save(any(Notification.class)))
                    .thenAnswer(invocation -> {
                        Notification n = invocation.getArgument(0);
                        if (n.getCreatedAt() == null) {
                            n.setCreatedAt(Instant.now());
                        }
                        n.setNotificationId(10L);
                        n.setPublicId("notif-10");
                        return n;
                    });

            // when
            NotificationResponse response = notificationService.createNotification(
                    username,
                    NotificationType.PRICE_OFFER,
                    entityType,
                    entityId,
                    message
            );

            // then
            assertThat(response.getNotificationId()).isEqualTo(10L);
            assertThat(response.getType()).isEqualTo(NotificationType.PRICE_OFFER);
            assertThat(response.getMessage()).isEqualTo(message);
            assertThat(response.getEntityType()).isEqualTo(entityType);
            assertThat(response.getEntityId()).isEqualTo(entityId);
            assertThat(response.getLinkUrl()).isEqualTo("/listings/" + entityId);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            Notification saved = captor.getValue();
            assertThat(saved.getUser()).isEqualTo(user);
            assertThat(saved.getType()).isEqualTo(NotificationType.PRICE_OFFER);
        }
    }

    @Nested
    @DisplayName("getUserNotifications")
    class GetUserNotificationsTests {

        @Test
        @DisplayName("should return paged notifications for user")
        void getUserNotifications_success() {

            // given
            String username = "user1";
            User user = createUser(1L, username);
            Notification n1 = createNotification(1L, "notif-1", user);

            Page<Notification> page = new PageImpl<>(
                    List.of(n1),
                    PageRequest.of(0, 10, Sort.by("createdAt").descending()),
                    1
            );

            when(notificationRepository.findByUser_UsernameOrderByCreatedAtDesc(eq(username), any(Pageable.class)))
                    .thenReturn(page);

            // when
            PageResponse<NotificationResponse> response =
                    notificationService.getUserNotifications(username, 0, 10);

            // then
            assertThat(response.getContent()).hasSize(1);
            NotificationResponse dto = response.getContent().get(0);
            assertThat(dto.getNotificationId()).isEqualTo(1L);
            assertThat(dto.getMessage()).isEqualTo("Test message");

            assertThat(response.getCurrentPage()).isEqualTo(0);
            assertThat(response.getTotalElements()).isEqualTo(1);
            assertThat(response.getTotalPages()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getUnreadNotifications")
    class GetUnreadNotificationsTests {

        @Test
        @DisplayName("should return paged unread notifications for user")
        void getUnreadNotifications_success() {

            // given
            String username = "user1";
            User user = createUser(1L, username);
            Notification n1 = createNotification(1L, "notif-1", user); // unread

            Page<Notification> page = new PageImpl<>(
                    List.of(n1),
                    PageRequest.of(0, 10),
                    1
            );

            when(notificationRepository.findByUser_UsernameAndReadAtIsNullOrderByCreatedAtDesc(
                    eq(username),
                    any(Pageable.class)
            )).thenReturn(page);

            // when
            PageResponse<NotificationResponse> response =
                    notificationService.getUnreadNotifications(username, 0, 10);

            // then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getIsRead()).isFalse();
        }
    }

    @Nested
    @DisplayName("markAsRead")
    class MarkAsReadTests {

        @Test
        @DisplayName("should mark notification as read when it belongs to user")
        void markAsRead_success() {

            // given
            String username = "user1";
            String publicId = "notif-1";
            User user = createUser(1L, username);
            Notification n1 = createNotification(1L, publicId, user);
            n1.setReadAt(null); // unread

            when(notificationRepository.findByPublicIdAndUser_Username(publicId, username))
                    .thenReturn(Optional.of(n1));

            // when
            NotificationResponse response = notificationService.markAsRead(publicId, username);

            // then
            assertThat(response.getIsRead()).isTrue();
            assertThat(n1.getReadAt()).isNotNull();
        }

        @Test
        @DisplayName("should throw when notification does not exist or does not belong to user")
        void markAsRead_notFound() {

            // given
            String username = "user1";
            String publicId = "notif-1";

            when(notificationRepository.findByPublicIdAndUser_Username(publicId, username))
                    .thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> notificationService.markAsRead(publicId, username))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Notification not found");
        }
    }

    @Nested
    @DisplayName("markAllAsRead")
    class MarkAllAsReadTests {

        @Test
        @DisplayName("should delegate to repository to mark all as read")
        void markAllAsRead_success() {

            // given
            String username = "user1";

            // when
            notificationService.markAllAsRead(username);

            // then
            verify(notificationRepository).markAllAsReadByUsername(username);
        }
    }

    @Nested
    @DisplayName("deleteNotification")
    class DeleteNotificationTests {

        @Test
        @DisplayName("should delete notification when it belongs to user")
        void deleteNotification_success() {
            // given
            String username = "user1";
            String publicId = "notif-1";
            User user = createUser(1L, username);
            Notification n1 = createNotification(1L, publicId, user);

            when(notificationRepository.findByPublicIdAndUser_Username(publicId, username))
                    .thenReturn(Optional.of(n1));

            // when
            notificationService.deleteNotification(publicId, username);

            // then
            verify(notificationRepository).delete(n1);
        }

        @Test
        @DisplayName("should throw when deleting non-existing notification")
        void deleteNotification_notFound() {
            // given
            String username = "user1";
            String publicId = "notif-1";

            when(notificationRepository.findByPublicIdAndUser_Username(publicId, username))
                    .thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> notificationService.deleteNotification(publicId, username))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Notification not found");
        }
    }

    @Nested
    @DisplayName("deleteAllNotifications")
    class DeleteAllNotificationsTests {

        @Test
        @DisplayName("should delete all notifications for user")
        void deleteAllNotifications_success() {
            // given
            String username = "user1";

            // when
            notificationService.deleteAllNotifications(username);

            // then
            verify(notificationRepository).deleteByUser_Username(username);
        }
    }

    @Nested
    @DisplayName("getUnreadCount")
    class GetUnreadCountTests {

        @Test
        @DisplayName("should return unread count from repository")
        void getUnreadCount_success() {
            // given
            String username = "user1";
            when(notificationRepository.countByUser_UsernameAndReadAtIsNull(username))
                    .thenReturn(5L);

            // when
            Long count = notificationService.getUnreadCount(username);

            // then
            assertThat(count).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("notifyNewMessage")
    class NotifyNewMessageTests {

        @Test
        @DisplayName("should create notification for receiver when new message arrives")
        void notifyNewMessage_success() {
            // given
            String receiverUsername = "receiver";
            String senderUsername = "sender";
            String conversationId = "conv-1";

            User receiver = createUser(1L, receiverUsername);
            User sender = createUser(2L, senderUsername);

            when(authValidator.validateUserByUsername(receiverUsername)).thenReturn(receiver);
            when(authValidator.validateUserByUsername(senderUsername)).thenReturn(sender);

            // when
            notificationService.notifyNewMessage(receiverUsername, senderUsername, conversationId);

            // then
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            Notification saved = captor.getValue();

            assertThat(saved.getUser()).isEqualTo(receiver);
            assertThat(saved.getType()).isEqualTo(NotificationType.NEW_MESSAGE);
            assertThat(saved.getEntityType()).isEqualTo("conversation");
            assertThat(saved.getEntityId()).isEqualTo(conversationId);
            assertThat(saved.getLinkUrl()).isEqualTo("/chat/" + conversationId);
            assertThat(saved.getMessage()).contains(senderUsername);
        }
    }

    @Nested
    @DisplayName("notifyNewOffer")
    class NotifyNewOfferTests {

        @Test
        @DisplayName("should create notification for seller when new offer is created")
        void notifyNewOffer_success() {

            // given
            String sellerUsername = "seller";
            String buyerUsername = "buyer";
            String listingPublicId = "listing-xyz";

            User seller = createUser(1L, sellerUsername);
            User buyer = createUser(2L, buyerUsername);

            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);
            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);

            // when
            notificationService.notifyNewOffer(sellerUsername, buyerUsername, listingPublicId);

            // then
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            Notification saved = captor.getValue();

            assertThat(saved.getUser()).isEqualTo(seller);
            assertThat(saved.getType()).isEqualTo(NotificationType.PRICE_OFFER);
            assertThat(saved.getEntityType()).isEqualTo("listing");
            assertThat(saved.getEntityId()).isEqualTo(listingPublicId);
            assertThat(saved.getLinkUrl()).isEqualTo("/listings/" + listingPublicId);
            assertThat(saved.getMessage()).contains(buyerUsername);
        }
    }

    @Nested
    @DisplayName("notifyOfferStatusChange")
    class NotifyOfferStatusChangeTests {

        @Test
        @DisplayName("should create accepted notification for buyer")
        void notifyOfferStatusChange_accepted() {

            // given
            String buyerUsername = "buyer";
            String listingPublicId = "listing-xyz";

            User buyer = createUser(1L, buyerUsername);
            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);

            // when
            notificationService.notifyOfferStatusChange(buyerUsername, listingPublicId, "ACCEPTED");

            // then
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            Notification saved = captor.getValue();

            assertThat(saved.getUser()).isEqualTo(buyer);
            assertThat(saved.getType()).isEqualTo(NotificationType.OFFER_ACCEPTED);
            assertThat(saved.getMessage()).contains("accepted");
        }

        @Test
        @DisplayName("should create rejected notification for buyer")
        void notifyOfferStatusChange_rejected() {

            // given
            String buyerUsername = "buyer";
            String listingPublicId = "listing-xyz";

            User buyer = createUser(1L, buyerUsername);
            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);

            // when
            notificationService.notifyOfferStatusChange(buyerUsername, listingPublicId, "REJECTED");

            // then
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            Notification saved = captor.getValue();

            assertThat(saved.getType()).isEqualTo(NotificationType.OFFER_REJECTED);
            assertThat(saved.getMessage()).contains("rejected");
        }

        @Test
        @DisplayName("should fallback to SYSTEM type for unknown status")
        void notifyOfferStatusChange_unknown() {

            // given
            String buyerUsername = "buyer";
            String listingPublicId = "listing-xyz";

            User buyer = createUser(1L, buyerUsername);
            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);

            // when
            notificationService.notifyOfferStatusChange(buyerUsername, listingPublicId, "PENDING_REVIEW");

            // then
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            Notification saved = captor.getValue();

            assertThat(saved.getType()).isEqualTo(NotificationType.SYSTEM);
            assertThat(saved.getMessage()).contains("PENDING_REVIEW");
        }
    }
}
