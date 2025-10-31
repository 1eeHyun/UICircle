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

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    
    /**
     * Find listings by seller
     */
    Page<Listing> findBySeller_UserId(Long sellerId, Pageable pageable);
    
    /**
     * Find listings by seller and status
     */
    Page<Listing> findBySeller_UserIdAndStatus(Long sellerId, ListingStatus status, Pageable pageable);
    
    /**
     * Find listings by status
     */
    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);
    
    /**
     * Find listings by category
     */
    Page<Listing> findByCategory_CategoryId(Long categoryId, Pageable pageable);
    
    /**
     * Search listings by title or description
     */
    @Query("""
           SELECT l FROM Listing l 
           WHERE
               (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND
               l.status = :status
           """
    )
    Page<Listing> searchByKeyword(@Param("keyword") String keyword, 
                                   @Param("status") ListingStatus status, 
                                   Pageable pageable);
    
    /**
     * Find nearby listings using distance calculation
     */
    @Query(value = """
            SELECT * 
            FROM listings l 
            WHERE l.status = :status
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
    List<Listing> findNearbyMiles(
            @Param("latitude") Double lat,
            @Param("longitude") Double lon,
            @Param("radiusMi") Double radiusMi,
            @Param("status") String status
    );

    /**
     * Count listings by seller
     */
    Long countBySeller_UserId(Long sellerId);
    
    /**
     * Count listings by seller and status
     */
    Long countBySeller_UserIdAndStatus(Long sellerId, ListingStatus status);
}
