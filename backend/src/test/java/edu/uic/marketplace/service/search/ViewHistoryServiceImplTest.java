package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.search.ViewHistory;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.search.ViewHistoryRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewHistoryServiceImplTest {

    @Mock
    private AuthValidator authValidator;

    @Mock
    private ViewHistoryRepository viewHistoryRepository;

    @Mock
    private ListingValidator listingValidator;

    @InjectMocks
    private ViewHistoryServiceImpl viewHistoryService;

    // Helper methods to create test entities
    private User createUser(String username) {
        User user = new User();
        user.setUserId(1L);
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        return user;
    }

    private Listing createListing(String publicId) {
        Listing listing = new Listing();
        listing.setListingId(10L);
        listing.setPublicId(publicId);
        listing.setTitle("Test Listing");
        return listing;
    }

    private ViewHistory createViewHistory(User user, Listing listing) {
        ViewHistory vh = ViewHistory.builder()
                .user(user)
                .listing(listing)
                .build();

        return vh;
    }

    @Nested
    @DisplayName("recordView")
    class RecordViewTests {

        @Test
        @DisplayName("Should create new ViewHistory when it does not exist")
        void recordView_createsNewWhenNotExists() {
            // given
            String username = "testUser";
            String listingPublicId = "listing-123";

            User user = createUser(username);
            Listing listing = createListing(listingPublicId);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);
            when(viewHistoryRepository.findByUsernameAndListingPublicId(username, listingPublicId))
                    .thenReturn(Optional.empty());

            ViewHistory newViewHistory = createViewHistory(user, listing);
            when(viewHistoryRepository.save(any(ViewHistory.class))).thenReturn(newViewHistory);

            // when
            ViewHistory result = viewHistoryService.recordView(username, listingPublicId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUser()).isEqualTo(user);
            assertThat(result.getListing()).isEqualTo(listing);

            // verify interactions
            verify(authValidator).validateUserByUsername(username);
            verify(listingValidator).validateListingByPublicId(listingPublicId);
            verify(viewHistoryRepository).findByUsernameAndListingPublicId(username, listingPublicId);
            verify(viewHistoryRepository).save(any(ViewHistory.class));
        }

        @Test
        @DisplayName("Should update existing ViewHistory when already exists")
        void recordView_updatesExistingWhenExists() {
            // given
            String username = "testUser";
            String listingPublicId = "listing-123";

            User user = createUser(username);
            Listing listing = createListing(listingPublicId);
            ViewHistory existing = createViewHistory(user, listing);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);
            when(viewHistoryRepository.findByUsernameAndListingPublicId(username, listingPublicId))
                    .thenReturn(Optional.of(existing));
            when(viewHistoryRepository.save(existing)).thenReturn(existing);

            // when
            ViewHistory result = viewHistoryService.recordView(username, listingPublicId);

            // then
            assertThat(result).isSameAs(existing);

            // verify interactions
            verify(authValidator).validateUserByUsername(username);
            verify(listingValidator).validateListingByPublicId(listingPublicId);
            verify(viewHistoryRepository).findByUsernameAndListingPublicId(username, listingPublicId);
            verify(viewHistoryRepository).save(existing);
        }
    }

    @Nested
    @DisplayName("getUserViewHistory")
    class GetUserViewHistoryTests {

        @Test
        @DisplayName("Should return paginated view history for user")
        void getUserViewHistory_returnsPageResponse() {
            // given
            String username = "testUser";
            int page = 0;
            int size = 10;
            String sortBy = "createdAt";
            String sortDirection = "DESC";

            User user = createUser(username);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            Listing listing = createListing("listing-1");
            ViewHistory vh = createViewHistory(user, listing);

            Pageable pageable = PageRequest.of(page, size);
            Page<ViewHistory> pageResult = new PageImpl<>(List.of(vh), pageable, 1);

            when(viewHistoryRepository.findByUsernameWithListing(eq(username), any(Pageable.class)))
                    .thenReturn(pageResult);

            // when
            PageResponse<ViewHistoryResponse> response =
                    viewHistoryService.getUserViewHistory(username, page, size, sortBy, sortDirection);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getListing().getPublicId())
                    .isEqualTo(listing.getPublicId());

            verify(authValidator).validateUserByUsername(username);
            verify(viewHistoryRepository).findByUsernameWithListing(eq(username), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getRecentlyViewedListings")
    class GetRecentlyViewedListingsTests {

        @Test
        @DisplayName("Should return recently viewed listings with given limit")
        void getRecentlyViewedListings_returnsList() {
            // given
            String username = "testUser";
            int limit = 5;

            User user = createUser(username);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            Listing listing1 = createListing("listing-1");
            Listing listing2 = createListing("listing-2");

            ViewHistory vh1 = createViewHistory(user, listing1);
            ViewHistory vh2 = createViewHistory(user, listing2);

            when(viewHistoryRepository.findRecentViewsWithListingByUsername(eq(username), any(Pageable.class)))
                    .thenReturn(List.of(vh1, vh2));

            // when
            List<ListingSummaryResponse> result =
                    viewHistoryService.getRecentlyViewedListings(username, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getPublicId()).isEqualTo("listing-1");
            assertThat(result.get(1).getPublicId()).isEqualTo("listing-2");

            verify(authValidator).validateUserByUsername(username);
            verify(viewHistoryRepository)
                    .findRecentViewsWithListingByUsername(eq(username), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("clearViewHistory")
    class ClearViewHistoryTests {

        @Test
        @DisplayName("Should delete all view history for user")
        void clearViewHistory_deletesAllForUser() {
            // given
            String username = "testUser";
            User user = createUser(username);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            // when
            viewHistoryService.clearViewHistory(username);

            // then
            verify(authValidator).validateUserByUsername(username);
            verify(viewHistoryRepository).deleteByUsername(username);
        }
    }

    @Nested
    @DisplayName("deleteViewHistory")
    class DeleteViewHistoryTests {

        @Test
        @DisplayName("Should delete specific view history by username and listingPublicId")
        void deleteViewHistory_deletesSpecificEntry() {
            // given
            String username = "testUser";
            String listingPublicId = "listing-123";

            User user = createUser(username);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(listingPublicId))
                    .thenReturn(createListing(listingPublicId));

            // when
            viewHistoryService.deleteViewHistory(username, listingPublicId);

            // then
            verify(authValidator).validateUserByUsername(username);
            verify(listingValidator).validateListingByPublicId(listingPublicId);
            verify(viewHistoryRepository).deleteByUsernameAndListingPublicId(username, listingPublicId);
        }
    }

    @Nested
    @DisplayName("hasViewed")
    class HasViewedTests {

        @Test
        @DisplayName("Should return true when user has viewed the listing")
        void hasViewed_returnsTrue() {
            // given
            String username = "testUser";
            String listingPublicId = "listing-123";

            User user = createUser(username);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(listingPublicId))
                    .thenReturn(createListing(listingPublicId));
            when(viewHistoryRepository.existsByUsernameAndListingPublicId(username, listingPublicId))
                    .thenReturn(true);

            // when
            boolean result = viewHistoryService.hasViewed(username, listingPublicId);

            // then
            assertThat(result).isTrue();
            verify(authValidator).validateUserByUsername(username);
            verify(listingValidator).validateListingByPublicId(listingPublicId);
            verify(viewHistoryRepository).existsByUsernameAndListingPublicId(username, listingPublicId);
        }

        @Test
        @DisplayName("Should return false when user has not viewed the listing")
        void hasViewed_returnsFalse() {
            // given
            String username = "testUser";
            String listingPublicId = "listing-123";

            User user = createUser(username);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(listingPublicId))
                    .thenReturn(createListing(listingPublicId));
            when(viewHistoryRepository.existsByUsernameAndListingPublicId(username, listingPublicId))
                    .thenReturn(false);

            // when
            boolean result = viewHistoryService.hasViewed(username, listingPublicId);

            // then
            assertThat(result).isFalse();
            verify(authValidator).validateUserByUsername(username);
            verify(listingValidator).validateListingByPublicId(listingPublicId);
            verify(viewHistoryRepository).existsByUsernameAndListingPublicId(username, listingPublicId);
        }
    }
}
