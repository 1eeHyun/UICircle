package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;

import java.util.List;

/**
 * Saved search management service interface
 */
public interface SavedSearchService {
    
    /**
     * Save search query
     * @param username Username
     * @param request Save search request
     * @return Created saved search response
     */
    SavedSearchResponse saveSearch(String username, SaveSearchRequest request);
    
    /**
     * Get user's saved searches
     * @param username User username
     * @return List of saved search responses
     */
    List<SavedSearchResponse> getUserSavedSearchesByUsername(String username);
    
    /**
     * Delete saved search
     * @param savedSearchId Saved search ID
     * @param username User usernmae
     */
    void deleteSavedSearch(String savedSearchId, String username);

    void deleteAllForUser(String username);
    
    /**
     * Update saved search name
     * @param savedSearchId Saved search ID
     * @param userId User ID
     * @param name New name
     * @return Updated saved search response
     */
    SavedSearchResponse updateSearchName(Long savedSearchId, Long userId, String name);
    
    /**
     * Check if user has saved this search query
     * @param username User username
     * @param queryHash Hash of query parameters
     * @return true if already saved, false otherwise
     */
    boolean hasSavedSearchByUsername(String username, String queryHash);
}
