//package edu.uic.marketplace.service.listing;
//
//import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
//import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
//import edu.uic.marketplace.dto.response.listing.ListingResponse;
//import edu.uic.marketplace.model.listing.Category;
//import edu.uic.marketplace.model.listing.ItemCondition;
//import edu.uic.marketplace.model.listing.Listing;
//import edu.uic.marketplace.model.listing.ListingStatus;
//import edu.uic.marketplace.model.user.User;
//import edu.uic.marketplace.model.user.UserRole;
//import edu.uic.marketplace.model.user.UserStatus;
//import edu.uic.marketplace.repository.listing.CategoryRepository;
//import edu.uic.marketplace.repository.listing.ListingRepository;
//import edu.uic.marketplace.repository.user.UserRepository;
//import edu.uic.marketplace.support.IntegrationTestSupport;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@DisplayName("ListingService Integration Test")
//class ListingServiceIntegrationTest extends IntegrationTestSupport {
//
//    @Autowired
//    private ListingService listingService;
//
//    @Autowired
//    private ListingRepository listingRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Test
//    @DisplayName("Create a listing full flow")
//    void createListing_FullFlow() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//
//        CreateListingRequest request = CreateListingRequest.builder()
//                .title("Calculus Textbook")
//                .description("Barely used, excellent condition")
//                .price(new BigDecimal("50.00"))
//                .categoryId(category.getCategoryId())
//                .condition(ItemCondition.LIKE_NEW)
//                .latitude(41.8781)
//                .longitude(-87.6298)
//                .build();
//
//        // When
//        ListingResponse response = listingService.createListing(seller.getUserId(), request);
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.getTitle()).isEqualTo("Calculus Textbook");
//        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
//        assertThat(response.getStatus()).isEqualTo(ListingStatus.ACTIVE);
//        assertThat(response.getViewCount()).isEqualTo(0);
//        assertThat(response.getFavoriteCount()).isEqualTo(0);
//
//        // DB Verification
//        Listing savedListing = listingRepository.findById(response.getListingId()).orElseThrow();
//        assertThat(savedListing.getTitle()).isEqualTo("Calculus Textbook");
//        assertThat(savedListing.getSeller().getUserId()).isEqualTo(seller.getUserId());
//    }
//
//    @Test
//    @DisplayName("Update listing full flow")
//    void updateListing_FullFlow() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Original Title", new BigDecimal("50.00"));
//
//        UpdateListingRequest request = UpdateListingRequest.builder()
//                .title("Updated Title")
//                .description("Updated description")
//                .price(new BigDecimal("45.00"))
//                .build();
//
//        // When
//        ListingResponse response = listingService.updateListing(
//                listing.getListingId(), seller.getUserId(), request);
//
//        // Then
//        assertThat(response.getTitle()).isEqualTo("Updated Title");
//        assertThat(response.getDescription()).isEqualTo("Updated description");
//        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("45.00"));
//
//        // DB Verification
//        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
//        assertThat(updatedListing.getTitle()).isEqualTo("Updated Title");
//        assertThat(updatedListing.getPrice()).isEqualByComparingTo(new BigDecimal("45.00"));
//    }
//
//    @Test
//    @DisplayName("Delete a listing full flow (Soft Delete)")
//    void deleteListing_FullFlow() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Test Listing", new BigDecimal("50.00"));
//
//        // When
//        listingService.deleteListing(listing.getListingId(), seller.getUserId());
//
//        // Then
//        Listing deletedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
//        assertThat(deletedListing.getStatus()).isEqualTo(ListingStatus.DELETED);
//        assertThat(deletedListing.getDeletedAt()).isNotNull();
//    }
//
//    @Test
//    @DisplayName("Increment view count")
//    void incrementViewCount() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Test Listing", new BigDecimal("50.00"));
//
//        Integer initialViewCount = listing.getViewCount();
//
//        // When
//        listingService.incrementViewCount(listing.getListingId());
//
//        // Then
//        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
//        assertThat(updatedListing.getViewCount()).isEqualTo(initialViewCount + 1);
//    }
//
//    @Test
//    @DisplayName("Update favorite count increment")
//    void updateFavoriteCount_Increment() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Test Listing", new BigDecimal("50.00"));
//
//        Integer initialFavoriteCount = listing.getFavoriteCount();
//
//        // When
//        listingService.updateFavoriteCount(listing.getListingId(), true);
//
//        // Then
//        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
//        assertThat(updatedListing.getFavoriteCount()).isEqualTo(initialFavoriteCount + 1);
//    }
//
//    @Test
//    @DisplayName("Update favorite count decrement")
//    void updateFavoriteCount_Decrement() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Test Listing", new BigDecimal("50.00"));
//
//        listing.setFavoriteCount(5);
//        listingRepository.save(listing);
//
//        // When
//        listingService.updateFavoriteCount(listing.getListingId(), false);
//
//        // Then
//        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
//        assertThat(updatedListing.getFavoriteCount()).isEqualTo(4);
//    }
//
//    @Test
//    @DisplayName("Update a listing status")
//    void updateListingStatus() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Test Listing", new BigDecimal("50.00"));
//
//        // When
//        ListingResponse response = listingService.updateListingStatus(
//                listing.getListingId(), seller.getUserId(), ListingStatus.SOLD);
//
//        // Then
//        assertThat(response.getStatus()).isEqualTo(ListingStatus.SOLD);
//
//        // DB Verification
//        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
//        assertThat(updatedListing.getStatus()).isEqualTo(ListingStatus.SOLD);
//    }
//
//    @Test
//    @DisplayName("Update a listing - Unauthorized")
//    void updateListing_Unauthorized() {
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        User otherUser = createUser("other@uic.edu", "Jane", "Smith");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Test Listing", new BigDecimal("50.00"));
//
//        UpdateListingRequest request = UpdateListingRequest.builder()
//                .title("Hacked Title")
//                .build();
//
//        // When & Then
//        assertThatThrownBy(() ->
//                listingService.updateListing(listing.getListingId(), otherUser.getUserId(), request))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Unauthorized");
//    }
//
//    @Test
//    @DisplayName("Get a user's listings")
//    void getUserListings() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//
//        createListing(seller, category, "Listing 1", new BigDecimal("50.00"));
//        createListing(seller, category, "Listing 2", new BigDecimal("60.00"));
//        createListing(seller, category, "Listing 3", new BigDecimal("70.00"));
//
//        // When
//        var response = listingService.getUserListings(seller.getUserId(), null, 0, 10);
//
//        // Then
//        assertThat(response.getContent()).hasSize(3);
//        assertThat(response.getTotalElements()).isEqualTo(3);
//    }
//
//    @Test
//    @DisplayName("Increment view count - Multiple")
//    void incrementViewCount_Multiple() {
//
//        // Given
//        User seller = createUser("seller@uic.edu", "John", "Doe");
//        Category category = createCategory("Books", null);
//        Listing listing = createListing(seller, category, "Popular Listing", new BigDecimal("50.00"));
//
//        // When - multiple retrieves
//        listingService.incrementViewCount(listing.getListingId());
//        listingService.incrementViewCount(listing.getListingId());
//        listingService.incrementViewCount(listing.getListingId());
//
//        // Then
//        Listing updatedListing = listingRepository.findById(listing.getListingId()).orElseThrow();
//        assertThat(updatedListing.getViewCount()).isEqualTo(3);
//    }
//
//    // Helper methods
//    private User createUser(String email, String firstName, String lastName) {
//
//        User user = User.builder()
//                .firstName(firstName)
//                .lastName(lastName)
//                .email(email)
//                .passwordHash("hashed_password")
//                .role(UserRole.USER)
//                .status(UserStatus.ACTIVE)
//                .emailVerified(true)
//                .createdAt(Instant.now())
//                .build();
//
//        return userRepository.save(user);
//    }
//
//    private Category createCategory(String name, Category parent) {
//        Category category = Category.builder()
//                .name(name)
//                .parent(parent)
//                .build();
//
//        return categoryRepository.save(category);
//    }
//
//    private Listing createListing(User seller, Category category, String title, BigDecimal price) {
//
//        Listing listing = Listing.builder()
//                .seller(seller)
//                .category(category)
//                .title(title)
//                .description("Test description")
//                .price(price)
//                .condition(ItemCondition.LIKE_NEW)
//                .status(ListingStatus.ACTIVE)
//                .latitude(41.8781)
//                .longitude(-87.6298)
//                .viewCount(0)
//                .favoriteCount(0)
//                .createdAt(Instant.now())
//                .build();
//
//        return listingRepository.save(listing);
//    }
//}
