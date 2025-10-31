package edu.uic.marketplace.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name = "user_badges",
        indexes = {
                @Index(name = "idx_user_badges_user_id", columnList = "user_id"),
                @Index(name = "idx_user_badges_badge_id", columnList = "badge_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {

    @EmbeddedId
    private UserBadgeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_badges_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("badgeId")
    @JoinColumn(name = "badge_id", foreignKey = @ForeignKey(name = "fk_user_badges_badge"))
    private Badge badge;

    @CreationTimestamp
    @Column(name = "awarded_at", nullable = false, updatable = false)
    private Instant awardedAt;

    // Composite Key
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UserBadgeId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "badge_id")
        private Long badgeId;
    }
}
