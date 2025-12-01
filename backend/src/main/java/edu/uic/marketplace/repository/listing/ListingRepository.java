package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

    // =================================================================
    // External API Methods - Use publicId for all external operations
    // =================================================================

    /**
     * Find listing by public ID where not deleted (for public/owner/admin views)
     */
    Optional<Listing> findByPublicIdAndDeletedAtIsNull(String publicId);

    /**
     * Find listing by public ID with all details (OPTIMIZED - prevents N+1)
     * Use this for detail views that need seller, category, images
     */
    @Query("SELECT DISTINCT l FROM Listing l " +
            "LEFT JOIN FETCH l.seller " +
            "LEFT JOIN FETCH l.category " +
            "LEFT JOIN FETCH l.images " +
            "WHERE l.publicId = :publicId AND l.deletedAt IS NULL")
    Optional<Listing> findByPublicIdWithDetails(@Param("publicId") String publicId);

    /**
     * Find active listing by public ID with all details (OPTIMIZED)
     * Most commonly used for public listing detail views
     */
    @Query("SELECT DISTINCT l FROM Listing l " +
            "LEFT JOIN FETCH l.seller " +
            "LEFT JOIN FETCH l.category " +
            "LEFT JOIN FETCH l.images " +
            "WHERE l.publicId = :publicId AND l.status = 'ACTIVE' AND l.deletedAt IS NULL")
    Optional<Listing> findActiveByPublicIdWithDetails(@Param("publicId") String publicId);

    /**
     * Find listing by public ID with specific status where not deleted
     */
    Optional<Listing> findByPublicIdAndStatusAndDeletedAtIsNull(String publicId, ListingStatus status);

    /**
     * Find listing by public ID with status in given set where not deleted
     */
    Optional<Listing> findByPublicIdAndStatusInAndDeletedAtIsNull(String publicId, Collection<ListingStatus> statuses);

    /**
     * Find active listings (no user exclusion)
     */
    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT l FROM Listing l WHERE l.status = :status AND l.deletedAt IS NULL")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Page<Listing> findByStatus(@Param("status") ListingStatus status, Pageable pageable);

    /**
     * Find active listings excluding specific users (OPTIMIZED)
     */
    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT l FROM Listing l " +
            "WHERE l.status = :status " +
            "AND l.seller.username NOT IN :excludedUsernames " +
            "AND l.deletedAt IS NULL")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Page<Listing> findByStatusExcludingUsers(
            @Param("status") ListingStatus status,
            @Param("excludedUsernames") List<String> excludedUsernames,
            Pageable pageable
    );

    /**
     * Check if listing exists by public ID
     */
    boolean existsByPublicId(String publicId);

    // =================================================================
    // Listing Update Operations - Optimized bulk updates
    // =================================================================

    /**
     * Increment view count without loading entity (OPTIMIZED)
     * Single UPDATE query, no SELECT needed
     */
    @Modifying
    @Query("UPDATE Listing l SET l.viewCount = l.viewCount + 1 WHERE l.publicId = :publicId")
    void incrementViewCount(@Param("publicId") String publicId);

    /**
     * Increment view count only if viewer is not the seller (OPTIMIZED)
     * Conditional update in single query
     */
    @Modifying
    @Query("UPDATE Listing l SET l.viewCount = l.viewCount + 1 " +
            "WHERE l.publicId = :publicId AND l.seller.userId != :viewerId")
    int incrementViewCountIfNotSeller(@Param("publicId") String publicId, @Param("viewerId") Long viewerId);

    /**
     * Update listing status efficiently (OPTIMIZED)
     * Bulk update without loading entity
     */
    @Modifying
    @Query("UPDATE Listing l SET l.status = :status WHERE l.publicId = :publicId")
    void updateStatus(@Param("publicId") String publicId, @Param("status") ListingStatus status);

    /**
     * Soft delete listing efficiently (OPTIMIZED)
     */
    @Modifying
    @Query("UPDATE Listing l SET l.deletedAt = :deletedAt WHERE l.publicId = :publicId")
    void softDelete(@Param("publicId") String publicId, @Param("deletedAt") Instant deletedAt);

    // =================================================================
    // Seller's Listings - Use seller's publicId
    // =================================================================

    /**
     * Find all listings by seller's username where not deleted
     */
    Page<Listing> findBySeller_PublicIdAndDeletedAtIsNull(String sellerPublicId, Pageable pageable);

    /**
     * Find listings by seller's username and status where not deleted
     */
    Page<Listing> findBySeller_UsernameAndStatusAndDeletedAtIsNull(String username, ListingStatus status, Pageable pageable);

    /**
     * Find listings by seller's public ID and status where not deleted (OPTIMIZED)
     */
    @Query(value = "SELECT DISTINCT l FROM Listing l " +
            "LEFT JOIN FETCH l.category " +
            "LEFT JOIN FETCH l.images " +
            "WHERE l.seller.publicId = :sellerPublicId AND l.status = :status AND l.deletedAt IS NULL",
            countQuery = "SELECT COUNT(l) FROM Listing l WHERE l.seller.publicId = :sellerPublicId AND l.status = :status AND l.deletedAt IS NULL")
    Page<Listing> findBySellerWithDetails(@Param("sellerPublicId") String sellerPublicId,
                                          @Param("status") ListingStatus status,
                                          Pageable pageable);

    /**
     * Find listings by seller's public ID with status in set where not deleted
     */
    Page<Listing> findBySeller_PublicIdAndStatusInAndDeletedAtIsNull(String sellerPublicId, Collection<ListingStatus> statuses, Pageable pageable);

    /**
     * Count listings by seller's username where not deleted
     */
    Long countBySeller_UsernameAndDeletedAtIsNull(String username);

    /**
     * Count listings by seller's public ID and status where not deleted
     */
    Long countBySeller_PublicIdAndStatusAndDeletedAtIsNull(String sellerPublicId, ListingStatus status);

    // =================================================================
    // Public Feed and Category Listings - Use category slug
    // =================================================================

    /**
     * Find listings by exact status where not deleted (for public feed)
     */
    Page<Listing> findByStatusAndDeletedAtIsNull(ListingStatus status, Pageable pageable);

    /**
     * Find listings by exact status with all related data (OPTIMIZED - no N+1)
     * Use this for home feed to prevent N+1 queries
     */
    @Query("SELECT DISTINCT l FROM Listing l " +
            "LEFT JOIN FETCH l.category " +
            "LEFT JOIN FETCH l.seller " +
            "LEFT JOIN FETCH l.images " +
            "WHERE l.status = :status AND l.deletedAt IS NULL")
    List<Listing> findByStatusWithDetailsNoPage(@Param("status") ListingStatus status);

    /**
     * Find listings by status with pagination and all related data (OPTIMIZED)
     * Prevents N+1 for paginated feeds
     */
    @Query(value = "SELECT DISTINCT l FROM Listing l " +
            "LEFT JOIN FETCH l.category " +
            "LEFT JOIN FETCH l.seller " +
            "WHERE l.status = :status AND l.deletedAt IS NULL",
            countQuery = "SELECT COUNT(l) FROM Listing l WHERE l.status = :status AND l.deletedAt IS NULL")
    Page<Listing> findByStatusWithDetails(@Param("status") ListingStatus status, Pageable pageable);

    /**
     * Find listings by status in set where not deleted (flexible public feed)
     */
    Page<Listing> findByStatusInAndDeletedAtIsNull(Collection<ListingStatus> statuses, Pageable pageable);

    /**
     * Find listings by category slug where not deleted
     */
    Page<Listing> findByCategory_SlugAndDeletedAtIsNull(String categorySlug, Pageable pageable);

    /**
     * Find listings by category slug and status where not deleted
     */
    Page<Listing> findByCategory_SlugAndStatusAndDeletedAtIsNull(String categorySlug, ListingStatus status, Pageable pageable);

    /**
     * Find listings by category slug and status with all related data (OPTIMIZED)
     * Prevents N+1 for category pages
     */
    @Query(value = "SELECT DISTINCT l FROM Listing l " +
            "LEFT JOIN FETCH l.category c " +
            "LEFT JOIN FETCH l.seller " +
            "LEFT JOIN FETCH l.images " +
            "WHERE c.slug = :categorySlug AND l.status = :status AND l.deletedAt IS NULL",
            countQuery = "SELECT COUNT(l) FROM Listing l WHERE l.category.slug = :categorySlug AND l.status = :status AND l.deletedAt IS NULL")
    Page<Listing> findByCategoryWithDetails(@Param("categorySlug") String categorySlug,
                                            @Param("status") ListingStatus status,
                                            Pageable pageable);

    /**
     * Find listings by category slug excluding blocked users (OPTIMIZED)
     */
    @EntityGraph(attributePaths = {"seller", "category"})
    @Query("SELECT l FROM Listing l " +
            "WHERE l.category.slug = :categorySlug " +
            "AND l.status = :status " +
            "AND l.seller.username NOT IN :excludedUsernames " +
            "AND l.deletedAt IS NULL")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Page<Listing> findByCategoryExcludingUsers(
            @Param("categorySlug") String categorySlug,
            @Param("status") ListingStatus status,
            @Param("excludedUsernames") List<String> excludedUsernames,
            Pageable pageable
    );

    /**
     * Find listings by category slug and status in set where not deleted
     */
    Page<Listing> findByCategory_SlugAndStatusInAndDeletedAtIsNull(String categorySlug, Collection<ListingStatus> statuses, Pageable pageable);

    // =================================================================
    // Search - Use publicId for results
    // =================================================================

    /**
     * Search listings by keyword with specific status, excluding deleted
     */
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

    /**
     * Search listings by keyword with all details (OPTIMIZED)
     * Prevents N+1 for search results
     */
    @Query(value = """
           SELECT DISTINCT l FROM Listing l
           LEFT JOIN FETCH l.category
           LEFT JOIN FETCH l.seller
           LEFT JOIN FETCH l.images
           WHERE (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND l.status = :status
             AND l.deletedAt IS NULL
           """,
            countQuery = """
           SELECT COUNT(l) FROM Listing l
           WHERE (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND l.status = :status
             AND l.deletedAt IS NULL
           """)
    Page<Listing> searchByKeywordWithDetails(@Param("keyword") String keyword,
                                             @Param("status") ListingStatus status,
                                             Pageable pageable);

    /**
     * Search listings by keyword with status in set, excluding deleted
     */
    @Query("""
           SELECT l FROM Listing l
           WHERE (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND l.status IN :statuses
             AND l.deletedAt IS NULL
           """)
    Page<Listing> searchByKeywordInStatuses(@Param("keyword") String keyword,
                                            @Param("statuses") Collection<ListingStatus> statuses,
                                            Pageable pageable);

    /**
     * Search listings by keyword and category slug with status filter
     */
    @Query("""
           SELECT l FROM Listing l
           WHERE (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND l.category.slug = :categorySlug
             AND l.status IN :statuses
             AND l.deletedAt IS NULL
           """)
    Page<Listing> searchByKeywordAndCategory(@Param("keyword") String keyword,
                                             @Param("categorySlug") String categorySlug,
                                             @Param("statuses") Collection<ListingStatus> statuses,
                                             Pageable pageable);

    /**
     * Search listings by keyword and category with all details (OPTIMIZED)
     */
    @Query(value = """
           SELECT DISTINCT l FROM Listing l
           LEFT JOIN FETCH l.category c
           LEFT JOIN FETCH l.seller
           LEFT JOIN FETCH l.images
           WHERE (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND c.slug = :categorySlug
             AND l.status IN :statuses
             AND l.deletedAt IS NULL
           """,
            countQuery = """
           SELECT COUNT(l) FROM Listing l
           WHERE (LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND l.category.slug = :categorySlug
             AND l.status IN :statuses
             AND l.deletedAt IS NULL
           """)
    Page<Listing> searchByKeywordAndCategoryWithDetails(@Param("keyword") String keyword,
                                                        @Param("categorySlug") String categorySlug,
                                                        @Param("statuses") Collection<ListingStatus> statuses,
                                                        Pageable pageable);

    // =================================================================
    // Geolocation Search
    // =================================================================

    /**
     * Find nearby listings within radius (miles) with single status filter
     * Uses Haversine formula for distance calculation
     * Note: For better performance, consider PostGIS or similar spatial database extensions
     */
    @Query(value = """
            SELECT *
            FROM listings l
            WHERE l.status = :#{#status.name()}
              AND l.deleted_at IS NULL
              AND (3959 * acos(
                    cos(radians(:latitude)) * cos(radians(l.latitude)) *
                    cos(radians(l.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(l.latitude))
                  )) <= :radiusMiles
            ORDER BY (3959 * acos(
                      cos(radians(:latitude)) * cos(radians(l.latitude)) *
                      cos(radians(l.longitude) - radians(:longitude)) +
                      sin(radians(:latitude)) * sin(radians(l.latitude))
                    ))
            """, nativeQuery = true)
    List<Listing> findNearbyWithinRadius(@Param("latitude") Double latitude,
                                         @Param("longitude") Double longitude,
                                         @Param("radiusMiles") Double radiusMiles,
                                         @Param("status") ListingStatus status);

    /**
     * Find nearby listings within radius with multiple status filters
     */
    @Query(value = """
            SELECT l.*
            FROM listings l
            WHERE l.status IN (:statuses)
              AND l.deleted_at IS NULL
              AND (3959 * acos(
                    cos(radians(:latitude)) * cos(radians(l.latitude)) *
                    cos(radians(l.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(l.latitude))
                  )) <= :radiusMiles
            ORDER BY (3959 * acos(
                      cos(radians(:latitude)) * cos(radians(l.latitude)) *
                      cos(radians(l.longitude) - radians(:longitude)) +
                      sin(radians(:latitude)) * sin(radians(l.latitude))
                    ))
            """, nativeQuery = true)
    List<Listing> findNearbyWithinRadiusInStatuses(@Param("latitude") Double latitude,
                                                   @Param("longitude") Double longitude,
                                                   @Param("radiusMiles") Double radiusMiles,
                                                   @Param("statuses") Collection<String> statuses);

    // =================================================================
    // Internal Methods - Use Long ID only for FK relationships
    // =================================================================

    /**
     * Find listing by internal ID (for internal FK operations only)
     * Do not expose this in external APIs
     */
    Optional<Listing> findById(Long listingId);

    /**
     * Find listing by internal ID where not deleted (for internal operations)
     */
    Optional<Listing> findByListingIdAndDeletedAtIsNull(Long listingId);

    /**
     * Find listings by seller's internal ID (for internal aggregations)
     */
    Page<Listing> findBySeller_UserIdAndDeletedAtIsNull(Long sellerId, Pageable pageable);
}