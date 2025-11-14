package edu.uic.marketplace.controller.search.api;

import edu.uic.marketplace.controller.search.docs.SearchApiDocs;
import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;
import edu.uic.marketplace.service.search.SavedSearchService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController implements SearchApiDocs {

    private final SavedSearchService savedSearchService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<SavedSearchResponse>> saveSearch(@RequestBody SaveSearchRequest request) {

        String username = authValidator.extractUsername();
        SavedSearchResponse res = savedSearchService.saveSearch(username, request);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<List<SavedSearchResponse>>> getSavedSearches() {

        String username = authValidator.extractUsername();
        List<SavedSearchResponse> res = savedSearchService.getUserSavedSearchesByUsername(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @DeleteMapping("/{publicId}")
    public ResponseEntity<CommonResponse<Void>> deleteSavedSearch(@PathVariable(value = "publicId") String publicId) {

        String username = authValidator.extractUsername();
        savedSearchService.deleteSavedSearch(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> deleteAllSavedSearches() {

        String username = authValidator.extractUsername();
        savedSearchService.deleteAllForUser(username);

        return ResponseEntity.ok(CommonResponse.success());
    }
}
