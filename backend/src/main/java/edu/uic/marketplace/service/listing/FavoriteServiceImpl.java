package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.common.util.PageMapper;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.Favorite;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.FavoriteRepository;
import edu.uic.marketplace.service.notification.NotificationService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final AuthValidator authValidator;
    private final ListingValidator listingValidator;

    private final NotificationService notificationService;

    @Override
    @Transactional
    public void toggleFavorite(String username, String listingPublicId) {

        // 1) validate
        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(listingPublicId);

        // 2) already favorite -> remove
        boolean exists = favoriteRepository.existsByUserAndListing(user, listing);
        if (exists) {
            favoriteRepository.deleteByUserAndListing(user, listing);
            listing.decrementFavoriteCount();
            return;
        }

        // 3) add
        Favorite fav = Favorite.builder()
                .user(user)
                .listing(listing)
                .build();
        favoriteRepository.save(fav);
        listing.incrementFavoriteCount();

        // 4) Send notification to seller
        if (user != listing.getSeller()) {
            notificationService.notifyListingFavorited(
                    listing.getSeller().getUsername(),
                    username,
                    listing.getPublicId()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(String username, String listingPublicId) {

        // 1) validate
        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(listingPublicId);

        return favoriteRepository.existsByUserAndListing(user, listing);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> getUserFavorites(String username, Integer page, Integer size) {

        User user = authValidator.validateUserByUsername(username);

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 20 : size;

        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Favorite> favPage = favoriteRepository.findByUser_Username(user.getUsername(), pageable);

        List<ListingSummaryResponse> content = favPage.getContent().stream()
                .map(Favorite::getListing)
                .filter(l -> l.getDeletedAt() == null && l.getStatus() == ListingStatus.ACTIVE)
                .map(l -> ListingSummaryResponse.from(l, true))
                .toList();

        return PageMapper.toPageResponse(favPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getFavoriteCount(String listingPublicId) {

        Listing listing = listingValidator.validateActiveListingByPublicId(listingPublicId);
        return listing.getFavoriteCount();
    }

    @Override
    public List<String> getUserFavoriteListingPublicIds(String username) {

        User user = authValidator.validateUserByUsername(username);
        return favoriteRepository.findByUser(user).stream()
                .map(fav -> fav.getListing().getPublicId())
                .toList();
    }

    @Override
    public Set<String> getFavoritedListingIds(String username, List<String> listingPublicIds) {
        if (listingPublicIds == null || listingPublicIds.isEmpty()) {
            return Collections.emptySet();
        }

        List<String> favoritedIds = favoriteRepository.findFavoritedListingPublicIds(
                username,
                listingPublicIds
        );

        return favoritedIds.stream().collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavoritedWithIds(Long userId, Long listingId) {
        return favoriteRepository.existsById_UserIdAndId_ListingId(userId, listingId);
    }
}
