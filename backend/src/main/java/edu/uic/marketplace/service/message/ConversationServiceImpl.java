package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.CreateConversationRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import edu.uic.marketplace.model.message.Conversation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    @Override
    public ConversationResponse createConversation(Long userId, CreateConversationRequest request) {
        return null;
    }

    @Override
    public Optional<Conversation> findById(Long conversationId) {
        return Optional.empty();
    }

    @Override
    public PageResponse<ConversationResponse> getConversations(Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public Optional<Conversation> findByListingAndUsers(Long listingId, Long user1Id, Long user2Id) {
        return Optional.empty();
    }

    @Override
    public void markAsRead(Long conversationId, Long userId) {

    }

    @Override
    public void updateLastMessageAt(Long conversationId) {

    }

    @Override
    public void incrementUnreadCount(Long conversationId, Long receiverId) {

    }

    @Override
    public Long getUnreadConversationCount(Long userId) {
        return null;
    }

    @Override
    public boolean isParticipant(Long conversationId, Long userId) {
        return false;
    }
}
