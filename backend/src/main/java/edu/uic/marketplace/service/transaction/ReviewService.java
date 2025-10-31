package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.request.transaction.CreateReviewRequest;
import edu.uic.marketplace.dto.response.transaction.ReviewResponse;
import edu.uic.marketplace.model.transaction.Review;

import java.util.List;
import java.util.Optional;

/**
 * Review management service interface
 */
public interface ReviewService {
    
    /**
     * Create review for transaction
     * @param transactionId Transaction ID
     * @param reviewerId Reviewer user ID
     * @param request Create review request
     * @return Created review response
     */
    ReviewResponse createReview(Long transactionId, Long reviewerId, CreateReviewRequest request);
    
    /**
     * Get review by ID
     * @param reviewId Review ID
     * @return Review entity
     */
    Optional<Review> findById(Long reviewId);
    
    /**
     * Get review by transaction and reviewer
     * @param transactionId Transaction ID
     * @param reviewerId Reviewer user ID
     * @return Review entity
     */
    Optional<Review> findByTransactionAndReviewer(Long transactionId, Long reviewerId);
    
    /**
     * Get reviews received by user
     * @param userId Reviewed user ID
     * @return List of reviews
     */
    List<ReviewResponse> getReceivedReviews(Long userId);
    
    /**
     * Get reviews written by user
     * @param userId Reviewer user ID
     * @return List of reviews
     */
    List<ReviewResponse> getWrittenReviews(Long userId);
    
    /**
     * Get reviews for listing (via transaction)
     * @param listingId Listing ID
     * @return List of reviews
     */
    List<ReviewResponse> getListingReviews(Long listingId);
    
    /**
     * Calculate average rating for user
     * @param userId User ID
     * @return Average rating (0.0 if no reviews)
     */
    Double getAverageRating(Long userId);
    
    /**
     * Check if user can review transaction
     * @param transactionId Transaction ID
     * @param userId User ID
     * @return true if can review, false otherwise
     */
    boolean canReview(Long transactionId, Long userId);
    
    /**
     * Check if user has reviewed transaction
     * @param transactionId Transaction ID
     * @param userId User ID
     * @return true if already reviewed, false otherwise
     */
    boolean hasReviewed(Long transactionId, Long userId);
    
    /**
     * Delete review (Admin only)
     * @param reviewId Review ID
     */
    void deleteReview(Long reviewId);
}
