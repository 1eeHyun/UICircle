package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.repository.listing.FavoriteRepository;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final AuthValidator authValidator;
    private final ListingRepository listingRepository;
//    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;

    private final CategoryValidator categoryValidator;
    private final ListingValidator listingValidator;

    private final FavoriteService favoriteService;

    @Override
    public ListingResponse createListing(String username, CreateListingRequest request) {
        return null;
    }

    @Override
    public ListingResponse updateListing(String publicId, String userPublicId, UpdateListingRequest request) {
        return null;
    }

    @Override
    public void deleteListing(String publicId, String userPublicId) {

    }

    @Override
    public void inactivateListing(String publicId, String userPublicId) {

    }

    @Override
    public void reactivateListing(String publicId, String userPublicId) {

    }

    @Override
    public void markAsSold(String publicId, String userPublicId) {

    }

    @Override
    public ListingResponse getListingByPublicId(String publicId, String viewerPublicId) {
        return null;
    }

    @Override
    public ListingResponse getListingForSeller(String publicId, String sellerPublicId) {
        return null;
    }

    @Override
    public ListingResponse getListingForAdmin(String publicId) {
        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> getAllActiveListings(int page, int size, String sortBy, String sortDirection) {
        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> getListingsByCategory(String categorySlug, int page, int size, String sortBy, String sortDirection) {
        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> getListingsBySeller(String sellerPublicId, ListingStatus status, int page, int size) {
        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> searchListings(SearchListingRequest request) {
        return null;
    }

    @Override
    public List<ListingSummaryResponse> getNearbyListings(Double latitude, Double longitude, Double radiusMiles, String categorySlug) {
        return null;
    }

    @Override
    public void incrementViewCount(String publicId) {

    }

    @Override
    public Long getListingCountBySeller(String sellerPublicId) {
        return null;
    }

    @Override
    public Long getListingCountBySellerAndStatus(String sellerPublicId, ListingStatus status) {
        return null;
    }
}
