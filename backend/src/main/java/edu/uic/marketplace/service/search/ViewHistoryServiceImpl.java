package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import edu.uic.marketplace.model.search.ViewHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViewHistoryServiceImpl implements ViewHistoryService {

    @Override
    public ViewHistory recordView(Long userId, Long listingId) {
        return null;
    }

    @Override
    public Optional<ViewHistory> findById(ViewHistory.ViewHistoryId id) {
        return Optional.empty();
    }

    @Override
    public PageResponse<ViewHistoryResponse> getUserViewHistory(Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public List<ListingSummaryResponse> getRecentlyViewedListings(Long userId, Integer limit) {
        return null;
    }

    @Override
    public void clearViewHistory(Long userId) {

    }

    @Override
    public void deleteViewHistory(Long viewHistoryId, Long userId) {

    }

    @Override
    public boolean hasViewed(Long userId, Long listingId) {
        return false;
    }

    @Override
    public Long getViewCountForListing(Long listingId) {
        return null;
    }
}
