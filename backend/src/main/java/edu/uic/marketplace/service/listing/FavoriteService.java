package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;

import java.util.List;

/**
 * Favorite management service interface
 */
public interface FavoriteService {
    
    /**
     * Add/remove listing to favorites
     * @param username User ID
     * @param listingPublicId Listing ID
     */
    void toggleFavorite(String username, String listingPublicId);
    
    /**
     * Check if user has favorited listing
     * @param username User ID
     * @param listingPublicId Listing ID
     * @return true if favorited, false otherwise
     */
    boolean isFavorited(String username, String listingPublicId);
    
    /**
     * Get user's favorite listings
     * @param username User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated favorite listings
     */
    PageResponse<ListingSummaryResponse> getUserFavorites(String username, Integer page, Integer size);
    
    /**
     * Get favorite count for listing
     * @param listingPublicId Listing ID
     * @return Number of users who favorited this listing
     */
    Integer getFavoriteCount(String listingPublicId);
    
    /**
     * Get user's favorited listing IDs (for quick lookup)
     * @param username User ID
     * @return List of favorited listing IDs
     */
    List<String> getUserFavoriteListingPublicIds(String username);

    /**
     * Check multiple listings at once (BATCH OPERATION for performance)
     * @param username User ID
     * @param listingPublicIds List of listing IDs to check
     * @return Set of favorited listing IDs
     */
    java.util.Set<String> getFavoritedListingIds(String username, List<String> listingPublicIds);
}
