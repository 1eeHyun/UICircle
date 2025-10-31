package edu.uic.marketplace.dto.response.notification;

import edu.uic.marketplace.model.notification.EmailSubscription;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSubscriptionResponse {

    private Long userId;
    private Boolean newMessageEmail;
    private Boolean priceChangeEmail;
    private Boolean offerReceivedEmail;
    private Boolean listingSoldEmail;

    public static EmailSubscriptionResponse from(EmailSubscription subscription) {
        return EmailSubscriptionResponse.builder()
                .userId(subscription.getUserId())
                .newMessageEmail(subscription.getNewMessageEmail())
                .priceChangeEmail(subscription.getPriceChangeEmail())
                .offerReceivedEmail(subscription.getOfferReceivedEmail())
                .listingSoldEmail(subscription.getListingSoldEmail())
                .build();
    }
}