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
     * @param userId User ID (buyer)
     * @param request Create conversation request
     * @return Created conversation response
     */
    ConversationResponse createConversation(Long userId, CreateConversationRequest request);
    
    /**
     * Get conversation by ID
     * @param conversationId Conversation ID
     * @return Conversation entity
     */
    Optional<Conversation> findById(Long conversationId);
    
    /**
     * Get user's conversations
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated conversation responses
     */
    PageResponse<ConversationResponse> getConversations(Long userId, Integer page, Integer size);
    
    /**
     * Get conversation between two users for a listing
     * @param listingId Listing ID
     * @param user1Id First user ID
     * @param user2Id Second user ID
     * @return Conversation if exists
     */
    Optional<Conversation> findByListingAndUsers(Long listingId, Long user1Id, Long user2Id);
    
    /**
     * Mark conversation as read by user
     * @param conversationId Conversation ID
     * @param userId User ID
     */
    void markAsRead(Long conversationId, Long userId);
    
    /**
     * Update last message timestamp
     * @param conversationId Conversation ID
     */
    void updateLastMessageAt(Long conversationId);
    
    /**
     * Increment unread count for receiver
     * @param conversationId Conversation ID
     * @param receiverId Receiver user ID
     */
    void incrementUnreadCount(Long conversationId, Long receiverId);
    
    /**
     * Get unread conversation count for user
     * @param userId User ID
     * @return Number of conversations with unread messages
     */
    Long getUnreadConversationCount(Long userId);
    
    /**
     * Check if user is participant in conversation
     * @param conversationId Conversation ID
     * @param userId User ID
     * @return true if participant, false otherwise
     */
    boolean isParticipant(Long conversationId, Long userId);
}
