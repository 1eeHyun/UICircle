package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.FavoriteRepository;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final AuthValidator authValidator;
    private final ListingRepository listingRepository;
//    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;

    private final CategoryValidator categoryValidator;
    private final ListingValidator listingValidator;

    @Override
    @Transactional
    public ListingResponse createListing(Long userId, CreateListingRequest request) {

        // Step 1: validate user & category
        User user = authValidator.validateUserById(userId);
        Category category = categoryValidator.validateCategoryExists(request.getCategoryId());

        // Step 2: Build entity
        Listing newList = Listing.builder()
                .title(request.getTitle())
                .seller(user)
                .description(request.getDescription())
                .category(category)
                .condition(request.getCondition())
                .price(request.getPrice())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isNegotiable(request.getIsNegotiable())
                .build();

        // Step 3: Save & return
        return ListingResponse.from(listingRepository.save(newList), false);
    }

    @Override
    @Transactional
    public ListingResponse updateListing(Long listingId, Long userId, UpdateListingRequest request) {

        // Step 1: validate user & listing
        User user = authValidator.validateUserById(userId);
        Listing foundList = listingValidator.validateListing(listingId);
        listingValidator.validateSellerOwnership(user, foundList.getSeller());

        // Step 2: update
        foundList.setTitle(request.getTitle());
        foundList.setDescription(request.getDescription());
        foundList.setCondition(request.getCondition());
        foundList.setPrice(request.getPrice());
        foundList.setStatus(request.getStatus());
        foundList.setIsNegotiable(request.getIsNegotiable());

        boolean isFavorite = favoriteRepository
                .existsByUser_UserIdAndListing_ListingId(userId, listingId);

        return ListingResponse.from(foundList, isFavorite);
    }

    @Override
    @Transactional
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
