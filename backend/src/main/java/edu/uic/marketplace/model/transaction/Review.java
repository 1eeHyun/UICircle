package edu.uic.marketplace.model.transaction;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "reviews",
        indexes = {
                @Index(name = "idx_reviews_transaction_id", columnList = "transaction_id"),
                @Index(name = "idx_reviews_reviewer_id", columnList = "reviewer_id"),
                @Index(name = "idx_reviews_reviewed_user_id", columnList = "reviewed_user_id"),
                @Index(name = "idx_reviews_created_at", columnList = "created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reviews_transaction_reviewer", 
                        columnNames = {"transaction_id", "reviewer_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "public_id", nullable = false, updatable = false, unique = true, length = 36)
    private String publicId;

    /**
     * Transaction this review is for
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_transaction"))
    private Transaction transaction;

    /**
     * User who wrote the review
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_reviewer"))
    private User reviewer;

    /**
     * User being reviewed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_reviewed_user"))
    private User reviewedUser;

    /**
     * Rating (1-5)
     */
    @Column(name = "rating", nullable = false)
    private Integer rating;

    /**
     * Review comment
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

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
    public boolean isPositive() {
        return rating >= 4;
    }

    public boolean isNegative() {
        return rating <= 2;
    }

    public boolean isNeutral() {
        return rating == 3;
    }
}
