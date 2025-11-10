package edu.uic.marketplace.controller.listing.api;

import edu.uic.marketplace.controller.listing.docs.FavoriteApiDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.service.listing.FavoriteService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings/favorites")
@RequiredArgsConstructor
public class FavoriteController implements FavoriteApiDocs {

    private final FavoriteService favoriteService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping("/toggle")
    public ResponseEntity<CommonResponse<Void>> toggle(
            @RequestParam("publicId") String listingPublicId) {

        String username = authValidator.extractUsername();
        favoriteService.toggleFavorite(username, listingPublicId);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getMyFavorites(
            Integer page, Integer size) {

        String username = authValidator.extractUsername();
        PageResponse<ListingSummaryResponse> res = favoriteService.getUserFavorites(username, page, size);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<CommonResponse<Integer>> getFavoriteCount(
            @RequestParam("publicId") String listingPublicId) {

        Integer res = favoriteService.getFavoriteCount(listingPublicId);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<List<String>>> getMyFavoriteListingIds() {
        return null;
    }
}
