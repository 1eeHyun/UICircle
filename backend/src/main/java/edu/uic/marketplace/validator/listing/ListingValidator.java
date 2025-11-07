package edu.uic.marketplace.validator.listing;

import edu.uic.marketplace.exception.listing.ListingNotFoundException;
import edu.uic.marketplace.exception.listing.UserNotSellerException;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListingValidator {

    private final ListingRepository listingRepository;

    // =================================================================
    // External API Validation - Use publicId
    // =================================================================

    /**
     * Validate and retrieve listing by public ID (for external API calls)
     * Returns listing if it exists and is not soft-deleted
     */
    public Listing validateListingByPublicId(String publicId) {
        return listingRepository
                .findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> new ListingNotFoundException("Listing with ID " + publicId + " not found"));
    }

    /**
     * Get listing for public view (ACTIVE or SOLD status only)
     * Used for public listing pages and search results
     */
    public Listing getListingForPublicView(String publicId) {
        return listingRepository
                .findByPublicIdAndStatusInAndDeletedAtIsNull(
                        publicId,
                        List.of(ListingStatus.ACTIVE, ListingStatus.SOLD)
                )
                .orElseThrow(() -> new ListingNotFoundException(
                        "Active or sold listing with ID " + publicId + " not found"));
    }

    /**
     * Get listing for seller view (ACTIVE, SOLD, or INACTIVE status)
     * Validates that the requester is the seller
     */
    public Listing getListingForSellerView(String publicId, String sellerPublicId) {
        Listing listing = listingRepository
                .findByPublicIdAndStatusInAndDeletedAtIsNull(
                        publicId,
                        List.of(ListingStatus.ACTIVE, ListingStatus.SOLD, ListingStatus.INACTIVE)
                )
                .orElseThrow(() -> new ListingNotFoundException("Listing with ID " + publicId + " not found"));

        if (!listing.getSeller().getPublicId().equals(sellerPublicId)) {
            throw new UserNotSellerException("User is not the seller of this listing");
        }

        return listing;
    }

    /**
     * Get listing for admin view (any status, including soft-deleted)
     * Used for moderation and admin panels
     */
    public Listing getListingForAdminView(String publicId) {
        return listingRepository
                .findByPublicIdAndDeletedAtIsNull(publicId)
                .orElseThrow(() -> new ListingNotFoundException("Listing with ID " + publicId + " not found"));
    }

    /**
     * Validate listing by public ID with specific status
     */
    public Listing validateListingByPublicIdAndStatus(String publicId, ListingStatus status) {
        return listingRepository
                .findByPublicIdAndStatusAndDeletedAtIsNull(publicId, status)
                .orElseThrow(() -> new ListingNotFoundException(
                        "Listing with ID " + publicId + " and status " + status + " not found"));
    }

    /**
     * Validate that listing is active and available for transactions
     */
    public Listing validateActiveListingByPublicId(String publicId) {
        Listing listing = validateListingByPublicIdAndStatus(publicId, ListingStatus.ACTIVE);

        if (!listing.isActive()) {
            throw new ListingNotFoundException("Listing with ID " + publicId + " is not available");
        }

        return listing;
    }

    // =================================================================
    // Ownership and Authorization Validation
    // =================================================================

    /**
     * Validate that the user is the seller of the listing
     */
    public void validateSellerOwnership(User user, User seller) {
        if (user == null || seller == null) {
            throw new UserNotSellerException("Invalid user or seller");
        }

        if (!user.getUserId().equals(seller.getUserId())) {
            throw new UserNotSellerException("User is not the seller of this listing");
        }
    }

    /**
     * Validate that the user is the seller of the listing using public IDs
     */
    public void validateSellerOwnershipByPublicId(String userPublicId, String sellerPublicId) {
        if (userPublicId == null || sellerPublicId == null) {
            throw new UserNotSellerException("Invalid user or seller ID");
        }

        if (!userPublicId.equals(sellerPublicId)) {
            throw new UserNotSellerException("User is not the seller of this listing");
        }
    }

    /**
     * Validate that the user can modify the listing
     * User must be the seller and listing must be in a modifiable state
     */
    public void validateListingModificationPermission(User user, Listing listing) {
        validateSellerOwnership(user, listing.getSeller());

        if (listing.getStatus() == ListingStatus.SOLD) {
            throw new IllegalStateException("Cannot modify a sold listing");
        }

        if (listing.getStatus() == ListingStatus.DELETED) {
            throw new IllegalStateException("Cannot modify a deleted listing");
        }
    }

    /**
     * Validate that the listing can be purchased
     */
    public void validateListingAvailableForPurchase(Listing listing) {
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalStateException("Listing is not available for purchase");
        }

        if (listing.getDeletedAt() != null) {
            throw new IllegalStateException("Listing has been deleted");
        }
    }

    /**
     * Validate that the user is not trying to buy their own listing
     */
    public void validateBuyerNotSeller(User buyer, Listing listing) {
        if (buyer.getUserId().equals(listing.getSeller().getUserId())) {
            throw new IllegalStateException("Cannot purchase your own listing");
        }
    }

    // =================================================================
    // Status Transition Validation
    // =================================================================

    /**
     * Validate that the status transition is allowed
     */
    public void validateStatusTransition(ListingStatus currentStatus, ListingStatus newStatus) {
        // Define allowed transitions
        boolean isValidTransition = switch (currentStatus) {
            case ACTIVE -> newStatus == ListingStatus.INACTIVE
                    || newStatus == ListingStatus.SOLD
                    || newStatus == ListingStatus.DELETED;
            case INACTIVE -> newStatus == ListingStatus.ACTIVE
                    || newStatus == ListingStatus.DELETED;
            case SOLD -> newStatus == ListingStatus.DELETED; // Can only delete sold listings
            case DELETED -> false; // Cannot transition from deleted
        };

        if (!isValidTransition) {
            throw new IllegalStateException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    // =================================================================
    // Internal Methods - Use Long ID only for FK relationships
    // =================================================================

    /**
     * Validate listing by internal ID (for internal FK operations only)
     * Do not expose this in external APIs
     */
    Listing validateListingByIdInternal(Long listingId) {
        return listingRepository
                .findByListingIdAndDeletedAtIsNull(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing with internal ID " + listingId + " not found"));
    }
}
