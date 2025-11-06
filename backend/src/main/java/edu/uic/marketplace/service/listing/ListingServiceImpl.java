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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final FavoriteService favoriteService;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public ListingResponse createListing(Long userId, CreateListingRequest request) {

        // 1) validate user & category
        User user = authValidator.validateUserById(userId);
        Category category = categoryService.findById(request.getCategoryId());

        if (category == null) return null;

        // 2) Build entity
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

        // 3) Save & return
        return ListingResponse.from(listingRepository.save(newList), false);
    }

    @Override
    @Transactional
    public ListingResponse updateListing(Long listingId, Long userId, UpdateListingRequest request) {

        // 1) validate user & listing
        User user = authValidator.validateUserById(userId);
        Listing foundList = listingValidator.validateListing(listingId);
        listingValidator.validateSellerOwnership(user, foundList.getSeller());

        // 2) update
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

        // 1) validate user & listing
        User user = authValidator.validateUserById(userId);
        Listing foundList = listingValidator.validateListing(listingId);
        listingValidator.validateSellerOwnership(user, foundList.getSeller());

        // 2) soft delete
        foundList.softDelete(); // ListingStatus.DELETE + deletedAt
        listingRepository.save(foundList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Listing> findById(Long listingId) {
        return listingRepository.findByListingIdAndDeletedAtIsNullAndStatusNot(listingId, ListingStatus.DELETED);
    }

    @Override
    @Transactional
    public ListingResponse getListingById(Long listingId, Long viewerId) {

        // 1 validate user & listing
        Listing listing = listingValidator.validateListing(listingId);
        authValidator.validateUserById(viewerId);

        boolean isOwner = false;
        isOwner = listing.getSeller() != null
                && listing.getSeller().getUserId() != null
                && listing.getSeller().getUserId().equals(viewerId);

        // 2) if the viewer is not the seller, increase the view count
        if (!isOwner) {
            incrementViewCount(listingId); // @Transactional dirty checking
        }

        // 3) check if the viewer favorites the listing
        boolean isFavorite = favoriteRepository.existsByUser_UserIdAndListing_ListingId(viewerId, listingId);


        // 4) return dto
        return ListingResponse.from(listing, isFavorite);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> searchListings(
            SearchListingRequest request, Integer page, Integer size) {

        return null;
    }

    @Override
    public PageResponse<ListingSummaryResponse> getNearbyListings(
            Double latitude, Double longitude,
            Double radiusKm, Integer page, Integer size) {

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> getUserListings(
            Long reqUserId,
            Long sellerId,
            ListingStatus status,
            Integer page,
            Integer size
    ) {

        // 1) validate users
        User seller = authValidator.validateUserById(sellerId);
        User reqUser = authValidator.validateUserById(reqUserId);

        // 1-based -> 0-based pagination
        int pageIndex = (page == null || page < 1) ? 0 : page - 1;
        int pageSize  = (size == null || size < 1) ? 10 : size;

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 2) fetch listings (filter by status if provided)
        Page<Listing> listings = (status == null)
                ? listingRepository.findBySeller_UserIdAndDeletedAtIsNull(sellerId, pageable):
                 listingRepository.findBySeller_UserIdAndStatusAndDeletedAtIsNull(sellerId, status, pageable);

        // 3) map entity -> DTO
        List<ListingSummaryResponse> content = listings.getContent().stream()
                .map(listing -> {
                    boolean isFavorite = favoriteService.isFavorited(reqUserId, listing.getListingId());
                    return ListingSummaryResponse.from(listing, isFavorite);
                })
                .toList();

        // 4) return paginated response
        return PageResponse.<ListingSummaryResponse>builder()
                .content(content)
                .currentPage(pageIndex + 1)
                .size(pageSize)
                .totalElements(listings.getTotalElements())
                .totalPages(listings.getTotalPages())
                .first(listings.isFirst())
                .last(listings.isLast())
                .empty(listings.isEmpty())
                .build();
    }


    @Override
    public ListingResponse updateListingStatus(Long listingId, Long userId, ListingStatus status) {
        return null;
    }

    @Override
    @Transactional
    public void incrementViewCount(Long listingId) {

        Optional<Listing> listing = listingRepository.findByListingIdAndDeletedAtIsNull(listingId);

        if (listing.isEmpty()) return;

        Listing found = listing.get();
        found.incrementViewCount();
    }

    @Override
    public void updateFavoriteCount(Long listingId, boolean increment) {

    }

    @Override
    public List<ListingSummaryResponse> getRecommendedListings(Long userId, Integer limit) {
        return null;
    }
}
