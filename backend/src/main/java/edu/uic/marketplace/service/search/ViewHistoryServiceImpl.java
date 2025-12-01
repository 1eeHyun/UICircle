package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.search.ViewHistory;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.search.ViewHistoryRepository;
import edu.uic.marketplace.service.common.Utils;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewHistoryServiceImpl implements ViewHistoryService {

    private final AuthValidator authValidator;
    private final ViewHistoryRepository viewHistoryRepository;
    private final ListingValidator listingValidator;

    @Override
    @Transactional
    public ViewHistory recordView(String username, String listingPublicId) {

        User user = authValidator.validateUserByUsername(username);
        Listing listing = listingValidator.validateListingByPublicId(listingPublicId);

        // Check if view history already exists
        Optional<ViewHistory> existingView = viewHistoryRepository
                .findByUsernameAndListingPublicId(username, listingPublicId);

        if (existingView.isPresent()) {
            // Update existing view history
            ViewHistory viewHistory = existingView.get();
            viewHistory.updateViewedAt();
            return viewHistoryRepository.save(viewHistory);
        }

        // Create new view history
        ViewHistory viewHistory = ViewHistory.builder()
                .user(user)
                .listing(listing)
                .build();

        return viewHistoryRepository.save(viewHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ViewHistoryResponse> getUserViewHistory(String username, Integer page, Integer size, String sortBy, String sortDirection) {

        authValidator.validateUserByUsername(username);
        Pageable pageable = Utils.buildPageable(page, size, sortBy, sortDirection);

        Page<ViewHistory> result = viewHistoryRepository.findByUsernameWithListing(username, pageable);

        Page<ViewHistoryResponse> map = result.map(ViewHistoryResponse::from);

        return PageResponse.fromPage(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingSummaryResponse> getRecentlyViewedListings(String username, Integer limit) {

        authValidator.validateUserByUsername(username);

        // Create pageable with limit
        Pageable pageable = PageRequest.of(0, limit);

        // Fetch recent view histories with listing information
        List<ViewHistory> recentViews = viewHistoryRepository
                .findRecentViewsWithListingByUsername(username, pageable);

        // Convert to ListingSummaryResponse
        return recentViews.stream()
                .map(ViewHistory::getListing)
                .map(ListingSummaryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearViewHistory(String username) {

        authValidator.validateUserByUsername(username);
        viewHistoryRepository.deleteByUsername(username);
    }

    @Override
    @Transactional
    public void deleteViewHistory(String username, String listingPublicId) {

        authValidator.validateUserByUsername(username);
        listingValidator.validateListingByPublicId(listingPublicId);
        viewHistoryRepository.deleteByUsernameAndListingPublicId(username, listingPublicId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasViewed(String username, String listingPublicId) {

        authValidator.validateUserByUsername(username);
        listingValidator.validateListingByPublicId(listingPublicId);

        return viewHistoryRepository.existsByUsernameAndListingPublicId(username, listingPublicId);
    }
}