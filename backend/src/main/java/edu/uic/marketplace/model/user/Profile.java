package edu.uic.marketplace.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "public_id", nullable = false, updatable = false, unique = true, length = 36)
    private String publicId;

    /**
     * User relationship (FK)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_profiles_user"))
    private User user;

    /**
     * Profile information
     */
    @Column(name = "display_name", length = 50)
    private String displayName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "major", length = 100)
    private String major;

    /**
     * Statistics - Number of items sold
     */
    @Column(name = "sold_count", nullable = false)
    @Builder.Default
    private Integer soldCount = 0;

    /**
     * Statistics - Number of items bought
     */
    @Column(name = "buy_count", nullable = false)
    @Builder.Default
    private Integer buyCount = 0;

    /**
     * Timestamps
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.publicId == null)
            this.publicId = UUID.randomUUID().toString();
    }

    /**
     * Helper Methods
     */
    public void incrementSoldCount() {
        this.soldCount++;
    }

    public void incrementBuyCount() {
        this.buyCount++;
    }
}
