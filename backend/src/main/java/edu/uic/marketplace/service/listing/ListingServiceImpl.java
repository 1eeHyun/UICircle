package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
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
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    @Transactional
    public ListingResponse createListing(String username, CreateListingRequest request, List<MultipartFile> images) {

        // 1) validate user
        User seller = authValidator.validateUserByUsername(username);

        // 2) validate category (must be a leaf)
        Category category = categoryValidator.validateLeafCategory(request.getSlug());

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
        listing.softDelete();
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
            throw new IllegalStateException("Only INACTIVE listing can be marked as SOLD.");

        // 2) reactivate
        listing.setStatus(ListingStatus.ACTIVE);
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

        // 2) mark as sold
        listing.setStatus(ListingStatus.SOLD);
    }

    @Override
    @Transactional
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
    @Transactional
    public void incrementViewCount(String publicId) {
        Listing found = listingValidator.validateActiveListingByPublicId(publicId);
        found.incrementViewCount(); // viewCount++
    }

    @Override
    public Long getListingCountBySeller(String sellerPublicId) {
        return null;
    }

    @Override
    public Long getListingCountBySellerAndStatus(String sellerPublicId, ListingStatus status) {
        return null;
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
