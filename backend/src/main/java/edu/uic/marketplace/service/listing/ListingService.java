package edu.uic.marketplace.service.listing;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.NearbyListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.ListingStatus;

/**
 * Service interface for listing operations
 * All methods use publicId for external API calls
 */
public interface ListingService {

    // =================================================================
    // Listing CRUD Operations - Use publicId
    // =================================================================

    /**
     * Create a new listing
     * @param username Username of the seller (from authentication context)
     * @param request Listing creation request
     * @return Created listing response
     */
    ListingResponse createListing(String username, CreateListingRequest request, List<MultipartFile> images);

    /**
     * Update an existing listing
     * @param publicId Public ID of the listing
     * @param username Public ID of the current user
     * @param request Listing update request
     * @param images List of MultipartFile
     * @return Updated listing response
     */
    ListingResponse updateListing(String publicId, String username, UpdateListingRequest request, List<MultipartFile> images);

    /**
     * Soft delete a listing
     * @param publicId Public ID of the listing
     * @param username Public ID of the current user (seller)
     */
    void deleteListing(String publicId, String username);

    /**
     * Inactivate a listing (seller temporarily hides it)
     * @param publicId Public ID of the listing
     * @param username Public ID of the current user (seller)
     */
    void inactivateListing(String publicId, String username);

    /**
     * Reactivate an inactive listing
     * @param publicId Public ID of the listing
     * @param username Public ID of the current user (seller)
     */
    void reactivateListing(String publicId, String username);

    /**
     * Mark listing as sold
     * @param publicId Public ID of the listing
     * @param username Public ID of the current user (seller)
     */
    void markAsSold(String publicId, String username);

    // =================================================================
    // Listing Retrieval - Use publicId
    // =================================================================

    /**
     * Get listing by public ID for public view
     * Increments view count
     * @param publicId Public ID of the listing
     * @param username Public ID of the viewer (optional, for favorite status)
     * @return Listing response
     */
    ListingResponse getListingByPublicId(String publicId, String username);

    /**
     * Get listing by public ID for seller view
     * @param publicId Public ID of the listing
     * @param sellerUsername Public ID of the seller
     * @return Listing response
     */
    ListingResponse getListingForSeller(String publicId, String sellerUsername);

    /**
     * Get listing by public ID for admin view
     * @param publicId Public ID of the listing
     * @return Listing response
     */
    ListingResponse getListingForAdmin(String publicId);

    // =================================================================
    // Listing Search and Browse - Use publicId and slug
    // =================================================================

    /**
     * Get all active listings (public feed)
     * @param username who requested this
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sortBy Sort field (e.g., "createdAt", "price")
     * @param sortDirection Sort direction ("asc" or "desc")
     * @return Paginated listing summary responses
     */
    PageResponse<ListingSummaryResponse> getAllActiveListings(
            String username, int page, int size, String sortBy, String sortDirection);

    /**
     * Get listings by category slug
     * @param username who requested this
     * @param categorySlug Category slug
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @param sortDirection Sort direction
     * @return Paginated listing summary responses
     */
    PageResponse<ListingSummaryResponse> getListingsByCategory(
            String username, String categorySlug, int page, int size, String sortBy, String sortDirection);

    /**
     * Get listings by seller's public ID
     * @param sellerUsername Seller's public ID
     * @param status Listing status filter (optional)
     * @param page Page number
     * @param size Page size
     * @return Paginated listing summary responses
     */
    PageResponse<ListingSummaryResponse> getListingsBySeller(
            String sellerUsername, ListingStatus status, int page, int size);

    /**
     * Get listings by seller's public ID with privacy check
     * @param sellerPublicId Seller's profile public ID
     * @param viewerUsername Viewer's username (for privacy check)
     * @param status Listing status filter (optional)
     * @param page Page number
     * @param size Page size
     * @return Paginated listing summary responses, or empty if viewer cannot see
     */
    PageResponse<ListingSummaryResponse> getListingsBySellerWithPrivacyCheck(
            String sellerPublicId, String viewerUsername, ListingStatus status, int page, int size);

    /**
     * Search listings by keyword
     * @param request Search request with filters
     * @return Paginated listing summary responses
     */
    PageResponse<ListingSummaryResponse> searchListings(SearchListingRequest request);

    /**
     * Get nearby listings within radius
     * @param request
     * @return List of nearby listings
     */
    List<ListingSummaryResponse> getNearbyListings(NearbyListingRequest request);

    // =================================================================
    // Listing Statistics - Use publicId
    // =================================================================

    /**
     * Increment view count for a listing
     * @param publicId Public ID of the listing
     */
    void incrementViewCount(String publicId);

    /**
     * Get total listing count by seller
     * @param sellerPublicId Seller's public ID
     * @return Total listing count
     */
    Long getListingCountBySeller(String sellerPublicId);

    /**
     * Get listing count by seller and status
     * @param sellerPublicId Seller's public ID
     * @param status Listing status
     * @return Listing count for the given status
     */
    Long getListingCountBySellerAndStatus(String sellerPublicId, ListingStatus status);
}
