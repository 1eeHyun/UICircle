package edu.uic.marketplace.dto.request.message;

import edu.uic.marketplace.model.message.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {

    @NotBlank(message = "Message body is required")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    private String body;

    @Builder.Default
    private MessageType messageType = MessageType.TEXT;
}