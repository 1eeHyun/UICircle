package edu.uic.marketplace.repository.transaction;

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

    /** Check existence by publicId (not strictly needed yet, but ok to keep) */
    boolean existsByPublicId(String publicId);

    /** Find all offers for a listing (optionally you can add OrderByCreatedAtDesc) */
    List<PriceOffer> findByListing_PublicId(String listingPublicId);

    // If you care about ordering:
    // List<PriceOffer> findByListing_PublicIdOrderByCreatedAtDesc(String listingPublicId);

    /** Find all offers for a listing by status */
    List<PriceOffer> findByListing_PublicIdAndStatus(String listingPublicId, OfferStatus status);

    /** Find offers sent by a buyer */
    List<PriceOffer> findByBuyer_Username(String buyerUsername);

    /** Find offers received by a seller */
    List<PriceOffer> findByListing_Seller_Username(String sellerUsername);

    /** Check if buyer has pending offer on listing */
    boolean existsByBuyer_UsernameAndListing_PublicIdAndStatus(
            String buyerUsername,
            String listingPublicId,
            OfferStatus status
    );

    /** Count all offers by listing (optional, useful for stats) */
    Long countByListing_PublicId(String listingPublicId);

    /** Count offers by listing & status (optional) */
    Long countByListing_PublicIdAndStatus(String listingPublicId, OfferStatus status);
}
