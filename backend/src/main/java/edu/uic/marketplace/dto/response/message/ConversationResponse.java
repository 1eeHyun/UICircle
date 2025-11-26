package edu.uic.marketplace.dto.response.message;

import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.user.User;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {

    private String conversationPublicId;
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
                .conversationPublicId(conversation.getPublicId())
                .listing(ListingSummaryResponse.from(conversation.getListing()))
                .otherUser(otherUser)
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    public static ConversationResponse fromForUser(Conversation conversation, User currentUser) {

        // determine other user
        User other = conversation.getSeller().equals(currentUser)
                ? conversation.getBuyer()
                : conversation.getSeller();

        int unread = conversation.getSeller().equals(currentUser)
                ? conversation.getSellerUnreadCount()
                : conversation.getBuyerUnreadCount();

        return ConversationResponse.builder()
                .conversationPublicId(conversation.getPublicId())
                .listing(ListingSummaryResponse.from(conversation.getListing()))
                .otherUser(UserResponse.from(other))
                .lastMessage(null)  // new conversation
                .unreadCount(unread)
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .build();
    }

}