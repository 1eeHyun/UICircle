package edu.uic.marketplace.dto.request.notification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEmailSubscriptionRequest {

    private Boolean newMessageEmail;
    private Boolean priceChangeEmail;
    private Boolean offerReceivedEmail;
    private Boolean listingSoldEmail;
}