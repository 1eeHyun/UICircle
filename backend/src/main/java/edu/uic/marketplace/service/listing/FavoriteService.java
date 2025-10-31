package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.FavoriteResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;

import java.util.List;

/**
 * Favorite management service interface
 */
public interface FavoriteService {
    
    /**
     * Add listing to favorites
     * @param userId User ID
     * @param listingId Listing ID
     * @return FavoriteResponse
     */
    FavoriteResponse addFavorite(Long userId, Long listingId);
    
    /**
     * Remove listing from favorites
     * @param userId User ID
     * @param listingId Listing ID
     */
    void removeFavorite(Long userId, Long listingId);
    
    /**
     * Check if user has favorited listing
     * @param userId User ID
     * @param listingId Listing ID
     * @return true if favorited, false otherwise
     */
    boolean isFavorited(Long userId, Long listingId);
    
    /**
     * Get user's favorite listings
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paginated favorite listings
     */
    PageResponse<ListingSummaryResponse> getUserFavorites(Long userId, Integer page, Integer size);
    
    /**
     * Get favorite count for listing
     * @param listingId Listing ID
     * @return Number of users who favorited this listing
     */
    Long getFavoriteCount(Long listingId);
    
    /**
     * Get user's favorited listing IDs (for quick lookup)
     * @param userId User ID
     * @return List of favorited listing IDs
     */
    List<Long> getUserFavoriteListingIds(Long userId);
}
