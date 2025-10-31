package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import edu.uic.marketplace.model.search.ViewHistory;

import java.util.List;
import java.util.Optional;

/**
 * View history management service interface
 */
public interface ViewHistoryService {
    
    /**
     * Record listing view
     * @param userId User ID
     * @param listingId Listing ID
     * @return Created view history
     */
    ViewHistory recordView(Long userId, Long listingId);
    
    /**
     * Get view history by composite ID
     * @param id Composite key (userId + listingId)
     * @return ViewHistory entity
     */
    Optional<ViewHistory> findById(ViewHistory.ViewHistoryId id);
    
    /**
     * Get user's view history
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated view history responses
     */
    PageResponse<ViewHistoryResponse> getUserViewHistory(Long userId, Integer page, Integer size);
    
    /**
     * Get recently viewed listings
     * @param userId User ID
     * @param limit Number of listings to return
     * @return List of listing summaries
     */
    List<ListingSummaryResponse> getRecentlyViewedListings(Long userId, Integer limit);
    
    /**
     * Clear user's view history
     * @param userId User ID
     */
    void clearViewHistory(Long userId);
    
    /**
     * Delete specific view history entry
     * @param userId User ID
     * @param listingId Listing ID
     */
    void deleteViewHistory(Long userId, Long listingId);
    
    /**
     * Check if user has viewed listing
     * @param userId User ID
     * @param listingId Listing ID
     * @return true if viewed, false otherwise
     */
    boolean hasViewed(Long userId, Long listingId);
    
    /**
     * Get view count for listing
     * @param listingId Listing ID
     * @return Number of unique views
     */
    Long getViewCountForListing(Long listingId);
}
