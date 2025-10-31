package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    @Override
    public ListingResponse createListing(Long userId, CreateListingRequest request) {
        return null;
    }

    @Override
    public ListingResponse updateListing(Long listingId, Long userId, UpdateListingRequest request) {
        return null;
    }

    @Override
    public void deleteListing(Long listingId, Long userId) {

    }

    @Override
    public Optional<Listing> findById(Long listingId) {
        return Optional.empty();
    }

    @Override
    public ListingResponse getListingById(Long listingId, Long viewerId) {
        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> searchListings(SearchListingRequest request, Integer page, Integer size) {
        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> getNearbyListings(Double latitude, Double longitude, Double radiusKm, Integer page, Integer size) {
        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> getUserListings(Long userId, ListingStatus status, Integer page, Integer size) {
        return null;
    }

    @Override
    public ListingResponse updateListingStatus(Long listingId, Long userId, ListingStatus status) {
        return null;
    }

    @Override
    public void incrementViewCount(Long listingId) {

    }

    @Override
    public void updateFavoriteCount(Long listingId, boolean increment) {

    }

    @Override
    public List<ListingSummaryResponse> getRecommendedListings(Long userId, Integer limit) {
        return null;
    }
}
