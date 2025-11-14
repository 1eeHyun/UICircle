package edu.uic.marketplace.validator.transaction;

import edu.uic.marketplace.model.transaction.PriceOffer;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.transaction.PriceOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceOfferValidator {

    private final PriceOfferRepository priceOfferRepository;

    /**
     * Validate that price offer exists by publicId.
     * Throw exception if not found.
     */
    public PriceOffer validatePriceOfferByPublicId(String publicId) {
        return priceOfferRepository.findByPublicId(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Price offer not found."));
    }

    /**
     * Ensure that this seller is the owner of the listing.
     */
    public void validateOfferRightSeller(User listingSeller, User actingSeller) {
        if (!listingSeller.getUserId().equals(actingSeller.getUserId())) {
            throw new SecurityException("You are not the seller of this listing.");
        }
    }

    /**
     * Ensure that this buyer is the owner of the offer.
     */
    public void validateOfferRightBuyer(User offerBuyer, User actingBuyer) {
        if (!offerBuyer.getUserId().equals(actingBuyer.getUserId())) {
            throw new SecurityException("You are not the owner of this offer.");
        }
    }

    /**
     * Ensure offer is still pending.
     */
    public void ensurePending(PriceOffer offer) {
        if (!offer.isPending()) {
            throw new IllegalStateException("Only pending offers can be modified.");
        }
    }
}
