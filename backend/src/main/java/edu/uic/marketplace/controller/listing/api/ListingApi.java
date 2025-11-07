package edu.uic.marketplace.controller.listing.api;

import edu.uic.marketplace.controller.listing.docs.ListingDocs;
import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.service.listing.ListingService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingApi implements ListingDocs {

    private final AuthValidator authValidator;
    private final ListingService listingService;

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<ListingResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid CreateListingRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        String username = authValidator.extractUsername(userDetails);
        ListingResponse res = listingService.createListing(username, request, images);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<ListingResponse>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId,
            @Valid UpdateListingRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        String username = authValidator.extractUsername(userDetails);
        ListingResponse res = listingService.updateListing(publicId, username, request, images);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId) {

        String username = authValidator.extractUsername(userDetails);
        listingService.deleteListing(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> inactivate(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId) {

        String username = authValidator.extractUsername(userDetails);
        listingService.inactivateListing(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> reactivate(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId) {

        String username = authValidator.extractUsername(userDetails);
        listingService.reactivateListing(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> markAsSold(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId) {

        String username = authValidator.extractUsername(userDetails);
        listingService.markAsSold(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<ListingResponse>> getByPublicId(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId) {

        String username = authValidator.extractUsername(userDetails);
        ListingResponse res = listingService.getListingByPublicId(publicId, username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<ListingResponse>> getForSeller(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId) {

        String username = authValidator.extractUsername(userDetails);
        ListingResponse res = listingService.getListingForSeller(publicId, username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<ListingResponse>> getForAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            String publicId) {

        // TODO
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getAllActiveListings(
            int page, int size, String sortBy, String sortDirection) {

        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getByCategory(
            String categorySlug, int page, int size, String sortBy, String sortDirection) {

        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getBySeller(
            String sellerPublicId, ListingStatus status, int page, int size) {
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> search(
            @Valid SearchListingRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<List<ListingSummaryResponse>>> getNearby(
            Double latitude, Double longitude, Double radiusMiles, String categorySlug) {
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getListingCountBySeller(
            String sellerPublicId) {
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getListingCountBySellerAndStatus(
            String sellerPublicId, ListingStatus status) {
        return null;
    }
}
