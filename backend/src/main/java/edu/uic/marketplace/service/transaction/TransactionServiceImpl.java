package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.response.transaction.TransactionResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.transaction.Transaction;
import edu.uic.marketplace.model.transaction.TransactionStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.transaction.TransactionRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AuthValidator authValidator;
    private final ListingValidator listingValidator;

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponse createTransaction(String listingPublicId, String buyerUsername, BigDecimal finalPrice) {

        Listing listing = listingValidator.validateListingByPublicId(listingPublicId);

        // Ensure listing is accepted
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new IllegalStateException("Only active listings may create a transaction");
        }

        // Prevent duplicate transaction
        if (transactionRepository.existsByListing_PublicId(listingPublicId)) {
            throw new IllegalStateException("Transaction already exists for this listing");
        }

        User buyer = authValidator.validateUserByUsername(buyerUsername);

        Transaction tx = Transaction.builder()
                .publicId(UUID.randomUUID().toString())
                .listing(listing)
                .buyer(buyer)
                .finalPrice(finalPrice)
                .status(TransactionStatus.PENDING)
                .build();

        Transaction saved = transactionRepository.save(tx);
        return TransactionResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findByPublicId(String transactionPublicId) {
        return transactionRepository.findByPublicId(transactionPublicId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findByListingPublicId(String listingPublicId) {
        return transactionRepository.findByListingPublicId(listingPublicId);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByPublicId(String transactionPublicId, String username) {

        Transaction tx = transactionRepository.findByPublicId(transactionPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (!isParticipantInternal(tx, username)) {
            throw new IllegalStateException("User is not a participant in this transaction");
        }

        return TransactionResponse.from(tx);
    }

    @Override
    public List<TransactionResponse> getUserPurchases(String username) {

        return transactionRepository.findPurchases(username).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Override
    public List<TransactionResponse> getUserSales(String username) {

        return transactionRepository.findSales(username).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse updateTransactionStatus(String transactionPublicId, String username, TransactionStatus status) {

        Transaction tx = transactionRepository.findByPublicId(transactionPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (!isParticipantInternal(tx, username)) {
            throw new IllegalStateException("Unauthorized action");
        }

        switch (status) {
            case COMPLETED -> tx.complete();
            case CANCELLED -> tx.cancel();
            default -> tx.setStatus(status);
        }

        return TransactionResponse.from(tx);
    }

    @Override
    @Transactional
    public TransactionResponse completeTransaction(String transactionPublicId, String username) {

        Transaction tx = transactionRepository.findByPublicId(transactionPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (!isParticipantInternal(tx, username)) {
            throw new IllegalStateException("Unauthorized");
        }

        // Mark confirmation depending on the user
        if (tx.getBuyer().getUsername().equals(username)) {
            tx.confirmBuyer();
        } else if (tx.getSeller().getUsername().equals(username)) {
            tx.confirmSeller();
        }

        // If both confirmed → mark transaction completed and listing sold
        if (tx.isFullyConfirmed()) {
            tx.complete();  // sets COMPLETED status + timestamp

            Listing listing = tx.getListing();
            listing.setStatus(ListingStatus.SOLD);
        }

        return TransactionResponse.from(tx);
    }

    @Override
    @Transactional
    public TransactionResponse cancelTransaction(String transactionPublicId, String username, String reason) {

        Transaction tx = transactionRepository.findByPublicId(transactionPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (!isParticipantInternal(tx, username)) {
            throw new IllegalStateException("Unauthorized");
        }

        if (!tx.isCompleted() && !tx.isCancelled()) {
            tx.cancel();
        }

        return TransactionResponse.from(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isParticipant(String transactionPublicId, String username) {
        return transactionRepository.findByPublicId(transactionPublicId)
                .map(tx -> isParticipantInternal(tx, username))
                .orElse(false);
    }

    private boolean isParticipantInternal(Transaction tx, String username) {
        return tx.getBuyer().getUsername().equals(username) ||
                tx.getSeller().getUsername().equals(username);
    }
}
