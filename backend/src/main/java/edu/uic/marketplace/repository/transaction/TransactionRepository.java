package edu.uic.marketplace.repository.transaction;

import edu.uic.marketplace.model.transaction.Transaction;
import edu.uic.marketplace.model.transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find transaction by listing
     */
    Optional<Transaction> findByListing_ListingId(Long listingId);
    
    /**
     * Find transactions by buyer
     */
    List<Transaction> findByBuyer_UserId(Long buyerId);
    
    /**
     * Find transactions by seller
     */
    List<Transaction> findByListing_Seller_UserId(Long sellerId);
    
    /**
     * Find transactions by buyer and status
     */
    List<Transaction> findByBuyer_UserIdAndStatus(Long buyerId, TransactionStatus status);
    
    /**
     * Find transactions by seller and status
     */
    List<Transaction> findByListing_Seller_UserIdAndStatus(Long sellerId, TransactionStatus status);
    
    /**
     * Count transactions by buyer
     */
    Long countByBuyer_UserId(Long buyerId);
    
    /**
     * Count transactions by seller
     */
    Long countByListing_Seller_UserId(Long sellerId);
    
    /**
     * Check if transaction exists for listing
     */
    boolean existsByListing_ListingId(Long listingId);
}
