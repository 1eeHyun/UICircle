package edu.uic.marketplace.repository.message;

import edu.uic.marketplace.model.message.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Find conversation by public ID.
     */
    Optional<Conversation> findByPublicId(String publicId);

    /**
     * Find conversation between two users for a listing (using listing public ID and usernames).
     * This assumes the direction (buyer, seller) is given explicitly.
     */
    Optional<Conversation> findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
            String listingPublicId,
            String buyerUsername,
            String sellerUsername
    );

    /**
     * Find conversations for a user (as buyer or seller) using username.
     * NOTE: This does NOT filter out soft-deleted conversations.
     *       For user-facing conversation lists, prefer findVisibleConversations().
     */
    @Query("""
           SELECT c FROM Conversation c
           WHERE c.buyer.username = :username
              OR c.seller.username = :username
           """)
    Page<Conversation> findByUsername(@Param("username") String username, Pageable pageable);

    /**
     * Find conversations by buyer username.
     */
    Page<Conversation> findByBuyer_Username(String buyerUsername, Pageable pageable);

    /**
     * Find conversations by seller username.
     */
    Page<Conversation> findBySeller_Username(String sellerUsername, Pageable pageable);

    /**
     * Find conversations by listing public ID.
     */
    Page<Conversation> findByListing_PublicId(String listingPublicId, Pageable pageable);

    /**
     * Count unread conversations for user (by username),
     * taking into account per-user soft delete flags.
     */
    @Query("""
           SELECT COUNT(c) FROM Conversation c
           WHERE (c.buyer.username = :username
                  AND c.buyerUnreadCount > 0
                  AND c.buyerDeletedAt IS NULL)
              OR (c.seller.username = :username
                  AND c.sellerUnreadCount > 0
                  AND c.sellerDeletedAt IS NULL)
           """)
    Long countUnreadByUsername(@Param("username") String username);

    /**
     * This prevents N+1 queries by fetching buyer, seller, and listing in a single query
     */
    @Query("""
            SELECT DISTINCT c FROM Conversation c
            LEFT JOIN FETCH c.buyer
            LEFT JOIN FETCH c.seller
            LEFT JOIN FETCH c.listing
            WHERE (c.seller.userId = :userId AND c.sellerDeletedAt IS NULL)
               OR (c.buyer.userId = :userId AND c.buyerDeletedAt IS NULL)
            ORDER BY c.lastMessageAt DESC
            """)
    Page<Conversation> findVisibleConversationsOptimized(@Param("userId") Long userId, Pageable pageable);

    /**
     * This is used as a secondary query to fetch last messages for all conversations at once
     */
    @Query("""
            SELECT c.publicId, 
                   (SELECT m FROM Message m 
                    WHERE m.conversation.publicId = c.publicId 
                    ORDER BY m.createdAt DESC 
                    LIMIT 1)
            FROM Conversation c
            WHERE c.publicId IN :conversationIds
            """)
    Page<Object[]> findLastMessagesForConversations(@Param("conversationIds") java.util.List<String> conversationIds, Pageable pageable);
}
