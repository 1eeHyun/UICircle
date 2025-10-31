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

    private Long messageId;
    private Long senderId;
    private String body;
    private MessageType messageType;
    private Instant readAt;
    private Instant createdAt;

    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSender().getUserId())
                .body(message.getBody())
                .messageType(message.getMessageType())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }
}