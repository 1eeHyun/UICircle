package edu.uic.marketplace.model.moderation;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name = "blocks",
        indexes = {
                @Index(name = "idx_blocks_blocker_id", columnList = "blocker_id"),
                @Index(name = "idx_blocks_blocked_id", columnList = "blocked_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @EmbeddedId
    private BlockId id;

    /**
     * User who blocked another user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("blockerId")
    @JoinColumn(name = "blocker_id", foreignKey = @ForeignKey(name = "fk_blocks_blocker"))
    private User blocker;

    /**
     * User who was blocked
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("blockedId")
    @JoinColumn(name = "blocked_id", foreignKey = @ForeignKey(name = "fk_blocks_blocked"))
    private User blocked;

    /**
     * Block timestamp
     */
    @CreationTimestamp
    @Column(name = "blocked_at", nullable = false, updatable = false)
    private Instant blockedAt;

    /**
     * Composite Primary Key for Block
     */
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BlockId implements Serializable {
        @Column(name = "blocker_id")
        private Long blockerId;

        @Column(name = "blocked_id")
        private Long blockedId;
    }
}
