package edu.uic.marketplace.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateConversationRequest {

    @NotNull(message = "Listing ID is required")
    private Long listingId;

    @NotBlank(message = "Initial message is required")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    private String initialMessage;
}