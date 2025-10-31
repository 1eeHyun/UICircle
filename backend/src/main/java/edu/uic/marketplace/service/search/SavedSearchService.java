package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;
import edu.uic.marketplace.model.search.SavedSearch;

import java.util.List;
import java.util.Optional;

/**
 * Saved search management service interface
 */
public interface SavedSearchService {
    
    /**
     * Save search query
     * @param userId User ID
     * @param request Save search request
     * @return Created saved search response
     */
    SavedSearchResponse saveSearch(Long userId, SaveSearchRequest request);
    
    /**
     * Get saved search by ID
     * @param savedSearchId Saved search ID
     * @return SavedSearch entity
     */
    Optional<SavedSearch> findById(Long savedSearchId);
    
    /**
     * Get user's saved searches
     * @param userId User ID
     * @return List of saved search responses
     */
    List<SavedSearchResponse> getUserSavedSearches(Long userId);
    
    /**
     * Delete saved search
     * @param savedSearchId Saved search ID
     * @param userId User ID
     */
    void deleteSavedSearch(Long savedSearchId, Long userId);
    
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
     * @param userId User ID
     * @param queryHash Hash of query parameters
     * @return true if already saved, false otherwise
     */
    boolean hasSavedSearch(Long userId, String queryHash);
}
