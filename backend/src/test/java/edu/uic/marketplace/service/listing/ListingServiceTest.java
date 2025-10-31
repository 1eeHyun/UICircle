package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.model.listing.ItemCondition;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.repository.listing.FavoriteRepository;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.service.user.UserService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Mock private ListingRepository listingRepository;
    @Mock private UserService userService;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AuthValidator authValidator;
    @Mock private CategoryValidator categoryValidator;
    @Mock private ListingValidator listingValidator;
    @Mock private FavoriteRepository favoriteRepository;

    @InjectMocks
    private ListingServiceImpl listingService;

    private User testUser;
    private Listing testListing;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .email("seller@uic.edu")
                .build();

        testCategory = Category.builder()
                .categoryId(1L)
                .name("Book")
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

        when(authValidator.validateUserById(1L)).thenReturn(testUser);
        when(categoryValidator.validateCategoryExists(1L)).thenReturn(testCategory);

        // Validate Listing after save
        when(listingRepository.save(any(Listing.class))).thenAnswer(inv -> {
            Listing l = inv.getArgument(0);
            assertThat(l.getSeller()).isNotNull();
            assertThat(l.getSeller().getUserId()).isEqualTo(1L);
            assertThat(l.getTitle()).isEqualTo("Calculus Textbook");

            // Validate Category
            assertThat(l.getCategory()).isEqualTo(testCategory);

            l.setListingId(1L);
            return l;
        });

        // When
        ListingResponse response = listingService.createListing(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getListingId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Calculus Textbook");

        ArgumentCaptor<Listing> captor = ArgumentCaptor.forClass(Listing.class);
        verify(listingRepository, times(1)).save(captor.capture());
        Listing saved = captor.getValue();
        assertThat(saved.getSeller()).isNotNull();
        assertThat(saved.getSeller().getUserId()).isEqualTo(1L);
        assertThat(saved.getStatus()).isEqualTo(ListingStatus.ACTIVE);
        assertThat(saved.getCondition()).isEqualTo(ItemCondition.LIKE_NEW);
        assertThat(saved.getPrice()).isEqualByComparingTo("50.00");
        assertThat(saved.getLatitude()).isEqualTo(41.8781);
        assertThat(saved.getLongitude()).isEqualTo(-87.6298);
    }

    @Test
    @DisplayName("Update listing - Success (dirty checking, no save)")
    void updateListing_Success() {

        // Given
        UpdateListingRequest request = UpdateListingRequest.builder()
                .title("Updated Title")
                .price(new BigDecimal("45.00"))
                .build();

        User testUser = User.builder().userId(1L).email("seller@uic.edu").build();
        Category testCategory = Category.builder().categoryId(1L).name("Book").build();
        Listing testListing = Listing.builder()
                .listingId(1L)
                .seller(testUser)
                .category(testCategory)
                .title("Old Title")
                .price(new BigDecimal("50.00"))
                .status(ListingStatus.ACTIVE)
                .build();

        when(authValidator.validateUserById(1L)).thenReturn(testUser);
        when(listingValidator.validateListing(1L)).thenReturn(testListing);
        doNothing().when(listingValidator).validateSellerOwnership(testUser, testListing.getSeller());
        when(favoriteRepository.existsByUser_UserIdAndListing_ListingId(1L, 1L)).thenReturn(false);

        // When
        ListingResponse response = listingService.updateListing(1L, 1L, request);

        // Then
        assertThat(testListing.getTitle()).isEqualTo("Updated Title");
        assertThat(testListing.getPrice()).isEqualByComparingTo("45.00");
        assertThat(response.getTitle()).isEqualTo("Updated Title");
        assertThat(response.getPrice()).isEqualByComparingTo("45.00");

        verify(listingRepository, never()).save(any());
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
