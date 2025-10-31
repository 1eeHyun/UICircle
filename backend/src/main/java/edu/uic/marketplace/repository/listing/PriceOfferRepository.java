package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.OfferStatus;
import edu.uic.marketplace.model.listing.PriceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceOfferRepository extends JpaRepository<PriceOffer, Long> {
    
    /**
     * Find offers by listing
     */
    List<PriceOffer> findByListing_ListingId(Long listingId);
    
    /**
     * Find offers by listing and status
     */
    List<PriceOffer> findByListing_ListingIdAndStatus(Long listingId, OfferStatus status);
    
    /**
     * Find offers sent by buyer
     */
    List<PriceOffer> findByBuyer_UserId(Long buyerId);
    
    /**
     * Find offers received by seller (through listing)
     */
    List<PriceOffer> findByListing_Seller_UserId(Long sellerId);
    
    /**
     * Find pending offer by buyer and listing
     */
    Optional<PriceOffer> findByBuyer_UserIdAndListing_ListingIdAndStatus(
            Long buyerId, Long listingId, OfferStatus status);
    
    /**
     * Check if buyer has pending offer on listing
     */
    boolean existsByBuyer_UserIdAndListing_ListingIdAndStatus(
            Long buyerId, Long listingId, OfferStatus status);
    
    /**
     * Count offers by listing
     */
    Long countByListing_ListingId(Long listingId);
    
    /**
     * Count offers by listing and status
     */
    Long countByListing_ListingIdAndStatus(Long listingId, OfferStatus status);
}
