package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.FavoriteResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    @Override
    public FavoriteResponse addFavorite(Long userId, Long listingId) {
        return null;
    }

    @Override
    public void removeFavorite(Long userId, Long listingId) {

    }

    @Override
    public boolean isFavorited(Long userId, Long listingId) {
        return false;
    }

    @Override
    public PageResponse<ListingSummaryResponse> getUserFavorites(Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public Long getFavoriteCount(Long listingId) {
        return null;
    }

    @Override
    public List<Long> getUserFavoriteListingIds(Long userId) {
        return null;
    }
}
