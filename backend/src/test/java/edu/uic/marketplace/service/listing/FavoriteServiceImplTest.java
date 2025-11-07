package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.Favorite;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.FavoriteRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock FavoriteRepository favoriteRepository;
    @Mock AuthValidator authValidator;
    @Mock ListingValidator listingValidator;
    @Mock ListingService listingService; // required for constructor injection

    @InjectMocks FavoriteServiceImpl sut;

    private User user;
    private Listing listing;

    @BeforeEach
    void setUp() {
        user = new User();
        listing = mock(Listing.class);
    }

    @Nested
    @DisplayName("toggleFavorite()")
    class ToggleFavorite {

        @Test
        @DisplayName("should add favorite and increase count if not exists")
        void addsFavoriteWhenNotExists() {

            // given
            String username = "lee";
            String publicId = "pub-1";
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(publicId)).thenReturn(listing);
            when(favoriteRepository.existsByUserAndListing(user, listing)).thenReturn(false);

            // when
            sut.toggleFavorite(username, publicId);

            // then
            verify(favoriteRepository).save(any(Favorite.class));
            verify(listing).incrementFavoriteCount();
            verify(favoriteRepository, never()).deleteByUserAndListing(any(), any());
            verify(listing, never()).decrementFavoriteCount();
        }

        @Test
        @DisplayName("should remove favorite and decrease count if already exists")
        void removesFavoriteWhenExists() {

            // given
            String username = "lee";
            String publicId = "pub-1";
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(publicId)).thenReturn(listing);
            when(favoriteRepository.existsByUserAndListing(user, listing)).thenReturn(true);

            // when
            sut.toggleFavorite(username, publicId);

            // then
            verify(favoriteRepository).deleteByUserAndListing(user, listing);
            verify(listing).decrementFavoriteCount();
            verify(favoriteRepository, never()).save(any());
            verify(listing, never()).incrementFavoriteCount();
        }
    }

    @Nested
    @DisplayName("isFavorite()")
    class IsFavorite {

        @Test
        @DisplayName("should return true when favorite exists")
        void returnsTrueWhenFavorited() {

            String username = "lee";
            String publicId = "pub-1";
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(publicId)).thenReturn(listing);
            when(favoriteRepository.existsByUserAndListing(user, listing)).thenReturn(true);

            boolean result = sut.isFavorited(username, publicId);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false when favorite does not exist")
        void returnsFalseWhenNotFavorited() {

            String username = "lee";
            String publicId = "pub-1";
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(publicId)).thenReturn(listing);
            when(favoriteRepository.existsByUserAndListing(user, listing)).thenReturn(false);

            boolean result = sut.isFavorited(username, publicId);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getUserFavorites()")
    class GetUserFavorites {

        @Test
        @DisplayName("should return paged active favorites only")
        void returnsActiveFavorites() {

            // given
            String username = "lee";

            try {
                Field f = User.class.getDeclaredField("userId");
                f.setAccessible(true);
                f.set(user, 10L);
            } catch (Exception ignore) {}

            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            Listing active = new Listing();
            active.setStatus(ListingStatus.ACTIVE);
            Listing inactive = new Listing();
            inactive.setStatus(ListingStatus.INACTIVE);

            Favorite f1 = Favorite.builder().user(user).listing(active).build();
            Favorite f2 = Favorite.builder().user(user).listing(inactive).build();

            Page<Favorite> page = new PageImpl<>(List.of(f1, f2));

            when(favoriteRepository.findByUser_UserId(anyLong(), any(Pageable.class)))
                    .thenReturn(page);

            // when
            PageResponse<ListingSummaryResponse> res = sut.getUserFavorites(username, 0, 10);

            // then
            assertThat(res.getContent()).hasSize(1);
            assertThat(res.getContent().get(0).getIsFavorite()).isTrue();
            verify(favoriteRepository).findByUser_UserId(anyLong(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getFavoriteCount()")
    class GetFavoriteCount {

        @Test
        @DisplayName("should return favoriteCount from listing")
        void returnsFavoriteCount() {
            String publicId = "pub-1";
            when(listingValidator.validateActiveListingByPublicId(publicId)).thenReturn(listing);
            when(listing.getFavoriteCount()).thenReturn(7);

            Integer result = sut.getFavoriteCount(publicId);

            assertThat(result).isEqualTo(7);
            verify(listingValidator).validateActiveListingByPublicId(publicId);
        }
    }
}
