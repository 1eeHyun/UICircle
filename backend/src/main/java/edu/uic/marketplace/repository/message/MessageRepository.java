package edu.uic.marketplace.repository.message;

import edu.uic.marketplace.model.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find message by public ID
     */
    Optional<Message> findByPublicId(String publicId);

    /**
     * Find messages by conversation public ID
     */
    Page<Message> findByConversation_PublicId(String conversationPublicId, Pageable pageable);

    /**
     * Prevents N+1 query when loading messages
     */
    @Query("""
           SELECT m FROM Message m
           LEFT JOIN FETCH m.sender
           WHERE m.conversation.publicId = :conversationPublicId
           ORDER BY m.createdAt ASC
           """)
    Page<Message> findByConversation_PublicIdWithSenderOptimized(
            @Param("conversationPublicId") String conversationPublicId,
            Pageable pageable
    );

    /**
     * Find messages by conversation ASC order
     */
    Page<Message> findByConversation_PublicIdOrderByCreatedAtAsc(
            String conversationPublicId,
            Pageable pageable
    );

    /**
     * Count messages in conversation
     */
    Long countByConversation_PublicId(String conversationPublicId);

    /**
     * Count unread messages for user in conversation
     */
    @Query("""
           SELECT COUNT(m) FROM Message m
           WHERE m.conversation.publicId = :conversationPublicId
             AND m.sender.username <> :username
             AND m.readAt IS NULL
           """)
    Long countUnreadInConversation(@Param("conversationPublicId") String conversationPublicId,
                                   @Param("username") String username);

    /**
     * Count total unread messages for user
     */
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE (
            (m.conversation.buyer.username = :username AND m.sender.username = m.conversation.seller.username)
            OR
            (m.conversation.seller.username = :username AND m.sender.username = m.conversation.buyer.username)
        )
        AND m.readAt IS NULL
    """)
    Long countUnreadByUsername(@Param("username") String username);

    /**
     * Mark all messages as read in conversation for user
     */
    @Modifying
    @Query("""
           UPDATE Message m
           SET m.readAt = FUNCTION('NOW')
           WHERE m.conversation.publicId = :conversationPublicId
             AND m.sender.username <> :username
             AND m.readAt IS NULL
           """)
    void markAllAsReadInConversation(@Param("conversationPublicId") String conversationPublicId,
                                     @Param("username") String username);

    /**
     * Delete messages by conversation public ID
     */
    void deleteByConversation_PublicId(String conversationPublicId);

    @Query("""
            SELECT m FROM Message m
            WHERE m.conversation.publicId = :publicId
            ORDER BY m.createdAt DESC
          """)
    Page<Message> findLatestMessages(
            @Param("publicId") String publicId,
            Pageable pageable
    );

    /**
     * This is used to fetch last messages for all conversations in a single query
     */
    @Query("""
            SELECT m FROM Message m
            LEFT JOIN FETCH m.sender
            WHERE m.conversation.publicId IN :conversationIds
              AND m.messageId IN (
                  SELECT MAX(m2.messageId) 
                  FROM Message m2 
                  WHERE m2.conversation.publicId = m.conversation.publicId
              )
            ORDER BY m.createdAt DESC
            """)
    List<Message> findLatestMessagesByConversationIds(
            @Param("conversationIds") List<String> conversationIds,
            Pageable pageable
    );

    /**
     * Find messages by conversation DESC order
     */
    Page<Message> findByConversation_PublicIdOrderByCreatedAtDesc(
            String conversationPublicId,
            Pageable pageable
    );
}
