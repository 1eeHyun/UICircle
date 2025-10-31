package edu.uic.marketplace.repository.transaction;

import edu.uic.marketplace.model.transaction.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find review by transaction and reviewer
     */
    Optional<Review> findByTransaction_TransactionIdAndReviewer_UserId(
            Long transactionId, Long reviewerId);
    
    /**
     * Find reviews received by user
     */
    List<Review> findByReviewedUser_UserId(Long reviewedUserId);
    
    /**
     * Find reviews written by user
     */
    List<Review> findByReviewer_UserId(Long reviewerId);
    
    /**
     * Find reviews for listing
     */
    List<Review> findByTransaction_Listing_ListingId(Long listingId);
    
    /**
     * Calculate average rating for user
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser.userId = :userId")
    Double calculateAverageRating(Long userId);
    
    /**
     * Count reviews for user
     */
    Long countByReviewedUser_UserId(Long userId);
    
    /**
     * Check if review exists for transaction by reviewer
     */
    boolean existsByTransaction_TransactionIdAndReviewer_UserId(
            Long transactionId, Long reviewerId);
}
