package edu.uic.marketplace.model.transaction;

import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_listing_id", columnList = "listing_id"),
                @Index(name = "idx_transactions_buyer_id", columnList = "buyer_id"),
                @Index(name = "idx_transactions_status", columnList = "status"),
                @Index(name = "idx_transactions_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "public_id", nullable = false, updatable = false, unique = true, length = 36)
    private String publicId;

    /**
     * Listing being transacted
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_listing"))
    private Listing listing;

    /**
     * Buyer in this transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_buyer"))
    private User buyer;

    /**
     * Final agreed price
     */
    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    /**
     * Payment method used
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Transaction status
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.PENDING;

    /**
     * Completion timestamp
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Cancellation timestamp
     */
    @Column(name = "cancelled_at")
    private Instant cancelledAt;

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
    public void complete() {
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void cancel() {
        this.status = TransactionStatus.CANCELLED;
        this.cancelledAt = Instant.now();
    }

    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == TransactionStatus.CANCELLED;
    }

    /**
     * Get seller from listing
     */
    public User getSeller() {
        return listing.getSeller();
    }
}
