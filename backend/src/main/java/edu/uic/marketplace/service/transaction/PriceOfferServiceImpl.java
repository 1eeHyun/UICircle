package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.OfferStatus;
import edu.uic.marketplace.model.transaction.PriceOffer;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.PriceOfferRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import edu.uic.marketplace.validator.listing.OfferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceOfferServiceImpl implements PriceOfferService {

    private final AuthValidator authValidator;
    private final ListingValidator listingValidator;
    private final OfferValidator offerValidator;
    private final PriceOfferRepository priceOfferRepository;

    @Override
    @Transactional
    public PriceOfferResponse createOffer(String listingId, String buyerUsername, CreateOfferRequest request) {

        // 1) validate
        Listing listing = listingValidator.validateActiveListingByPublicId(listingId);
        User buyer = authValidator.validateUserByUsername(buyerUsername);

        // 2) prevent duplicate pending offer from same buyer for same listing
        boolean pendingExists = priceOfferRepository.existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                buyerUsername, listingId, OfferStatus.PENDING
        );

        if (pendingExists) {
            throw new IllegalStateException("You already have a pending offer for this listing.");
        }

        // 3) create a new offer (timestamps handled by Hibernate)
        PriceOffer newOffer = PriceOffer.builder()
                .listing(listing)
                .buyer(buyer)
                .amount(request.getAmount())
                .message(request.getMessage())
                .status(OfferStatus.PENDING)
                .build();

        PriceOffer saved = priceOfferRepository.save(newOffer);

        // 4) TODO: notification

        return PriceOfferResponse.from(saved);
    }

    @Override
    @Transactional
    public PriceOfferResponse updateOfferStatus(String offerId, String username, UpdateOfferStatusRequest request) {

        // 1) validate
        PriceOffer offer = offerValidator.validatePriceOfferByPublicId(offerId);
        User user = authValidator.validateUserByUsername(username);
        offerValidator.validateOfferRightSeller(offer.getListing().getSeller(), user);

        // 2) change status
        offer.setStatus(request.getStatus());
        offer.setMessage(request.getNote());
        // updatedAt -> @UpdateTimestamp auto

        // 3) TODO: notification
        return PriceOfferResponse.from(offer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceOffer> findById(String offerId) {
        return priceOfferRepository.findByPublicId(offerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceOfferResponse> getOffersForListing(String listingId, String sellerUsername) {

        // 1) validate
        Listing listing = listingValidator.validateListingByPublicId(listingId);
        User seller = authValidator.validateUserByUsername(sellerUsername);
        offerValidator.validateOfferRightSeller(listing.getSeller(), seller);

        // 2) fetch & map
        return priceOfferRepository.findByListing_PublicId(listingId).stream()
                .map(PriceOfferResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceOfferResponse> getUserSentOffers(String username) {

        // Retrieve offers that buyer sent
        authValidator.validateUserByUsername(username);
        return priceOfferRepository.findByBuyer_Username(username).stream()
                .map(PriceOfferResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceOfferResponse> getUserReceivedOffers(String username) {

        // Retrieve offers seller received
        authValidator.validateUserByUsername(username);
        return priceOfferRepository.findByListing_Seller_Username(username).stream()
                .map(PriceOfferResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void cancelOffer(String offerId, String buyerUsername) {

        // 1) validate
        PriceOffer offer = offerValidator.validatePriceOfferByPublicId(offerId);
        User buyer = authValidator.validateUserByUsername(buyerUsername);

        // 2) only the buyer can cancel their own pending offer
        if (!offer.getBuyer().equals(buyer)) {
            throw new SecurityException("Not your offer.");
        }
        if (!offer.isPending()) {
            throw new IllegalStateException("Only pending offers can be canceled.");
        }

        offer.setStatus(OfferStatus.REJECTED);
        offer.setMessage("(canceled by buyer) " + (offer.getMessage() == null ? "" : offer.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingOffer(String username, String listingId) {
        return priceOfferRepository.existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                username, listingId, OfferStatus.PENDING
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceOfferResponse> getAcceptedOffer(String listingId) {

        List<PriceOffer> accepted = priceOfferRepository.findByListing_PublicIdAndStatus(
                listingId, OfferStatus.ACCEPTED
        );

        return accepted.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .map(PriceOfferResponse::from);
    }

    @Override
    @Transactional
    public void autoRejectOtherOffers(String listingId, String acceptedOfferId) {

        List<PriceOffer> pending = priceOfferRepository.findByListing_PublicIdAndStatus(
                listingId, OfferStatus.PENDING
        );
        for (PriceOffer po : pending) {
            if (!po.getPublicId().equals(acceptedOfferId)) {
                po.reject(); // helper: status = REJECTED
            }
        }
    }
}
