package edu.uic.marketplace.model.message;

import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "conversations",
        indexes = {
                @Index(name = "idx_conversations_listing_id", columnList = "listing_id"),
                @Index(name = "idx_conversations_seller_id", columnList = "seller_id"),
                @Index(name = "idx_conversations_buyer_id", columnList = "buyer_id"),
                @Index(name = "idx_conversations_last_message_at", columnList = "last_message_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_conversations_listing_buyer_seller", 
                        columnNames = {"listing_id", "buyer_id", "seller_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long conversationId;

    /**
     * Listing that this conversation is about
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false, foreignKey = @ForeignKey(name = "fk_conversations_listing"))
    private Listing listing;

    /**
     * Seller (listing owner)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, foreignKey = @ForeignKey(name = "fk_conversations_seller"))
    private User seller;

    /**
     * Buyer (interested user)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_conversations_buyer"))
    private User buyer;

    /**
     * Unread message count for seller
     */
    @Column(name = "seller_unread_count", nullable = false)
    @Builder.Default
    private Integer sellerUnreadCount = 0;

    /**
     * Unread message count for buyer
     */
    @Column(name = "buyer_unread_count", nullable = false)
    @Builder.Default
    private Integer buyerUnreadCount = 0;

    /**
     * Timestamp of last message
     */
    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Helper Methods
     */
    public void updateLastMessageAt() {
        this.lastMessageAt = Instant.now();
    }

    public void incrementUnreadCountForSeller() {
        this.sellerUnreadCount++;
    }

    public void incrementUnreadCountForBuyer() {
        this.buyerUnreadCount++;
    }

    public void resetUnreadCountForSeller() {
        this.sellerUnreadCount = 0;
    }

    public void resetUnreadCountForBuyer() {
        this.buyerUnreadCount = 0;
    }

    public void resetUnreadCountForUser(Long userId) {
        if (seller.getUserId().equals(userId)) {
            resetUnreadCountForSeller();
        } else if (buyer.getUserId().equals(userId)) {
            resetUnreadCountForBuyer();
        }
    }

    public void incrementUnreadCountForUser(Long userId) {
        if (seller.getUserId().equals(userId)) {
            incrementUnreadCountForSeller();
        } else if (buyer.getUserId().equals(userId)) {
            incrementUnreadCountForBuyer();
        }
    }
}
