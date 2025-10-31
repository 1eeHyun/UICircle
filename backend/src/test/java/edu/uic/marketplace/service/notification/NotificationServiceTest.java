package edu.uic.marketplace.service.notification;

import edu.uic.marketplace.dto.response.notification.NotificationResponse;
import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.notification.NotificationRepository;
import edu.uic.marketplace.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Test")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {

        user = User.builder().userId(1L).build();
        notification = Notification.builder()
                .notificationId(1L)
                .user(user)
                .type(NotificationType.NEW_MESSAGE)
                .entityType("message")
                .entityId(1L)
                .message("You have a new message")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Create a notification - Success")
    void createNotification_Success() {

        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // When
        NotificationResponse response = notificationService.createNotification(
                1L, NotificationType.NEW_MESSAGE, "message", 1L, "You have a new message"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("You have a new message");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Mark as read")
    void markAsRead() {

        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // When
        NotificationResponse response = notificationService.markAsRead(1L, 1L);

        // Then
        assertThat(notification.getReadAt()).isNotNull();
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("Mark all as read")
    void markAllAsRead() {

        // Given & When
        notificationService.markAllAsRead(1L);

        // Then
        verify(notificationRepository, times(1)).markAllAsReadByUserId(1L);
    }
}
