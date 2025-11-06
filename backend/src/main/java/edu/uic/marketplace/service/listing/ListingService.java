package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;

import java.util.List;
import java.util.Optional;

/**
 * Listing management service interface
 */
public interface ListingService {
    
    /**
     * Create new listing
     * @param userId Seller user ID
     * @param request Create listing request
     * @return Created listing response
     */
    ListingResponse createListing(Long userId, CreateListingRequest request);
    
    /**
     * Update listing
     * @param listingId Listing ID
     * @param userId User ID (must be seller)
     * @param request Update listing request
     * @return Updated listing response
     */
    ListingResponse updateListing(Long listingId, Long userId, UpdateListingRequest request);
    
    /**
     * Delete listing (soft delete)
     * @param listingId Listing ID
     * @param userId User ID (must be seller)
     */
    void deleteListing(Long listingId, Long userId);
    
    /**
     * Get listing by ID
     * @param listingId Listing ID
     * @return Listing entity
     */
    Optional<Listing> findById(Long listingId);
    
    /**
     * Get listing detail by ID
     * @param listingId Listing ID
     * @param viewerId User ID viewing the listing (nullable)
     * @return Listing response with full details
     */
    ListingResponse getListingById(Long listingId, Long viewerId);
    
    /**
     * Search listings with filters
     * @param request Search request with filters
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated listing summary responses
     */
    PageResponse<ListingSummaryResponse> searchListings(SearchListingRequest request, Integer page, Integer size);
    
    /**
     * Get nearby listings
     * @param latitude Latitude
     * @param longitude Longitude
     * @param radiusKm Radius in kilometers
     * @param page Page number
     * @param size Page size
     * @return Paginated listing summary responses
     */
    PageResponse<ListingSummaryResponse> getNearbyListings(Double latitude, Double longitude, Double radiusKm, Integer page, Integer size);
    
    /**
     * Get user's listings
     * @param sellerId User ID
     * @param status Listing status filter (nullable)
     * @param page Page number
     * @param size Page size
     * @return Paginated listing summary responses
     */
    PageResponse<ListingSummaryResponse> getUserListings(Long reqUserId, Long sellerId, ListingStatus status, Integer page, Integer size);
    
    /**
     * Change listing status
     * @param listingId Listing ID
     * @param userId User ID (must be seller)
     * @param status New status
     * @return Updated listing response
     */
    ListingResponse updateListingStatus(Long listingId, Long userId, ListingStatus status);
    
    /**
     * Increment view count
     * @param listingId Listing ID
     */
    void incrementViewCount(Long listingId);
    
    /**
     * Update favorite count
     * @param listingId Listing ID
     * @param increment true to increment, false to decrement
     */
    void updateFavoriteCount(Long listingId, boolean increment);
    
    /**
     * Get recommended listings for user
     * @param userId User ID
     * @param limit Number of recommendations
     * @return List of recommended listings
     */
    List<ListingSummaryResponse> getRecommendedListings(Long userId, Integer limit);
}
