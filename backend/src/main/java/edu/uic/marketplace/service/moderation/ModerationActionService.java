package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.request.moderation.ModerationActionRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ModerationActionResponse;
import edu.uic.marketplace.model.moderation.ModerationAction;

import java.util.List;
import java.util.Optional;

/**
 * Moderation action service interface (Admin only)
 */
public interface ModerationActionService {
    
    /**
     * Create moderation action
     * @param adminId Admin user ID
     * @param request Moderation action request
     * @return Created moderation action response
     */
    ModerationActionResponse createAction(Long adminId, ModerationActionRequest request);
    
    /**
     * Get moderation action by ID
     * @param actionId Action ID
     * @return ModerationAction entity
     */
    Optional<ModerationAction> findById(Long actionId);
    
    /**
     * Get all moderation actions
     * @param page Page number
     * @param size Page size
     * @return Paginated moderation action responses
     */
    PageResponse<ModerationActionResponse> getAllActions(Integer page, Integer size);
    
    /**
     * Get actions by admin
     * @param adminId Admin user ID
     * @param page Page number
     * @param size Page size
     * @return Paginated moderation action responses
     */
    PageResponse<ModerationActionResponse> getActionsByAdmin(Long adminId, Integer page, Integer size);
    
    /**
     * Get actions for target
     * @param targetType Target type (e.g., "user", "listing")
     * @param targetId Target ID
     * @return List of moderation actions
     */
    List<ModerationActionResponse> getActionsForTarget(String targetType, Long targetId);
    
    /**
     * Suspend user
     * @param adminId Admin user ID
     * @param userId User ID to suspend
     * @param reason Suspension reason
     * @return Created moderation action response
     */
    ModerationActionResponse suspendUser(Long adminId, Long userId, String reason);
    
    /**
     * Delete listing
     * @param adminId Admin user ID
     * @param listingId Listing ID to delete
     * @param reason Deletion reason
     * @return Created moderation action response
     */
    ModerationActionResponse deleteListing(Long adminId, Long listingId, String reason);
    
    /**
     * Warn user
     * @param adminId Admin user ID
     * @param userId User ID to warn
     * @param reason Warning reason
     * @return Created moderation action response
     */
    ModerationActionResponse warnUser(Long adminId, Long userId, String reason);
    
    /**
     * Get moderation statistics (Admin dashboard)
     * @return Map of action types to counts
     */
    java.util.Map<String, Long> getModerationStatistics();
}
