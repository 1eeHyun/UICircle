package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.FavoriteResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.model.listing.ItemCondition;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.repository.listing.FavoriteRepository;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FavoriteService Integration Test")
class FavoriteServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Add favorite full flow")
    void addFavorite_FullFlow() {

        // Given
        User user = createUser("user@uic.edu");
        User seller = createUser("seller@uic.edu");
        Listing listing = createListing(seller);

        // When
        FavoriteResponse response = favoriteService.addFavorite(user.getUserId(), listing.getListingId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(user.getUserId());
        assertThat(response.getListingId()).isEqualTo(listing.getListingId());

        // DB verification
        boolean exists = favoriteRepository.existsByUser_UserIdAndListing_ListingId(
                user.getUserId(), listing.getListingId());
        assertThat(exists).isTrue();

        // Check listing's favoriteCount increment
        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
        assertThat(updatedListing.getFavoriteCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Remove favorite full flow")
    void removeFavorite_FullFlow() {

        // Given
        User user = createUser("user@uic.edu");
        User seller = createUser("seller@uic.edu");
        Listing listing = createListing(seller);
        
        favoriteService.addFavorite(user.getUserId(), listing.getListingId());

        // When
        favoriteService.removeFavorite(user.getUserId(), listing.getListingId());

        // Then
        boolean exists = favoriteRepository.existsByUser_UserIdAndListing_ListingId(
                user.getUserId(), listing.getListingId());
        assertThat(exists).isFalse();

        // Check listing's favoriteCount decrement
        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
        assertThat(updatedListing.getFavoriteCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Check if the item is favorite")
    void isFavorited() {

        // Given
        User user = createUser("user@uic.edu");
        User seller = createUser("seller@uic.edu");
        Listing listing = createListing(seller);
        
        favoriteService.addFavorite(user.getUserId(), listing.getListingId());

        // When
        boolean isFavorited = favoriteService.isFavorited(user.getUserId(), listing.getListingId());

        // Then
        assertThat(isFavorited).isTrue();
    }

    @Test
    @DisplayName("Get user favorites")
    void getUserFavorites() {

        // Given
        User user = createUser("user@uic.edu");
        User seller = createUser("seller@uic.edu");
        
        Listing listing1 = createListing(seller);
        Listing listing2 = createListing(seller);
        Listing listing3 = createListing(seller);
        
        favoriteService.addFavorite(user.getUserId(), listing1.getListingId());
        favoriteService.addFavorite(user.getUserId(), listing2.getListingId());
        favoriteService.addFavorite(user.getUserId(), listing3.getListingId());

        // When
        var response = favoriteService.getUserFavorites(user.getUserId(), 0, 10);

        // Then
        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("Add favorite duplicate")
    void addFavorite_Duplicate() {

        // Given
        User user = createUser("user@uic.edu");
        User seller = createUser("seller@uic.edu");
        Listing listing = createListing(seller);
        
        favoriteService.addFavorite(user.getUserId(), listing.getListingId());

        // When & Then
        assertThatThrownBy(() -> 
                favoriteService.addFavorite(user.getUserId(), listing.getListingId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already favorited");
    }

    // Helper methods
    private User createUser(String email) {

        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        return userRepository.save(user);
    }

    private Listing createListing(User seller) {

        Category category = categoryRepository.save(
                Category.builder().name("Books").parent(null).build());
        
        Listing listing = Listing.builder()
                .seller(seller)
                .category(category)
                .title("Test Listing")
                .description("Test description")
                .price(new BigDecimal("50.00"))
                .condition(ItemCondition.LIKE_NEW)
                .status(ListingStatus.ACTIVE)
                .latitude(41.8781)
                .longitude(-87.6298)
                .viewCount(0)
                .favoriteCount(0)
                .createdAt(Instant.now())
                .build();
        
        return listingRepository.save(listing);
    }
}
