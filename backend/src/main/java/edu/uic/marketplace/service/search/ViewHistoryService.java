package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import edu.uic.marketplace.model.search.ViewHistory;

import java.util.List;

/**
 * View history management service interface
 */
public interface ViewHistoryService {
    
    /**
     * Record listing view
     * @param username Username
     * @param listingPublicId Listing Public ID
     * @return Created view history
     */
    ViewHistory recordView(String username, String listingPublicId);

    
    /**
     * Get user's view history
     * @param username Username
     * @param page Page number
     * @param size Page size
     * @return Paginated view history responses
     */
    PageResponse<ViewHistoryResponse> getUserViewHistory(String username, Integer page, Integer size, String sortBy, String sortDirection);
    
    /**
     * Get recently viewed listings
     * @param username Username
     * @param limit Number of listings to return
     * @return List of listing summaries
     */
    List<ListingSummaryResponse> getRecentlyViewedListings(String username, Integer limit);
    
    /**
     * Clear user's view history
     * @param username Username
     */
    void clearViewHistory(String username);
    
    /**
     * Delete specific view history entry
     * @param username Username
     * @param listingPublicId Listing Public ID
     */
    void deleteViewHistory(String username, String listingPublicId);
    
    /**
     * Check if user has viewed listing
     * @param username User ID
     * @param listingPublicId Listing Public ID
     * @return true if viewed, false otherwise
     */
    boolean hasViewed(String username, String listingPublicId);
}
