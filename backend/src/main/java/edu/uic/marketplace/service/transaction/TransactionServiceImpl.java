package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.response.transaction.TransactionResponse;
import edu.uic.marketplace.model.transaction.Transaction;
import edu.uic.marketplace.model.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @Override
    public TransactionResponse createTransaction(Long listingId, Long buyerId, BigDecimal finalPrice) {
        return null;
    }

    @Override
    public Optional<Transaction> findById(Long transactionId) {
        return Optional.empty();
    }

    @Override
    public Optional<Transaction> findByListingId(Long listingId) {
        return Optional.empty();
    }

    @Override
    public TransactionResponse getTransactionById(Long transactionId, Long userId) {
        return null;
    }

    @Override
    public List<TransactionResponse> getUserPurchases(Long userId) {
        return null;
    }

    @Override
    public List<TransactionResponse> getUserSales(Long userId) {
        return null;
    }

    @Override
    public TransactionResponse updateTransactionStatus(Long transactionId, Long userId, TransactionStatus status) {
        return null;
    }

    @Override
    public TransactionResponse completeTransaction(Long transactionId, Long userId) {
        return null;
    }

    @Override
    public TransactionResponse cancelTransaction(Long transactionId, Long userId, String reason) {
        return null;
    }

    @Override
    public boolean isParticipant(Long transactionId, Long userId) {
        return false;
    }
}
