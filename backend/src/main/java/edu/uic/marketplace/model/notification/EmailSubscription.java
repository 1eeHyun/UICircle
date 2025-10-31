package edu.uic.marketplace.model.notification;

import edu.uic.marketplace.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "email_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSubscription {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_email_subscriptions_user"))
    private User user;

    @Column(name = "new_message_email", nullable = false)
    @Builder.Default
    private Boolean newMessageEmail = true;

    @Column(name = "price_change_email", nullable = false)
    @Builder.Default
    private Boolean priceChangeEmail = true;

    @Column(name = "offer_received_email", nullable = false)
    @Builder.Default
    private Boolean offerReceivedEmail = true;

    @Column(name = "listing_sold_email", nullable = false)
    @Builder.Default
    private Boolean listingSoldEmail = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}