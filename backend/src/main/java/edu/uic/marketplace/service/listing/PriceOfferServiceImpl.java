package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import edu.uic.marketplace.model.listing.PriceOffer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceOfferServiceImpl implements PriceOfferService {

    @Override
    public PriceOfferResponse createOffer(Long listingId, Long buyerId, CreateOfferRequest request) {
        return null;
    }

    @Override
    public PriceOfferResponse updateOfferStatus(Long offerId, Long userId, UpdateOfferStatusRequest request) {
        return null;
    }

    @Override
    public Optional<PriceOffer> findById(Long offerId) {
        return Optional.empty();
    }

    @Override
    public List<PriceOfferResponse> getOffersForListing(Long listingId, Long sellerId) {
        return null;
    }

    @Override
    public List<PriceOfferResponse> getUserSentOffers(Long userId) {
        return null;
    }

    @Override
    public List<PriceOfferResponse> getUserReceivedOffers(Long userId) {
        return null;
    }

    @Override
    public void cancelOffer(Long offerId, Long buyerId) {

    }

    @Override
    public boolean hasPendingOffer(Long userId, Long listingId) {
        return false;
    }

    @Override
    public Optional<PriceOfferResponse> getAcceptedOffer(Long listingId) {
        return Optional.empty();
    }

    @Override
    public void autoRejectOtherOffers(Long listingId, Long acceptedOfferId) {

    }
}
