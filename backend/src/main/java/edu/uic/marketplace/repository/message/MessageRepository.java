package edu.uic.marketplace.repository.message;

import edu.uic.marketplace.model.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * Find messages by conversation
     */
    Page<Message> findByConversation_ConversationId(Long conversationId, Pageable pageable);
    
    /**
     * Find messages by conversation ordered by created date
     */
    Page<Message> findByConversation_ConversationIdOrderByCreatedAtDesc(
            Long conversationId, Pageable pageable);

    Long countByConversation_ConversationId(Long conversationId);
    
    /**
     * Count unread messages for user in conversation
     */
    @Query("""
           SELECT COUNT(m) FROM Message m 
           WHERE
               m.conversation.conversationId = :conversationId AND
               m.sender.userId != :userId AND
               m.readAt IS NULL
           """
    )
    Long countUnreadInConversation(Long conversationId, @Param("userId") Long userId);
    
    /**
     * Count total unread messages for user
     */
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE (
            (m.conversation.buyer.userId = :userId AND m.sender.userId = m.conversation.seller.userId)
            OR
            (m.conversation.seller.userId = :userId AND m.sender.userId = m.conversation.buyer.userId)
        )
        AND m.readAt IS NULL
    """)
    Long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * Mark all messages as read in conversation for user
     */
    @Modifying
    @Query("""
           UPDATE Message m
           SET m.readAt = FUNCTION('NOW')
           WHERE m.conversation.conversationId = :conversationId
           AND m.sender.userId != :userId
           AND m.readAt IS NULL
           """
    )
    void markAllAsReadInConversation(@Param("conversationId") Long conversationId,
                                     @Param("userId") Long userId);

    /**
     * Delete messages by conversation
     */
    void deleteByConversation_ConversationId(Long conversationId);


}
