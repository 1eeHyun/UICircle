package edu.uic.marketplace.dto.response.message;

import edu.uic.marketplace.model.message.Message;
import edu.uic.marketplace.model.message.MessageType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private String messagePublicId;
    private String senderUsername;
    private String body;
    private MessageType messageType;
    private Instant readAt;
    private Instant createdAt;

    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .messagePublicId(message.getPublicId())
                .senderUsername(message.getSender().getUsername())
                .body(message.getBody())
                .messageType(message.getMessageType())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }
}