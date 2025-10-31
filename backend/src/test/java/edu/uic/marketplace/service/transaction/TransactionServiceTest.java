package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.response.transaction.TransactionResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.transaction.Transaction;
import edu.uic.marketplace.model.transaction.TransactionStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.transaction.TransactionRepository;
import edu.uic.marketplace.service.listing.ListingService;
import edu.uic.marketplace.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Test")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ListingService listingService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User buyer;
    private User seller;
    private Listing listing;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        buyer = User.builder().userId(1L).build();
        seller = User.builder().userId(2L).build();
        listing = Listing.builder().listingId(1L).seller(seller).build();
        transaction = Transaction.builder()
                .transactionId(1L)
                .listing(listing)
                .buyer(buyer)
                .finalPrice(new BigDecimal("50.00"))
                .status(TransactionStatus.PENDING)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Create transaction - Success")
    void createTransaction_Success() {
        // Given
        when(listingService.findById(1L)).thenReturn(Optional.of(listing));
        when(userService.findById(1L)).thenReturn(Optional.of(buyer));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        TransactionResponse response = transactionService.createTransaction(1L, 1L, new BigDecimal("50.00"));

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getFinalPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Complete transaction")
    void completeTransaction() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // When
        TransactionResponse response = transactionService.completeTransaction(1L, 2L);

        // Then
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(transaction.getCompletedAt()).isNotNull();
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("Cancel transaction")
    void cancelTransaction() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // When
        TransactionResponse response = transactionService.cancelTransaction(1L, 1L, "Changed mind");

        // Then
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.CANCELLED);
        verify(transactionRepository, times(1)).save(transaction);
    }
}
