package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.model.listing.ItemCondition;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListingService Unit Test")
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ListingServiceImpl listingService;

    private User testUser;
    private Listing testListing;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .email("seller@uic.edu")
                .build();

        testListing = Listing.builder()
                .listingId(1L)
                .seller(testUser)
                .title("Calculus Textbook")
                .description("Barely used")
                .price(new BigDecimal("50.00"))
                .condition(ItemCondition.LIKE_NEW)
                .status(ListingStatus.ACTIVE)
                .viewCount(0)
                .favoriteCount(0)
                .latitude(41.8781)
                .longitude(-87.6298)
                .build();
    }

    @Test
    @DisplayName("Create listing - Success")
    void createListing_Success() {

        // Given
        CreateListingRequest request = CreateListingRequest.builder()
                .title("Calculus Textbook")
                .description("Barely used textbook in great condition")
                .price(new BigDecimal("50.00"))
                .condition(ItemCondition.LIKE_NEW)
                .categoryId(1L)
                .latitude(41.8781)
                .longitude(-87.6298)
                .isNegotiable(true)
                .build();

        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(listingRepository.save(any(Listing.class))).thenReturn(testListing);

        // When
        ListingResponse response = listingService.createListing(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Calculus Textbook");
        verify(userService, times(1)).findById(1L);
        verify(listingRepository, times(1)).save(any(Listing.class));
    }

    @Test
    @DisplayName("Update listing - Success")
    void updateListing_Success() {

        // Given
        UpdateListingRequest request = UpdateListingRequest.builder()
                .title("Updated Title")
                .price(new BigDecimal("45.00"))
                .build();

        when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));
        when(listingRepository.save(any(Listing.class))).thenReturn(testListing);

        // When
        ListingResponse response = listingService.updateListing(1L, 1L, request);

        // Then
        assertThat(testListing.getTitle()).isEqualTo("Updated Title");
        assertThat(testListing.getPrice()).isEqualByComparingTo(new BigDecimal("45.00"));
        verify(listingRepository, times(1)).save(testListing);
    }

    @Test
    @DisplayName("Delete listing - Success")
    void deleteListing_Success() {

        // Given
        when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

        // When
        listingService.deleteListing(1L, 1L);

        // Then
        assertThat(testListing.getStatus()).isEqualTo(ListingStatus.DELETED);
        verify(listingRepository, times(1)).save(testListing);
    }

    @Test
    @DisplayName("Increment view count")
    void incrementViewCount() {

        // Given
        when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

        // When
        listingService.incrementViewCount(1L);

        // Then
        assertThat(testListing.getViewCount()).isEqualTo(1);
        verify(listingRepository, times(1)).save(testListing);
    }

    @Test
    @DisplayName("Update favorite count")
    void updateFavoriteCount() {

        // Given
        when(listingRepository.findById(1L)).thenReturn(Optional.of(testListing));

        // When
        listingService.updateFavoriteCount(1L, true);

        // Then
        assertThat(testListing.getFavoriteCount()).isEqualTo(1);
        verify(listingRepository, times(1)).save(testListing);
    }
}
