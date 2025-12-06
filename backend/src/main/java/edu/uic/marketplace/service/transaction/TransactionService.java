package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.response.transaction.TransactionResponse;
import edu.uic.marketplace.model.transaction.Transaction;
import edu.uic.marketplace.model.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    TransactionResponse createTransaction(
            String listingPublicId,
            String buyerUsername,
            BigDecimal finalPrice
    );

    Optional<Transaction> findByPublicId(String transactionPublicId);

    Optional<Transaction> findByListingPublicId(String listingPublicId);

    TransactionResponse getTransactionByPublicId(String transactionPublicId, String username);

    List<TransactionResponse> getUserPurchases(String username);

    List<TransactionResponse> getUserSales(String username);

    TransactionResponse updateTransactionStatus(
            String transactionPublicId,
            String username,
            TransactionStatus status
    );

    TransactionResponse completeTransaction(String transactionPublicId, String username);

    TransactionResponse cancelTransaction(String transactionPublicId, String username, String reason);

    boolean isParticipant(String transactionPublicId, String username);
}
