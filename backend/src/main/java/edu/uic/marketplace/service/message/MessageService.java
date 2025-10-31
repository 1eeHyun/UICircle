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
     * @param conversationId Conversation ID
     * @param senderId Sender user ID
     * @param request Send message request
     * @return Created message response
     */
    MessageResponse sendMessage(Long conversationId, Long senderId, SendMessageRequest request);
    
    /**
     * Get message by ID
     * @param messageId Message ID
     * @return Message entity
     */
    Optional<Message> findById(Long messageId);
    
    /**
     * Get messages in conversation
     * @param conversationId Conversation ID
     * @param userId User ID (for authorization)
     * @param page Page number
     * @param size Page size
     * @return Paginated message responses
     */
    PageResponse<MessageResponse> getMessages(Long conversationId, Long userId, Integer page, Integer size);
    
    /**
     * Mark message as read
     * @param messageId Message ID
     * @param userId User ID (must be receiver)
     */
    void markAsRead(Long messageId, Long userId);
    
    /**
     * Mark all messages in conversation as read
     * @param conversationId Conversation ID
     * @param userId User ID (receiver)
     */
    void markConversationAsRead(Long conversationId, Long userId);
    
    /**
     * Delete message (soft delete)
     * @param messageId Message ID
     * @param userId User ID (must be sender)
     */
    void deleteMessage(Long messageId, Long userId);
    
    /**
     * Get unread message count for user
     * @param userId User ID
     * @return Total number of unread messages
     */
    Long getUnreadMessageCount(Long userId);
    
    /**
     * Get unread messages in conversation
     * @param conversationId Conversation ID
     * @param userId User ID (receiver)
     * @return Number of unread messages
     */
    Long getUnreadCountInConversation(Long conversationId, Long userId);
}
