package edu.uic.marketplace.dto.response.transaction;

import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.transaction.Transaction;
import edu.uic.marketplace.model.transaction.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long transactionId;
    private ListingSummaryResponse listing;
    private UserResponse buyer;
    private UserResponse seller;
    private BigDecimal finalPrice;
    private String paymentMethod;
    private TransactionStatus status;
    private Instant completedAt;
    private Instant createdAt;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .listing(ListingSummaryResponse.from(transaction.getListing()))
                .buyer(UserResponse.from(transaction.getBuyer()))
                .seller(UserResponse.from(transaction.getSeller()))
                .finalPrice(transaction.getFinalPrice())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .completedAt(transaction.getCompletedAt())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}