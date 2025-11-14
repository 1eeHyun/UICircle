package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;

import java.util.List;
import java.util.Optional;

/**
 * Price offer management service interface
 */
public interface PriceOfferService {

    PriceOfferResponse createOffer(String listingPublicId,
                                   String buyerUsername,
                                   CreateOfferRequest request);

    PriceOfferResponse acceptOffer(String offerPublicId,
                                   String sellerUsername,
                                   UpdateOfferStatusRequest request);

    PriceOfferResponse rejectOffer(String offerPublicId,
                                   String sellerUsername,
                                   UpdateOfferStatusRequest request);

    void cancelOffer(String offerPublicId,
                     String buyerUsername);

    List<PriceOfferResponse> getOffersForListing(String listingPublicId,
                                                 String sellerUsername);

    List<PriceOfferResponse> getUserSentOffers(String buyerUsername);

    List<PriceOfferResponse> getUserReceivedOffers(String sellerUsername);

    Optional<PriceOfferResponse> getOffer(String offerPublicId,
                                          String username);

    boolean hasPendingOffer(String buyerUsername,
                            String listingPublicId);

    Optional<PriceOfferResponse> getAcceptedOffer(String listingPublicId);

    void autoRejectOtherOffers(String listingPublicId, String acceptedOfferPublicId);
}
