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
     * @param blockerUsername Blocker username
     * @param blockedUsername Blocked username
     * @return Created block response
     */
    BlockResponse blockUser(String blockerUsername, String blockedUsername);
    
    /**
     * Unblock user
     * @param blockerUsername Blocker username
     * @param blockedUsername Blocked username
     */
    void unblockUser(String blockerUsername, String blockedUsername);
    
    /**
     * Get block relationship
     * @param blockerUsername Blocker username
     * @param blockerUsername Blocked username
     * @return Block entity if exists
     */
    Optional<Block> findByBlockerAndBlocked(String blockerUsername, String blockedUsername);
    
    /**
     * Get list of users blocked by user
     * @param username Blocker user ID
     * @return List of blocked user responses
     */
    List<BlockResponse> getBlockedUsers(String username);
    
    /**
     * Check if user has blocked another user
     * @param blockerUsername Blocker username
     * @param blockedUsername Blocked username
     * @return true if blocked, false otherwise
     */
    boolean isBlocked(String blockerUsername, String blockedUsername);

    /**
     * Get all usernames that have block relationship with user (blocked + blockers)
     */
    List<String> getAllBlockRelatedUsernames(String username);
}
