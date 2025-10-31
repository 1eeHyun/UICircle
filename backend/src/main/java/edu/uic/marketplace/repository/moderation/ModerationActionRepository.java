package edu.uic.marketplace.repository.moderation;

import edu.uic.marketplace.model.moderation.ModerationAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModerationActionRepository extends JpaRepository<ModerationAction, Long> {
    
    /**
     * Find actions by admin
     */
    Page<ModerationAction> findByAdmin_UserId(Long adminId, Pageable pageable);
    
    /**
     * Find actions by target
     */
    List<ModerationAction> findByTargetTypeAndTargetId(String targetType, Long targetId);
    
    /**
     * Find actions by action type
     */
    Page<ModerationAction> findByActionType(String actionType, Pageable pageable);
    
    /**
     * Count actions by admin
     */
    Long countByAdmin_UserId(Long adminId);
    
    /**
     * Count actions by action type
     */
    Long countByActionType(String actionType);
    
    /**
     * Get moderation statistics
     */
    @Query("SELECT m.actionType, COUNT(m) FROM ModerationAction m GROUP BY m.actionType")
    List<Object[]> getModerationStatistics();
    
    /**
     * Delete actions by target
     */
    void deleteByTargetTypeAndTargetId(String targetType, Long targetId);
}
