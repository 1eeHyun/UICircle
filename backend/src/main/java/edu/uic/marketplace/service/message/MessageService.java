package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import edu.uic.marketplace.model.message.Message;

import java.util.Optional;

/**
 * Message management service interface
 */
public interface MessageService {
    
    /**
     * Send message in conversation
     * @param conversationPublicId Conversation ID
     * @param senderUsername Sender user ID
     * @param request Send message request
     * @return Created message response
     */
    MessageResponse sendMessage(String conversationPublicId, String senderUsername, SendMessageRequest request);
    
    /**
     * Get message by ID
     * @param messagePublicId Message ID
     * @return Message entity
     */
    Optional<Message> findById(String messagePublicId, String username);
    
    /**
     * Get messages in conversation
     * @param conversationPublicId Conversation ID
     * @param username Username (for authorization)
     * @param page Page number
     * @param size Page size
     * @return Paginated message responses
     */
    PageResponse<MessageResponse> getMessages(String conversationPublicId, String username, Integer page, Integer size);
    
    /**
     * Mark message as read
     * @param messagePublicId Message ID
     * @param username Username (must be receiver)
     */
    void markAsRead(String messagePublicId, String username);
    
    /**
     * Mark all messages in conversation as read
     * @param conversationPublicId Conversation ID
     * @param username Username (receiver)
     */
    void markConversationAsRead(String conversationPublicId, String username);
    
    /**
     * Delete message (soft delete)
     * @param messagePublicId Message ID
     * @param username Username (must be sender)
     */
    void deleteMessage(String messagePublicId, String username);
    
    /**
     * Get unread message count for user
     * @param username Username
     * @return Total number of unread messages
     */
    Long getUnreadMessageCount(String username);
    
    /**
     * Get unread messages in conversation
     * @param conversationPublicId Conversation ID
     * @param username Username (receiver)
     * @return Number of unread messages
     */
    Long getUnreadCountInConversation(String conversationPublicId, String username);
}
