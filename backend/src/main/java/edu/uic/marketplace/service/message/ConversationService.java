package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.CreateConversationRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import edu.uic.marketplace.model.message.Conversation;

import java.util.Optional;

/**
 * Conversation management service interface
 */
public interface ConversationService {
    
    /**
     * Create new conversation
     * @param username Username (buyer)
     * @param request Create conversation request
     * @return Created conversation response
     */
    ConversationResponse createConversation(String username, CreateConversationRequest request);
    
    /**
     * Get conversation by ID
     * @param conversationPublicId Conversation ID
     * @return Conversation entity
     */
    Optional<Conversation> findById(String conversationPublicId, String username);

    ConversationResponse getConversation(String conversationPublicId, String username);
    
    /**
     * Get user's conversations
     * @param username Username
     * @param page Page number
     * @param size Page size
     * @return Paginated conversation responses
     */
    PageResponse<ConversationResponse> getConversations(String username, Integer page, Integer size);
    
    /**
     * Get conversation between two users for a listing
     * @param listingPublicId Listing ID
     * @param username1 First username
     * @param username2 Second username
     * @return Conversation if exists
     */
    Optional<Conversation> findByListingAndUsers(String listingPublicId, String username1, String username2);


    /**
     * Get unread conversation count for user
     * @param username Username
     * @return Number of conversations with unread messages
     */
    Long getUnreadConversationCount(String username);

    void leaveConversation(String conversationPublicId, String username);
}
