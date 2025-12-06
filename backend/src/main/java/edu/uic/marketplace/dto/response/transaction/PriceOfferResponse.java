package edu.uic.marketplace.dto.response.transaction;

import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.listing.OfferStatus;
import edu.uic.marketplace.model.transaction.PriceOffer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceOfferResponse {

    private String publicId;
    private ListingSummaryResponse listing;
    private UserResponse buyer;
    private BigDecimal amount;
    private String message;
    private OfferStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private String transactionPublicId;

    public static PriceOfferResponse from(PriceOffer offer) {
        return PriceOfferResponse.builder()
                .publicId(offer.getPublicId())
                .listing(ListingSummaryResponse.from(offer.getListing()))
                .buyer(UserResponse.from(offer.getBuyer()))
                .amount(offer.getAmount())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }

    public static PriceOfferResponse from(PriceOffer offer, String transactionPublicId) {
        return PriceOfferResponse.builder()
                .publicId(offer.getPublicId())
                .listing(ListingSummaryResponse.from(offer.getListing()))
                .buyer(UserResponse.from(offer.getBuyer()))
                .amount(offer.getAmount())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .transactionPublicId(transactionPublicId)
                .build();
    }
}