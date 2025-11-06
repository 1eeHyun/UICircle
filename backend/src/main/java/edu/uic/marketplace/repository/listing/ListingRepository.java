package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    // -------------------------
    // Single Listing Retrieval
    // -------------------------

    /** Find a listing by ID where deletedAt is NULL (not deleted) */
    Optional<Listing> findByListingIdAndDeletedAtIsNull(Long listingId);

    /** Find a listing by ID where deletedAt is NULL and status is not the given status */
    Optional<Listing> findByListingIdAndDeletedAtIsNullAndStatusNot(Long listingId, ListingStatus status);


    // -------------------------
    // Paged Listing Retrieval
    // -------------------------

    /** Find all listings by a seller (includes deleted ones) */
    Page<Listing> findBySeller_UserId(Long sellerId, Pageable pageable);

    /** Find all listings by a seller and specific status (includes deleted ones) */
    Page<Listing> findBySeller_UserIdAndStatus(Long sellerId, ListingStatus status, Pageable pageable);

    /** Find all listings by a seller where deletedAt is NULL (not deleted) */
    Page<Listing> findBySeller_UserIdAndDeletedAtIsNull(Long sellerId, Pageable pageable);

    /** Find all listings by a seller and status where deletedAt is NULL (not deleted) */
    Page<Listing> findBySeller_UserIdAndStatusAndDeletedAtIsNull(Long sellerId, ListingStatus status, Pageable pageable);

    /** Find listings by status where deletedAt is NULL (not deleted) */
    Page<Listing> findByStatusAndDeletedAtIsNull(ListingStatus status, Pageable pageable);

    /** Find listings by category where deletedAt is NULL (not deleted) */
    Page<Listing> findByCategory_CategoryIdAndDeletedAtIsNull(Long categoryId, Pageable pageable);


    // -------------------------
    // Search
    // -------------------------

    /** Search listings by keyword in title or description (case-insensitive) with status filter and excluding deleted ones */
    @Query("""
           SELECT l FROM Listing l
           WHERE (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND l.status = :status
             AND l.deletedAt IS NULL
           """)
    Page<Listing> searchByKeyword(@Param("keyword") String keyword,
                                  @Param("status") ListingStatus status,
                                  Pageable pageable);


    // -------------------------
    // Geolocation-based Search
    // -------------------------

    /**
     * Find nearby listings within a certain radius (in miles) using latitude/longitude distance formula.
     * Deleted listings are excluded (deleted_at IS NULL).
     *
     * Note:
     * - 3959 = radius of the Earth in miles.
     * - Change @Param("status") type to ListingStatus if your DB supports enum mapping.
     */
    @Query(value = """
            SELECT *
            FROM listings l
            WHERE l.status = :status
              AND l.deleted_at IS NULL
              AND (3959 * acos(
                    cos(radians(:latitude)) * cos(radians(l.latitude)) *
                    cos(radians(l.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(l.latitude))
                  )) <= :radiusMi
            ORDER BY (3959 * acos(
                      cos(radians(:latitude)) * cos(radians(l.latitude)) *
                      cos(radians(l.longitude) - radians(:longitude)) +
                      sin(radians(:latitude)) * sin(radians(l.latitude))
                    ))
            """, nativeQuery = true)
    List<Listing> findNearbyMiles(@Param("latitude") Double lat,
                                  @Param("longitude") Double lon,
                                  @Param("radiusMi") Double radiusMi,
                                  @Param("status") String status);


    // -------------------------
    // Counting
    // -------------------------

    /** Count total listings by a seller */
    Long countBySeller_UserId(Long sellerId);

    /** Count listings by a seller and specific status */
    Long countBySeller_UserIdAndStatus(Long sellerId, ListingStatus status);
}
