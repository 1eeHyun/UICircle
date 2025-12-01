package edu.uic.marketplace.controller.search.api;

import edu.uic.marketplace.controller.search.docs.ViewHistoryApiDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import edu.uic.marketplace.service.search.ViewHistoryService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/view-history")
@RequiredArgsConstructor
public class ViewHistoryController implements ViewHistoryApiDocs {

    private final ViewHistoryService viewHistoryService;
    private final AuthValidator authValidator;

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<ViewHistoryResponse>>> getUserViewHistory(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {

        String username = authValidator.extractUsername();
        PageResponse<ViewHistoryResponse> res = viewHistoryService.getUserViewHistory(username, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/recent")
    public ResponseEntity<CommonResponse<List<ListingSummaryResponse>>> getRecentlyViewedListings(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {

        String username = authValidator.extractUsername();
        List<ListingSummaryResponse> res = viewHistoryService.getRecentlyViewedListings(username, limit);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @DeleteMapping("/{listingPublicId}")
    public ResponseEntity<CommonResponse<Void>> deleteViewHistory(
            @PathVariable String listingPublicId) {

        String username = authValidator.extractUsername();
        viewHistoryService.deleteViewHistory(username, listingPublicId);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> clearViewHistory() {

        String username = authValidator.extractUsername();
        viewHistoryService.clearViewHistory(username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/viewed/{listingPublicId}")
    public ResponseEntity<CommonResponse<Boolean>> hasViewed(
            @PathVariable String listingPublicId) {

        String username = authValidator.extractUsername();
        boolean res = viewHistoryService.hasViewed(username, listingPublicId);

        return ResponseEntity.ok(CommonResponse.success(res));
    }
}
