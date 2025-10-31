package edu.uic.marketplace.dto.response.message;

import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.message.Conversation;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {

    private Long conversationId;
    private ListingSummaryResponse listing;
    private UserResponse otherUser;
    private MessageResponse lastMessage;
    private Integer unreadCount;
    private Instant lastMessageAt;
    private Instant createdAt;

    public static ConversationResponse from(Conversation conversation,
                                            UserResponse otherUser,
                                            MessageResponse lastMessage,
                                            Integer unreadCount) {
        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .listing(ListingSummaryResponse.from(conversation.getListing()))
                .otherUser(otherUser)
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}