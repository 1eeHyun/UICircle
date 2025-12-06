package edu.uic.marketplace.repository.transaction;

import edu.uic.marketplace.model.transaction.Transaction;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find by transaction publicId (main lookup for API).
     */
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.publicId = :publicId
    """)
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Optional<Transaction> findByPublicId(@Param("publicId") String publicId);


    /**
     * Find by listing publicId.
     */
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.listing.publicId = :listingPublicId
    """)
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Optional<Transaction> findByListingPublicId(@Param("listingPublicId") String listingPublicId);


    /**
     * Check if a transaction exists for given listing (publicId).
     */
    boolean existsByListing_PublicId(String listingPublicId);


    /**
     * Find purchases by username.
     */
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.buyer.username = :username
        ORDER BY t.createdAt DESC
    """)
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Transaction> findPurchases(@Param("username") String username);


    /**
     * Find sales by seller username.
     */
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.listing.seller.username = :sellerUsername
        ORDER BY t.createdAt DESC
    """)
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Transaction> findSales(@Param("sellerUsername") String sellerUsername);
}
