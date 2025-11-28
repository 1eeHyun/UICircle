package edu.uic.marketplace.model.notification;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user_id", columnList = "user_id"),
                @Index(name = "idx_notifications_type", columnList = "type"),
                @Index(name = "idx_notifications_read_at", columnList = "read_at"),
                @Index(name = "idx_notifications_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "public_id", nullable = false, updatable = false, unique = true, length = 36)
    private String publicId;

    /**
     * User receiving the notification
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notifications_user"))
    private User user;

    /**
     * Notification type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    /**
     * Notification title
     */
    @Column(name = "title", length = 200)
    private String title;

    /**
     * Notification message
     */
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    /**
     * Link URL for the notification
     */
    @Column(name = "link_url", length = 500)
    private String linkUrl;

    /**
     * Related entity type (listing, message, transaction, etc.)
     */
    @Column(name = "entity_type", length = 50)
    private String entityType;

    /**
     * Related entity ID
     */
    @Column(name = "entity_id")
    private String entityId;

    /**
     * Read timestamp - null means unread
     */
    @Column(name = "read_at")
    private Instant readAt;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.publicId == null)
            this.publicId = UUID.randomUUID().toString();
    }

    /**
     * Helper Methods
     */
    public void markAsRead() {
        this.readAt = Instant.now();
    }

    public boolean isRead() {
        return readAt != null;
    }

    public boolean isUnread() {
        return readAt == null;
    }
}
