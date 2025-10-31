package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.request.transaction.CreateReviewRequest;
import edu.uic.marketplace.dto.response.transaction.ReviewResponse;
import edu.uic.marketplace.model.transaction.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    @Override
    public ReviewResponse createReview(Long transactionId, Long reviewerId, CreateReviewRequest request) {
        return null;
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return Optional.empty();
    }

    @Override
    public Optional<Review> findByTransactionAndReviewer(Long transactionId, Long reviewerId) {
        return Optional.empty();
    }

    @Override
    public List<ReviewResponse> getReceivedReviews(Long userId) {
        return null;
    }

    @Override
    public List<ReviewResponse> getWrittenReviews(Long userId) {
        return null;
    }

    @Override
    public List<ReviewResponse> getListingReviews(Long listingId) {
        return null;
    }

    @Override
    public Double getAverageRating(Long userId) {
        return null;
    }

    @Override
    public boolean canReview(Long transactionId, Long userId) {
        return false;
    }

    @Override
    public boolean hasReviewed(Long transactionId, Long userId) {
        return false;
    }

    @Override
    public void deleteReview(Long reviewId) {

    }
}
