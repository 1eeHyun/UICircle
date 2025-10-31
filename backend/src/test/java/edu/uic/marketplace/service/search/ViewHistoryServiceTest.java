package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.search.ViewHistory;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.search.ViewHistoryRepository;
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
@DisplayName("ViewHistoryService Unit Test")
class ViewHistoryServiceTest {

    @Mock
    private ViewHistoryRepository viewHistoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ViewHistoryServiceImpl viewHistoryService;

    private User testUser;
    private Listing testListing;
    private ViewHistory viewHistory;
    private ViewHistory.ViewHistoryId viewHistoryId;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .email("user@uic.edu")
                .build();

        testListing = Listing.builder()
                .listingId(1L)
                .title("Calculus Textbook")
                .price(new BigDecimal("50.00"))
                .build();

        // Create composite key
        viewHistoryId = new ViewHistory.ViewHistoryId(1L, 1L);

        viewHistory = ViewHistory.builder()
                .id(viewHistoryId)
                .user(testUser)
                .listing(testListing)
                .viewedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Record view - Success")
    void recordView_Success() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(listingService.findById(1L)).thenReturn(Optional.of(testListing));
        when(viewHistoryRepository.save(any(ViewHistory.class))).thenReturn(viewHistory);

        // When
        ViewHistory result = viewHistoryService.recordView(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getListing()).isEqualTo(testListing);
        verify(viewHistoryRepository, times(1)).save(any(ViewHistory.class));
        verify(listingService, times(1)).incrementViewCount(1L);
    }

    @Test
    @DisplayName("Find view history by ID")
    void findById_Success() {
        // Given
        when(viewHistoryRepository.findById(viewHistoryId)).thenReturn(Optional.of(viewHistory));

        // When
        Optional<ViewHistory> result = viewHistoryService.findById(viewHistoryId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(testUser);
        verify(viewHistoryRepository, times(1)).findById(viewHistoryId);
    }

    @Test
    @DisplayName("Delete view history")
    void deleteViewHistory() {
        // Given
        when(viewHistoryRepository.findByUser_UserIdAndListing_ListingId(1L, 1L))
                .thenReturn(Optional.of(viewHistory));

        // When
        viewHistoryService.deleteViewHistory(1L, 1L);

        // Then
        verify(viewHistoryRepository, times(1)).findByUser_UserIdAndListing_ListingId(1L, 1L);
        verify(viewHistoryRepository, times(1)).delete(viewHistory);
    }

    @Test
    @DisplayName("Clear all view history")
    void clearViewHistory() {
        // Given & When
        viewHistoryService.clearViewHistory(1L);

        // Then
        verify(viewHistoryRepository, times(1)).deleteByUser_UserId(1L);
    }

    @Test
    @DisplayName("Check if viewed - True")
    void hasViewed_True() {
        // Given
        when(viewHistoryRepository.existsByUser_UserIdAndListing_ListingId(1L, 1L))
                .thenReturn(true);

        // When
        boolean result = viewHistoryService.hasViewed(1L, 1L);

        // Then
        assertThat(result).isTrue();
        verify(viewHistoryRepository, times(1))
                .existsByUser_UserIdAndListing_ListingId(1L, 1L);
    }

    @Test
    @DisplayName("Check if viewed - False")
    void hasViewed_False() {
        // Given
        when(viewHistoryRepository.existsByUser_UserIdAndListing_ListingId(1L, 1L))
                .thenReturn(false);

        // When
        boolean result = viewHistoryService.hasViewed(1L, 1L);

        // Then
        assertThat(result).isFalse();
        verify(viewHistoryRepository, times(1))
                .existsByUser_UserIdAndListing_ListingId(1L, 1L);
    }

    @Test
    @DisplayName("Get view count for listing")
    void getViewCountForListing() {
        // Given
        when(viewHistoryRepository.countByListing_ListingId(1L)).thenReturn(42L);

        // When
        Long count = viewHistoryService.getViewCountForListing(1L);

        // Then
        assertThat(count).isEqualTo(42L);
        verify(viewHistoryRepository, times(1)).countByListing_ListingId(1L);
    }

    @Test
    @DisplayName("Record view - Update existing record")
    void recordView_UpdateExisting() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(listingService.findById(1L)).thenReturn(Optional.of(testListing));
        when(viewHistoryRepository.findByUser_UserIdAndListing_ListingId(1L, 1L))
                .thenReturn(Optional.of(viewHistory));
        when(viewHistoryRepository.save(any(ViewHistory.class))).thenReturn(viewHistory);

        Instant oldViewedAt = viewHistory.getViewedAt();

        // When
        ViewHistory result = viewHistoryService.recordView(1L, 1L);

        // Then
        assertThat(result.getViewedAt()).isAfter(oldViewedAt);
        verify(viewHistoryRepository, times(1)).save(viewHistory);
        verify(listingService, times(1)).incrementViewCount(1L);
    }
}
