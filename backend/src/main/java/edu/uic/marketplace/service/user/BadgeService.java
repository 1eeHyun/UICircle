package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.BadgeResponse;
import edu.uic.marketplace.model.user.Badge;

import java.util.List;
import java.util.Optional;

/**
 * Badge management service interface
 */
public interface BadgeService {
    
    /**
     * Get all badges
     * @return List of all badges
     */
    List<Badge> findAll();
    
    /**
     * Find badge by code
     * @param code Badge code
     * @return Badge entity
     */
    Optional<Badge> findByCode(String code);
    
    /**
     * Get user's badges
     * @param userId User ID
     * @return List of user's badges
     */
    List<BadgeResponse> getUserBadges(Long userId);
    
    /**
     * Award badge to user
     * @param userId User ID
     * @param badgeCode Badge code
     * @return BadgeResponse
     */
    BadgeResponse awardBadge(Long userId, String badgeCode);
    
    /**
     * Check if user has badge
     * @param userId User ID
     * @param badgeCode Badge code
     * @return true if user has badge, false otherwise
     */
    boolean userHasBadge(Long userId, String badgeCode);
    
    /**
     * Auto-award badges based on user activity
     * Called after certain actions (e.g., first sale, 10 sales, etc.)
     * @param userId User ID
     */
    void checkAndAwardAutoBadges(Long userId);
}
