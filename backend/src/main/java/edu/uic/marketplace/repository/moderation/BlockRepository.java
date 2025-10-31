package edu.uic.marketplace.repository.moderation;

import edu.uic.marketplace.model.moderation.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    
    /**
     * Find block relationship
     */
    Optional<Block> findByBlocker_UserIdAndBlocked_UserId(Long blockerId, Long blockedId);
    
    /**
     * Find users blocked by user
     */
    List<Block> findByBlocker_UserId(Long blockerId);
    
    /**
     * Find users who blocked this user
     */
    List<Block> findByBlocked_UserId(Long blockedId);
    
    /**
     * Check if user is blocked
     */
    boolean existsByBlocker_UserIdAndBlocked_UserId(Long blockerId, Long blockedId);
    
    /**
     * Check if there's any block relationship between two users
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Block b WHERE " +
           "(b.blocker.userId = :userId1 AND b.blocked.userId = :userId2) OR " +
           "(b.blocker.userId = :userId2 AND b.blocked.userId = :userId1)")
    boolean hasBlockRelationship(Long userId1, Long userId2);
    
    /**
     * Get blocked user IDs
     */
    @Query("SELECT b.blocked.userId FROM Block b WHERE b.blocker.userId = :userId")
    List<Long> findBlockedUserIds(Long userId);
    
    /**
     * Count blocks by blocker
     */
    Long countByBlocker_UserId(Long blockerId);
    
    /**
     * Delete block relationship
     */
    void deleteByBlocker_UserIdAndBlocked_UserId(Long blockerId, Long blockedId);
    
    /**
     * Delete all blocks by user
     */
    void deleteByBlocker_UserId(Long blockerId);
}
