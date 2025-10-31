package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Favorite.FavoriteId> {
    
    /**
     * Find favorite by user and listing
     */
    Optional<Favorite> findByUser_UserIdAndListing_ListingId(Long userId, Long listingId);
    
    /**
     * Check if user favorited listing
     */
    boolean existsByUser_UserIdAndListing_ListingId(Long userId, Long listingId);
    
    /**
     * Find all favorites by user
     */
    Page<Favorite> findByUser_UserId(Long userId, Pageable pageable);
    
    /**
     * Count favorites for listing
     */
    Long countByListing_ListingId(Long listingId);
    
    /**
     * Count favorites by user
     */
    Long countByUser_UserId(Long userId);
    
    /**
     * Get user's favorite listing IDs
     */
    @Query("SELECT f.listing.listingId FROM Favorite f WHERE f.user.userId = :userId")
    List<Long> findListingIdsByUser_UserId(@Param("userId") Long userId);
    
    /**
     * Delete favorite by user and listing
     */
    void deleteByUser_UserIdAndListing_ListingId(Long userId, Long listingId);
    
    /**
     * Delete all favorites for a listing
     */
    void deleteByListing_ListingId(Long listingId);
    
    /**
     * Delete all favorites by user
     */
    void deleteByUser_UserId(Long userId);
}
