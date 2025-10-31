package edu.uic.marketplace.service.search;

import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;
import edu.uic.marketplace.model.search.SavedSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedSearchServiceImpl implements SavedSearchService {

    @Override
    public SavedSearchResponse saveSearch(Long userId, SaveSearchRequest request) {
        return null;
    }

    @Override
    public Optional<SavedSearch> findById(Long savedSearchId) {
        return Optional.empty();
    }

    @Override
    public List<SavedSearchResponse> getUserSavedSearches(Long userId) {
        return null;
    }

    @Override
    public void deleteSavedSearch(Long savedSearchId, Long userId) {

    }

    @Override
    public SavedSearchResponse updateSearchName(Long savedSearchId, Long userId, String name) {
        return null;
    }

    @Override
    public boolean hasSavedSearch(Long userId, String queryHash) {
        return false;
    }
}
