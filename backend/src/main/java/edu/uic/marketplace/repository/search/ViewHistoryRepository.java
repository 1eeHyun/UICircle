package edu.uic.marketplace.repository.search;

import edu.uic.marketplace.model.search.ViewHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    /**
     * Find view history by username and listing public ID
     * Uses read-only hint for performance
     */
    @Query("SELECT vh FROM ViewHistory vh " +
            "JOIN vh.user u " +
            "JOIN vh.listing l " +
            "WHERE u.username = :username AND l.publicId = :listingPublicId")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Optional<ViewHistory> findByUsernameAndListingPublicId(@Param("username") String username,
                                                           @Param("listingPublicId") String listingPublicId);

    Optional<ViewHistory> findByUser_UserIdAndListing_ListingId(Long userId, Long listingId);

    /**
     * Find user's view history with pagination, ordered by most recent first
     * Fetch joins with listing to avoid N+1 problem
     */
    @Query(value = "SELECT vh FROM ViewHistory vh " +
            "JOIN FETCH vh.listing l " +
            "LEFT JOIN FETCH l.seller " +
            "LEFT JOIN FETCH l.category " +
            "JOIN vh.user u " +
            "WHERE u.username = :username " +
            "ORDER BY vh.viewedAt DESC",
            countQuery = "SELECT COUNT(vh) FROM ViewHistory vh " +
                    "JOIN vh.user u " +
                    "WHERE u.username = :username")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Page<ViewHistory> findByUsernameWithListing(@Param("username") String username, Pageable pageable);

    /**
     * Find recent views by username with listing information
     * Optimized with fetch join to prevent N+1 queries
     */
    @Query("SELECT DISTINCT vh FROM ViewHistory vh " +
            "JOIN FETCH vh.listing l " +
            "LEFT JOIN FETCH l.seller " +
            "LEFT JOIN FETCH l.category " +
            "JOIN vh.user u " +
            "WHERE u.username = :username " +
            "ORDER BY vh.viewedAt DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<ViewHistory> findRecentViewsWithListingByUsername(@Param("username") String username,
                                                           Pageable pageable);


    /**
     * Delete all view history for a user
     * Uses batch delete for better performance
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ViewHistory vh WHERE vh.user.username = :username")
    void deleteByUsername(@Param("username") String username);

    /**
     * Delete view history for a specific listing
     * Uses batch delete for better performance
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ViewHistory vh " +
            "WHERE vh.user.username = :username " +
            "AND vh.listing.publicId = :listingPublicId")
    void deleteByUsernameAndListingPublicId(@Param("username") String username,
                                            @Param("listingPublicId") String listingPublicId);

    /**
     * Check if user has viewed a specific listing
     * Optimized with EXISTS query
     */
    @Query("SELECT CASE WHEN COUNT(vh) > 0 THEN true ELSE false END " +
            "FROM ViewHistory vh " +
            "WHERE vh.user.username = :username " +
            "AND vh.listing.publicId = :listingPublicId")
    boolean existsByUsernameAndListingPublicId(@Param("username") String username,
                                               @Param("listingPublicId") String listingPublicId);

    /**
     * Count total view history entries for a user
     */
    @Query("SELECT COUNT(vh) FROM ViewHistory vh " +
            "WHERE vh.user.username = :username")
    long countByUsername(@Param("username") String username);

    /**
     * Delete old view history in batches (for data cleanup)
     * Uses batch delete for better performance on large datasets
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ViewHistory vh WHERE vh.viewedAt < :cutoffDate")
    void deleteOldViewHistory(@Param("cutoffDate") Instant cutoffDate);

    /**
     * Count total views for a specific listing
     */
    @Query("SELECT COUNT(vh) FROM ViewHistory vh " +
            "WHERE vh.listing.publicId = :listingPublicId")
    long countByListingPublicId(@Param("listingPublicId") String listingPublicId);

    /**
     * Find view history within a date range with listing information
     */
    @Query("SELECT DISTINCT vh FROM ViewHistory vh " +
            "JOIN FETCH vh.listing l " +
            "LEFT JOIN FETCH l.seller " +
            "LEFT JOIN FETCH l.category " +
            "JOIN vh.user u " +
            "WHERE u.username = :username " +
            "AND vh.viewedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY vh.viewedAt DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<ViewHistory> findByUsernameAndDateRangeWithListing(@Param("username") String username,
                                                            @Param("startDate") Instant startDate,
                                                            @Param("endDate") Instant endDate);

    /**
     * Batch update viewed_at timestamp for multiple view histories
     * More efficient than updating one by one
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ViewHistory vh SET vh.viewedAt = :viewedAt " +
            "WHERE vh.user.username = :username " +
            "AND vh.listing.publicId = :listingPublicId")
    void updateViewedAt(@Param("username") String username,
                        @Param("listingPublicId") String listingPublicId,
                        @Param("viewedAt") Instant viewedAt);
}