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
     * Find conversation between two users for a listing
     */
    Optional<Conversation> findByListing_ListingIdAndBuyer_UserIdAndSeller_UserId(
            Long listingId, Long buyerId, Long sellerId);
    
    /**
     * Find conversations for a user (as buyer or seller)
     */
    @Query("SELECT c FROM Conversation c WHERE c.buyer.userId = :userId OR c.seller.userId = :userId")
    Page<Conversation> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Find conversations by buyer
     */
    Page<Conversation> findByBuyer_UserId(Long buyerId, Pageable pageable);
    
    /**
     * Find conversations by seller
     */
    Page<Conversation> findBySeller_UserId(Long sellerId, Pageable pageable);
    
    /**
     * Find conversations by listing
     */
    Page<Conversation> findByListing_ListingId(Long listingId, Pageable pageable);
    
    /**
     * Count unread conversations for user
     */
    @Query("""
           SELECT COUNT(c) FROM Conversation c 
           WHERE 
               c.buyer.userId = :userId AND c.buyerUnreadCount > 0 
               OR
               c.seller.userId = :userId AND c.sellerUnreadCount > 0
           """
    )
    Long countUnreadByUserId(@Param("userId") Long userId);
}
