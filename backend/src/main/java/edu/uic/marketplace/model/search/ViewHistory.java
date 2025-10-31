package edu.uic.marketplace.model.search;

import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name = "view_history",
        indexes = {
                @Index(name = "idx_view_history_user_id", columnList = "user_id"),
                @Index(name = "idx_view_history_listing_id", columnList = "listing_id"),
                @Index(name = "idx_view_history_viewed_at", columnList = "viewed_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewHistory {

    @EmbeddedId
    private ViewHistoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_view_history_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("listingId")
    @JoinColumn(name = "listing_id", foreignKey = @ForeignKey(name = "fk_view_history_listing"))
    private Listing listing;

    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt;

    @PrePersist
    public void prePersist() {
        if (viewedAt == null) {
            viewedAt = Instant.now();
        }
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ViewHistoryId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "listing_id")
        private Long listingId;
    }
}