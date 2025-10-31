package edu.uic.marketplace.repository.user;

import edu.uic.marketplace.model.user.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    
    /**
     * Find all badges for a user
     */
    List<UserBadge> findByUser_UserId(Long userId);
    
    /**
     * Find user badge by user ID and badge ID
     */
    Optional<UserBadge> findByUser_UserIdAndBadge_BadgeId(Long userId, Long badgeId);
    
    /**
     * Check if user has specific badge
     */
    boolean existsByUser_UserIdAndBadge_BadgeId(Long userId, Long badgeId);
    
    /**
     * Delete all badges for a user
     */
    void deleteByUser_UserId(Long userId);
}
