package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.common.util.PageMapper;
import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.NearbyListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingImage;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.service.common.S3Service;
import edu.uic.marketplace.service.common.Utils;
import edu.uic.marketplace.service.search.ViewHistoryService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    // repositories
    private final ListingRepository listingRepository;

    // validators
    private final ListingValidator listingValidator;
    private final AuthValidator authValidator;
    private final CategoryValidator categoryValidator;

    // services
    private final S3Service s3Service;
    private final FavoriteService favoriteService;
    private final ViewHistoryService viewHistoryService;

    @Override
    @Transactional
    public ListingResponse createListing(String username, CreateListingRequest request, List<MultipartFile> images) {

        // 1) validate user
        User seller = authValidator.validateUserByUsername(username);

        // 2) validate category (must be a leaf)
        Category category = categoryValidator.validateLeafCategory(request.getCategorySlug());

        // 3) build entity (publicId handled by @PrePersist)
        Listing listing = Listing.builder()
                .seller(seller)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .condition(request.getCondition())
                .category(category)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isNegotiable(Boolean.TRUE.equals(request.getIsNegotiable()))
                .status(ListingStatus.ACTIVE)
                .viewCount(0)
                .favoriteCount(0)
                .build();

        // 4) upload images + attach to listing (best-effort rollback on failure)
        List<String> uploadedUrls = new ArrayList<>();
        try {
            attachImages(listing, images, uploadedUrls);

            // 5) save
            Listing saved = listingRepository.save(listing);

            // 6) map to response (newly created -> not favorited yet)
            return ListingResponse.from(saved, false);

        } catch (RuntimeException e) {
            cleanupUploaded(uploadedUrls); // compensate S3 uploads
            throw e;
        }
    }

    @Override
    @Transactional
    public ListingResponse updateListing(String publicId, String username, UpdateListingRequest request, List<MultipartFile> images) {

        // 1) validate
        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(publicId);

        listingValidator.validateSellerOwnership(user, listing.getSeller());

        // 2) update
        if (request.getTitle() != null) listing.setTitle(request.getTitle());
        if (request.getDescription() != null) listing.setDescription(request.getDescription());
        if (request.getPrice() != null) listing.setPrice(request.getPrice());
        if (request.getCondition() != null) listing.setCondition(request.getCondition());
        if (request.getStatus() != null) listing.setStatus(request.getStatus());
        if (request.getIsNegotiable() != null) listing.setIsNegotiable(request.getIsNegotiable());

        // 3) update images: image==null : nothing, images==empty: remove all, otherwise: change all
        if (images != null) {

            List<String> oldUrls = listing.getImages().stream()
                    .map(ListingImage::getImageUrl)
                    .toList();

            if (!oldUrls.isEmpty()) {
                try {
                    s3Service.deleteFiles(oldUrls);
                } catch (Exception ignore) {
                }
            }

            listing.getImages().clear();

            // upload new images & connect
            List<String> uploaded = new ArrayList<>();
            try {

                int order = 0;
                for (MultipartFile f : images) {
                    if (f == null || f.isEmpty()) continue;
                    String url = s3Service.upload(f);
                    uploaded.add(url);
                    listing.getImages().add(ListingImage.builder()
                            .listing(listing)
                            .imageUrl(url)
                            .displayOrder(order++)
                            .build());
                }
            } catch (RuntimeException e) {

                // fail to upload
                for (String u : uploaded) {
                    try {
                        s3Service.deleteByUrl(u);
                    } catch (Exception ignore) {
                    }
                }
                throw e;
            }
        }

        // 4) dirty checking
        return ListingResponse.from(listing, false);
    }

    @Override
    @Transactional
    public void deleteListing(String publicId, String username) {

        // 1) validate
        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(publicId);
        listingValidator.validateSellerOwnership(user, listing.getSeller());

        // 2) soft delete
        Instant now = Instant.now();
        listing.setStatus(ListingStatus.DELETED);
        listing.setDeletedAt(now);

        listingRepository.softDelete(publicId, now);
    }

    @Override
    @Transactional
    public void inactivateListing(String publicId, String username) {

        // 1) validate
        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(publicId);
        listingValidator.validateSellerOwnership(user, listing.getSeller());

        // 2) validate listing is activate
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE listing can be inactivated.");
        }

        // 3) inactivate
        listing.setStatus(ListingStatus.INACTIVE);
//        listingRepository.updateStatus(publicId, ListingStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void reactivateListing(String publicId, String username) {

        // 1) validate
        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(publicId);
        listingValidator.validateSellerOwnership(user, listing.getSeller());

        // 2) validate listing is inactivate
        if (listing.getStatus() != ListingStatus.INACTIVE)
            throw new IllegalStateException("Only INACTIVE listing can be ACTIVE.");

        // 3) reactivate
        listing.setStatus(ListingStatus.ACTIVE);
//        listingRepository.updateStatus(publicId, ListingStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void markAsSold(String publicId, String username) {

        // 1) validate
        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(publicId);
        listingValidator.validateSellerOwnership(user, listing.getSeller());

        // 2) validate listing is already sold
        if (listing.getStatus() == ListingStatus.SOLD) return;

        if (listing.getStatus() != ListingStatus.ACTIVE)
            throw new IllegalStateException("Only ACTIVE listing can be marked as SOLD.");

        // 3) mark as sold
        listing.setStatus(ListingStatus.SOLD);
//        listingRepository.updateStatus(publicId, ListingStatus.SOLD);
    }

    @Override
    @Transactional
    public ListingResponse getListingByPublicId(String publicId, String username) {

        // 1) Fetch listing with all details
        Listing listing = listingRepository.findActiveByPublicIdWithDetails(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found or not active"));

        // 2) Get user
        User user = authValidator.validateUserByUsername(username);

        // 3) Check if favorited
        boolean isFavorite = favoriteService.isFavorited(username, publicId);

        // 4) Increment view count asynchronously
        incrementViewCountIfNotSellerAsync(publicId, user.getUserId());

        return ListingResponse.from(listing, isFavorite);
    }

    /**
     * Increment view count asynchronously if viewer is not the seller
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void incrementViewCountIfNotSellerAsync(String publicId, Long userId) {
        listingRepository.incrementViewCountIfNotSeller(publicId, userId);
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
    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> getAllActiveListings(String username, int page, int size, String sortBy, String sortDirection) {

        Pageable pageable = Utils.buildPageable(page, size, sortBy, sortDirection);

        Page<Listing> result = listingRepository.findByStatusWithDetails(ListingStatus.ACTIVE, pageable);

        // Batch favorite check
        List<String> listingIds = result.getContent().stream()
                .map(Listing::getPublicId)
                .toList();

        java.util.Set<String> favoritedIds = favoriteService.getFavoritedListingIds(username, listingIds);

        List<ListingSummaryResponse> content = result.getContent().stream()
                .map(listing -> {
                    boolean isFavorite = favoritedIds.contains(listing.getPublicId());
                    return ListingSummaryResponse.from(listing, isFavorite);
                })
                .toList();

        return PageMapper.toPageResponse(result, content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> getListingsByCategory(String username, String categorySlug, int page, int size, String sortBy, String sortDirection) {

        categoryValidator.validateLeafCategory(categorySlug);

        Pageable pageable = Utils.buildPageable(page, size, sortBy, sortDirection);

        Page<Listing> result = listingRepository.findByCategoryWithDetails(
                categorySlug, ListingStatus.ACTIVE, pageable);

        // Batch favorite check
        List<String> listingIds = result.getContent().stream()
                .map(Listing::getPublicId)
                .toList();

        java.util.Set<String> favoritedIds = favoriteService.getFavoritedListingIds(username, listingIds);

        List<ListingSummaryResponse> content = result.getContent().stream()
                .map(listing -> {
                    boolean isFavorite = favoritedIds.contains(listing.getPublicId());
                    return ListingSummaryResponse.from(listing, isFavorite);
                })
                .toList();

        return PageMapper.toPageResponse(result, content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> getListingsBySeller(String sellerUsername, ListingStatus status, int page, int size) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> searchListings(SearchListingRequest request) {

        // Validate price range
        if (request.getMinPrice() != null && request.getMaxPrice() != null
                && request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            throw new IllegalArgumentException("minPrice must be <= maxPrice");
        }

        int page = (request.getPage() == null || request.getPage() < 1) ? 0 : request.getPage() - 1;
        int size = (request.getSize() == null || request.getSize() < 1) ? 20 : request.getSize();

        String sortBy = (request.getSortBy() == null || request.getSortBy().isBlank()) ? "createdAt" : request.getSortBy();
        String sortDir = (request.getSortOrder() != null && request.getSortOrder().equalsIgnoreCase("asc")) ? "ASC" : "DESC";
        Pageable pageable = Utils.buildPageable(page, size, sortBy, sortDir);

        // when keyword exists to prevent N+1
        Page<Listing> pageResult;

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            if (request.getCategorySlug() != null && !request.getCategorySlug().isBlank()) {
                pageResult = listingRepository.searchByKeywordAndCategoryWithDetails(
                        request.getKeyword(),
                        request.getCategorySlug(),
                        List.of(request.getStatus()),
                        pageable
                );
            } else {
                pageResult = listingRepository.searchByKeywordWithDetails(
                        request.getKeyword(),
                        request.getStatus(),
                        pageable
                );
            }
        } else {
            // Fallback to Specification for complex filters
            Specification<Listing> spec = (root, q, cb) -> cb.and(
                    cb.isNull(root.get("deletedAt")),
                    cb.equal(root.get("status"), request.getStatus())
            );

            // Category
            if (request.getCategorySlug() != null && !request.getCategorySlug().isBlank()) {
                spec = spec.and((root, q, cb) -> cb.equal(root.get("category").get("slug"), request.getCategorySlug()));
            }

            // Condition
            if (request.getCondition() != null) {
                spec = spec.and((root, q, cb) -> cb.equal(root.get("condition"), request.getCondition()));
            }

            // Price
            if (request.getMinPrice() != null) {
                spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            pageResult = listingRepository.findAll(spec, pageable);
        }

        // Get username for favorite check (optional - may not be logged in)
        String username = null;
        try {
            username = authValidator.extractUsername();
        } catch (Exception e) {
            // Not logged in - skip favorite check
        }

        // Batch favorite check if user is logged in
        java.util.Set<String> favoritedIds = java.util.Collections.emptySet();
        if (username != null) {
            List<String> listingIds = pageResult.getContent().stream()
                    .map(Listing::getPublicId)
                    .toList();
            favoritedIds = favoriteService.getFavoritedListingIds(username, listingIds);
        }

        final java.util.Set<String> finalFavoritedIds = favoritedIds;
        List<ListingSummaryResponse> content = pageResult.getContent().stream()
                .map(l -> ListingSummaryResponse.from(l, finalFavoritedIds.contains(l.getPublicId())))
                .toList();

        return PageMapper.toPageResponse(pageResult, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingSummaryResponse> getNearbyListings(NearbyListingRequest request) {

        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();
        Double radiusMiles = request.getRadiusMiles();
        String categorySlug = request.getCategorySlug();

        if (latitude == null || longitude == null || radiusMiles == null) {
            throw new IllegalArgumentException("latitude, longitude, radiusMiles are required");
        }

        // 1) Only ACTIVE within a range
        List<Listing> nearby = listingRepository.findNearbyWithinRadius(
                latitude, longitude, radiusMiles, ListingStatus.ACTIVE);

        // 2) memory filter when there's category slug
        if (categorySlug != null && !categorySlug.isBlank()) {
            nearby = nearby.stream()
                    .filter(l -> l.getCategory() != null
                            && categorySlug.equals(l.getCategory().getSlug()))
                    .toList();
        }

        // 3) mapping to response
        return nearby.stream()
                .map(ListingSummaryResponse::from) // from(listing) → isFavorite=false
                .toList();
    }

    @Override
    @Transactional
    public void incrementViewCount(String publicId) {

        Listing listing = listingValidator.validateActiveListingByPublicId(publicId);

        Integer current = listing.getViewCount();
        if (current == null) {
            current = 0;
        }

        listing.setViewCount(current + 1);
        listingRepository.incrementViewCount(publicId);
    }

    @Override
    public Long getListingCountBySeller(String sellerUsername) {
        User seller = authValidator.validateUserByUsername(sellerUsername);
        return listingRepository.countBySeller_UsernameAndDeletedAtIsNull(sellerUsername);
    }

    @Override
    public Long getListingCountBySellerAndStatus(String sellerPublicId, ListingStatus status) {
        return listingRepository.countBySeller_PublicIdAndStatusAndDeletedAtIsNull(sellerPublicId, status);
    }

    // Helper methods

    /**
     *  Upload images to S3 and attach as ListingImage with display order.
     *  Skips empty files; applies a simple max-count guard.
     **/
    private void attachImages(Listing listing,
                              List<MultipartFile> images,
                              List<String> uploadedUrls) {

        if (images == null || images.isEmpty()) return;

        // simple guardrail for assignments
        if (images.size() > 10) {
            throw new IllegalArgumentException("You can upload up to 10 images.");
        }

        int order = 0;
        for (MultipartFile file : images) {
            if (file == null || file.isEmpty()) continue;

            String url = s3Service.upload(file);   // returns S3 public URL
            uploadedUrls.add(url);

            listing.getImages().add(
                    ListingImage.builder()
                            .listing(listing)
                            .imageUrl(url)
                            .displayOrder(order++)
                            .build()
            );
        }
    }

    /** Best-effort cleanup for already uploaded files when something fails mid-flow. */
    private void cleanupUploaded(List<String> urls) {
        for (String url : urls) {
            try { s3Service.deleteByUrl(url); } catch (Exception ignore) {}
        }
    }
}