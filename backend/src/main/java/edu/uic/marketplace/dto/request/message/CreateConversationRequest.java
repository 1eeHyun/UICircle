package edu.uic.marketplace.dto.request.message;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateConversationRequest {

    @NotNull(message = "Listing publicID is required")
    private String listingPublicId;

    private String initialMessage;
}