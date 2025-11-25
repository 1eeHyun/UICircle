package edu.uic.marketplace.dto.response.notification;

import edu.uic.marketplace.model.notification.Notification;
import edu.uic.marketplace.model.notification.NotificationType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;
    private NotificationType type;
    private String title;
    private String message;
    private String linkUrl;
    private String entityType;
    private String entityId;
    private Boolean isRead;
    private Instant readAt;
    private Instant createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .linkUrl(notification.getLinkUrl())
                .entityType(notification.getEntityType())
                .entityId(notification.getEntityId())
                .isRead(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}