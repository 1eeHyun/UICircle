package edu.uic.marketplace.dto.request.transaction;

import edu.uic.marketplace.model.listing.OfferStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOfferStatusRequest {

    @NotNull(message = "Status is required")
    private OfferStatus status;  // ACCEPTED or REJECTED

    private String note;
}