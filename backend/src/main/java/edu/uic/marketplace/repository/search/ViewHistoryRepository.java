package edu.uic.marketplace.repository.search;

import edu.uic.marketplace.model.search.ViewHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, ViewHistory.ViewHistoryId> {
    
    /**
     * Find view history by user
     */
    Page<ViewHistory> findByUser_UserId(Long userId, Pageable pageable);
    
    /**
     * Find view history by user ordered by viewed date
     */
    Page<ViewHistory> findByUser_UserIdOrderByViewedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find view history by user and listing
     */
    Optional<ViewHistory> findByUser_UserIdAndListing_ListingId(Long userId, Long listingId);
    
    /**
     * Check if user has viewed listing
     */
    boolean existsByUser_UserIdAndListing_ListingId(Long userId, Long listingId);
    
    /**
     * Count views for listing
     */
    Long countByListing_ListingId(Long listingId);
    
    /**
     * Get recently viewed listings for user
     */
    @Query("SELECT vh FROM ViewHistory vh WHERE vh.user.userId = :userId " +
           "ORDER BY vh.viewedAt DESC")
    List<ViewHistory> findRecentlyViewedByUser(Long userId, Pageable pageable);
    
    /**
     * Delete view history by user
     */
    void deleteByUser_UserId(Long userId);
    
    /**
     * Delete view history by listing
     */
    void deleteByListing_ListingId(Long listingId);
}
