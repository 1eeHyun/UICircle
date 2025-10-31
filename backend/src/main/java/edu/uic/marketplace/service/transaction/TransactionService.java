package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.response.transaction.TransactionResponse;
import edu.uic.marketplace.model.transaction.Transaction;
import edu.uic.marketplace.model.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Transaction management service interface
 */
public interface TransactionService {
    
    /**
     * Create transaction from accepted offer
     * @param listingId Listing ID
     * @param buyerId Buyer user ID
     * @param finalPrice Final agreed price
     * @return Created transaction response
     */
    TransactionResponse createTransaction(Long listingId, Long buyerId, BigDecimal finalPrice);
    
    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction entity
     */
    Optional<Transaction> findById(Long transactionId);
    
    /**
     * Get transaction by listing ID
     * @param listingId Listing ID
     * @return Transaction entity
     */
    Optional<Transaction> findByListingId(Long listingId);
    
    /**
     * Get transaction detail
     * @param transactionId Transaction ID
     * @param userId User ID (for authorization)
     * @return Transaction response
     */
    TransactionResponse getTransactionById(Long transactionId, Long userId);
    
    /**
     * Get user's purchase transactions
     * @param userId Buyer user ID
     * @return List of purchase transactions
     */
    List<TransactionResponse> getUserPurchases(Long userId);
    
    /**
     * Get user's sale transactions
     * @param userId Seller user ID
     * @return List of sale transactions
     */
    List<TransactionResponse> getUserSales(Long userId);
    
    /**
     * Update transaction status
     * @param transactionId Transaction ID
     * @param userId User ID (for authorization)
     * @param status New transaction status
     * @return Updated transaction response
     */
    TransactionResponse updateTransactionStatus(Long transactionId, Long userId, TransactionStatus status);
    
    /**
     * Mark transaction as completed
     * @param transactionId Transaction ID
     * @param userId User ID (seller)
     * @return Updated transaction response
     */
    TransactionResponse completeTransaction(Long transactionId, Long userId);
    
    /**
     * Cancel transaction
     * @param transactionId Transaction ID
     * @param userId User ID (buyer or seller)
     * @param reason Cancellation reason
     * @return Updated transaction response
     */
    TransactionResponse cancelTransaction(Long transactionId, Long userId, String reason);
    
    /**
     * Check if user is part of transaction
     * @param transactionId Transaction ID
     * @param userId User ID
     * @return true if buyer or seller, false otherwise
     */
    boolean isParticipant(Long transactionId, Long userId);
}
