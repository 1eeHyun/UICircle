package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.OfferStatus;
import edu.uic.marketplace.model.transaction.PriceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceOfferRepository extends JpaRepository<PriceOffer, Long> {

    /** Find by publicId */
    Optional<PriceOffer> findByPublicId(String publicId);

    /** Check existence by publicId */
    boolean existsByPublicId(String publicId);

    /** Find all offers for a listing */
    List<PriceOffer> findByListing_PublicId(String publicId);

    /** Find all offers for a listing by status */
    List<PriceOffer> findByListing_PublicIdAndStatus(String publicId, OfferStatus status);

    /** Find offers sent by a buyer */
    List<PriceOffer> findByBuyer_Username(String username);

    /** Find offers received by a seller */
    List<PriceOffer> findByListing_Seller_Username(String sellerUsername);

    /** Find pending offer for given buyer & listing */
    Optional<PriceOffer> findByBuyer_UserIdAndListing_ListingIdAndStatus(
            Long buyerId, Long listingId, OfferStatus status);

    /** Check if buyer has pending offer on listing */
    boolean existsByBuyer_UsernameAndListing_PublicIdAndStatus(
            String buyerUsername, String publicId, OfferStatus status);

    /** Count all offers by listing */
    Long countByListing_PublicId(String publicId);

    /** Count offers by listing & status */
    Long countByListing_PublicIdAndStatus(String publicId, OfferStatus status);
}
