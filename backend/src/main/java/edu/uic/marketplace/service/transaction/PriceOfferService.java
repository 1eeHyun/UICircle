package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import edu.uic.marketplace.model.transaction.PriceOffer;

import java.util.List;
import java.util.Optional;

/**
 * Price offer management service interface
 */
public interface PriceOfferService {
    
    /**
     * Create price offer
     * @param listingId Listing ID
     * @param buyerUsername Buyer user ID
     * @param request Create offer request
     * @return Created price offer response
     */
    PriceOfferResponse createOffer(String listingId, String buyerUsername, CreateOfferRequest request);
    
    /**
     * Update offer status (Accept/Reject/Counter)
     * @param offerId Offer ID
     * @param username User ID (must be seller)
     * @param request Update status request
     * @return Updated price offer response
     */
    PriceOfferResponse updateOfferStatus(String offerId, String username, UpdateOfferStatusRequest request);
    
    /**
     * Get offer by ID
     * @param offerId Offer ID
     * @return PriceOffer entity
     */
    Optional<PriceOffer> findById(String offerId);
    
    /**
     * Get offers for listing
     * @param listingId Listing ID
     * @param sellerUsername Seller user ID (for authorization)
     * @return List of offers
     */
    List<PriceOfferResponse> getOffersForListing(String listingId, String sellerUsername);
    
    /**
     * Get user's sent offers
     * @param username User ID
     * @return List of offers sent by user
     */
    List<PriceOfferResponse> getUserSentOffers(String username);
    
    /**
     * Get user's received offers
     * @param username User ID (seller)
     * @return List of offers received by user
     */
    List<PriceOfferResponse> getUserReceivedOffers(String username);
    
    /**
     * Cancel offer (by buyer)
     * @param offerId Offer ID
     * @param buyerUsername Buyer user ID
     */
    void cancelOffer(String offerId, String buyerUsername);
    
    /**
     * Check if user has pending offer on listing
     * @param username User ID
     * @param listingId Listing ID
     * @return true if has pending offer, false otherwise
     */
    boolean hasPendingOffer(String username, String listingId);
    
    /**
     * Get accepted offer for listing
     * @param listingId Listing ID
     * @return Accepted offer if exists
     */
    Optional<PriceOfferResponse> getAcceptedOffer(String listingId);
    
    /**
     * Auto-reject pending offers when listing is sold
     * @param listingId Listing ID
     * @param acceptedOfferId Accepted offer ID
     */
    void autoRejectOtherOffers(String listingId, String acceptedOfferId);
}
