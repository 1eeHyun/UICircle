package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.response.moderation.BlockResponse;
import edu.uic.marketplace.model.moderation.Block;

import java.util.List;
import java.util.Optional;

/**
 * User blocking service interface
 */
public interface BlockService {
    
    /**
     * Block user
     * @param blockerId Blocker user ID
     * @param blockedId Blocked user ID
     * @return Created block response
     */
    BlockResponse blockUser(Long blockerId, Long blockedId);
    
    /**
     * Unblock user
     * @param blockerId Blocker user ID
     * @param blockedId Blocked user ID
     */
    void unblockUser(Long blockerId, Long blockedId);
    
    /**
     * Get block relationship
     * @param blockerId Blocker user ID
     * @param blockedId Blocked user ID
     * @return Block entity if exists
     */
    Optional<Block> findByBlockerAndBlocked(Long blockerId, Long blockedId);
    
    /**
     * Get list of users blocked by user
     * @param userId Blocker user ID
     * @return List of blocked user responses
     */
    List<BlockResponse> getBlockedUsers(Long userId);
    
    /**
     * Get list of users who blocked this user
     * @param userId Blocked user ID
     * @return List of blocker user responses
     */
    List<BlockResponse> getBlockers(Long userId);
    
    /**
     * Check if user has blocked another user
     * @param blockerId Blocker user ID
     * @param blockedId Blocked user ID
     * @return true if blocked, false otherwise
     */
    boolean isBlocked(Long blockerId, Long blockedId);
    
    /**
     * Check if there is any block relationship between two users
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if either user has blocked the other
     */
    boolean hasBlockRelationship(Long userId1, Long userId2);
    
    /**
     * Get blocked user IDs (for filtering)
     * @param userId User ID
     * @return List of blocked user IDs
     */
    List<Long> getBlockedUserIds(Long userId);
}
