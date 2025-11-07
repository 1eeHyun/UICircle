package edu.uic.marketplace.controller.listing.api;

import edu.uic.marketplace.controller.listing.docs.FavoriteDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.service.listing.FavoriteService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings/favorites")
@RequiredArgsConstructor
public class FavoriteApi implements FavoriteDocs {

    private final FavoriteService favoriteService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping("/toggle")
    public ResponseEntity<CommonResponse<Void>> toggle(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("publicId") String listingPublicId) {

        String username = authValidator.extractUsername(userDetails);
        favoriteService.toggleFavorite(username, listingPublicId);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            Integer page, Integer size) {

        String username = authValidator.extractUsername(userDetails);
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
