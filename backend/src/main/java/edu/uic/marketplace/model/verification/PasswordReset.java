package edu.uic.marketplace.model.verification;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "password_resets",
        indexes = {
                @Index(name = "idx_password_resets_user_id", columnList = "user_id"),
                @Index(name = "idx_password_resets_token", columnList = "token"),
                @Index(name = "idx_password_resets_expires_at", columnList = "expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * User requesting password reset
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_password_resets_user"))
    private User user;

    /**
     * Reset token
     */
    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    /**
     * Expiration timestamp
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /**
     * Timestamp when token was used
     */
    @Column(name = "used_at")
    private Instant usedAt;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Helper Methods
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isValid() {
        return !isExpired() && !isUsed();
    }

    public void markAsUsed() {
        this.usedAt = Instant.now();
    }
}
