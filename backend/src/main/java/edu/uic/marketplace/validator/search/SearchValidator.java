package edu.uic.marketplace.validator.search;

import edu.uic.marketplace.exception.search.SavedSearchNotFound;
import edu.uic.marketplace.model.search.SavedSearch;
import edu.uic.marketplace.repository.search.SavedSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchValidator {

    private final SavedSearchRepository savedSearchRepository;

    public SavedSearch validatePublicId(String publicId) {
        return savedSearchRepository.findByPublicId(publicId)
                .orElseThrow(() -> new SavedSearchNotFound("Saved search not found"));
    }


}
