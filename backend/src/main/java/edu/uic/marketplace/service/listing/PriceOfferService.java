package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import edu.uic.marketplace.model.listing.OfferStatus;
import edu.uic.marketplace.model.listing.PriceOffer;

import java.util.List;
import java.util.Optional;

/**
 * Price offer management service interface
 */
public interface PriceOfferService {
    
    /**
     * Create price offer
     * @param listingId Listing ID
     * @param buyerId Buyer user ID
     * @param request Create offer request
     * @return Created price offer response
     */
    PriceOfferResponse createOffer(Long listingId, Long buyerId, CreateOfferRequest request);
    
    /**
     * Update offer status (Accept/Reject/Counter)
     * @param offerId Offer ID
     * @param userId User ID (must be seller)
     * @param request Update status request
     * @return Updated price offer response
     */
    PriceOfferResponse updateOfferStatus(Long offerId, Long userId, UpdateOfferStatusRequest request);
    
    /**
     * Get offer by ID
     * @param offerId Offer ID
     * @return PriceOffer entity
     */
    Optional<PriceOffer> findById(Long offerId);
    
    /**
     * Get offers for listing
     * @param listingId Listing ID
     * @param sellerId Seller user ID (for authorization)
     * @return List of offers
     */
    List<PriceOfferResponse> getOffersForListing(Long listingId, Long sellerId);
    
    /**
     * Get user's sent offers
     * @param userId User ID
     * @return List of offers sent by user
     */
    List<PriceOfferResponse> getUserSentOffers(Long userId);
    
    /**
     * Get user's received offers
     * @param userId User ID (seller)
     * @return List of offers received by user
     */
    List<PriceOfferResponse> getUserReceivedOffers(Long userId);
    
    /**
     * Cancel offer (by buyer)
     * @param offerId Offer ID
     * @param buyerId Buyer user ID
     */
    void cancelOffer(Long offerId, Long buyerId);
    
    /**
     * Check if user has pending offer on listing
     * @param userId User ID
     * @param listingId Listing ID
     * @return true if has pending offer, false otherwise
     */
    boolean hasPendingOffer(Long userId, Long listingId);
    
    /**
     * Get accepted offer for listing
     * @param listingId Listing ID
     * @return Accepted offer if exists
     */
    Optional<PriceOfferResponse> getAcceptedOffer(Long listingId);
    
    /**
     * Auto-reject pending offers when listing is sold
     * @param listingId Listing ID
     * @param acceptedOfferId Accepted offer ID
     */
    void autoRejectOtherOffers(Long listingId, Long acceptedOfferId);
}
