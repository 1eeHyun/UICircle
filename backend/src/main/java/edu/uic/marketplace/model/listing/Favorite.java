package edu.uic.marketplace.model.listing;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name = "favorites",
        indexes = {
                @Index(name = "idx_favorites_user_id", columnList = "user_id"),
                @Index(name = "idx_favorites_listing_id", columnList = "listing_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_favorites_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("listingId")
    @JoinColumn(name = "listing_id", foreignKey = @ForeignKey(name = "fk_favorites_listing"))
    private Listing listing;

    @CreationTimestamp
    @Column(name = "favorited_at", nullable = false, updatable = false)
    private Instant favoritedAt;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class FavoriteId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "listing_id")
        private Long listingId;
    }
}