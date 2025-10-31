package edu.uic.marketplace.dto.response.transaction;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.transaction.Review;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long reviewId;
    private Long transactionId;
    private UserResponse reviewer;
    private UserResponse reviewedUser;
    private Integer rating;
    private String comment;
    private Instant createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .transactionId(review.getTransaction().getTransactionId())
                .reviewer(UserResponse.from(review.getReviewer()))
                .reviewedUser(UserResponse.from(review.getReviewedUser()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}