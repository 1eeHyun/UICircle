package edu.uic.marketplace.controller.listing.api;

import edu.uic.marketplace.controller.listing.docs.ListingApiDocs;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController implements ListingApiDocs {

    private final AuthValidator authValidator;
    private final ListingService listingService;

    /* -------------------- Create / Update -------------------- */

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<ListingResponse>> create(
            @Valid @RequestPart("request") CreateListingRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        String username = authValidator.extractUsername();
        ListingResponse res = listingService.createListing(username, request, images);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<ListingResponse>> update(
            @RequestParam("publicId") String publicId,
            @Valid @RequestPart("request") UpdateListingRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        String username = authValidator.extractUsername();
        ListingResponse res = listingService.updateListing(publicId, username, request, images);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    /* -------------------- State changes -------------------- */

    @Override
    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> delete(
            @RequestParam("publicId") String publicId) {

        String username = authValidator.extractUsername();
        listingService.deleteListing(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PatchMapping("/inactivate")
    public ResponseEntity<CommonResponse<Void>> inactivate(
            @RequestParam("publicId") String publicId) {

        String username = authValidator.extractUsername();
        listingService.inactivateListing(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PatchMapping("/reactivate")
    public ResponseEntity<CommonResponse<Void>> reactivate(
            @RequestParam("publicId") String publicId) {

        String username = authValidator.extractUsername();
        listingService.reactivateListing(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PatchMapping("/sold")
    public ResponseEntity<CommonResponse<Void>> markAsSold(
            @RequestParam("publicId") String publicId) {

        String username = authValidator.extractUsername();
        listingService.markAsSold(publicId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    /* -------------------- Single read -------------------- */

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<ListingResponse>> getByPublicId(
            @RequestParam("publicId") String publicId) {

        String username = authValidator.extractUsername();
        ListingResponse res = listingService.getListingByPublicId(publicId, username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/seller")
    public ResponseEntity<CommonResponse<ListingResponse>> getForSeller(
            @RequestParam("publicId") String publicId) {

        String sellerPublicId = authValidator.extractUsername();
        ListingResponse res = listingService.getListingForSeller(publicId, sellerPublicId);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/admin")
    public ResponseEntity<CommonResponse<ListingResponse>> getForAdmin(
            @RequestParam("publicId") String publicId) {

        return null;

//        ListingResponse res = listingService.getListingForAdmin(publicId);
//        return ResponseEntity.ok(CommonResponse.success(res));
    }

    /* -------------------- Collections -------------------- */

    @Override
    @GetMapping("/active")
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getAllActiveListings(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection) {

        String username = authValidator.extractUsername();
        PageResponse<ListingSummaryResponse> res =
                listingService.getAllActiveListings(username, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/category")
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getByCategory(
            @RequestParam("categorySlug") String categorySlug,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection) {

        String username = authValidator.extractUsername();
        PageResponse<ListingSummaryResponse> res =
                listingService.getListingsByCategory(username, categorySlug, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/seller/list")
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getBySeller(
            @RequestParam("sellerPublicId") String sellerPublicId,
            @RequestParam(value = "status", required = false) ListingStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        PageResponse<ListingSummaryResponse> res =
                listingService.getListingsBySeller(sellerPublicId, status, page, size);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PostMapping("/search")
    public ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> search(
            @Valid @RequestBody SearchListingRequest request) {

        PageResponse<ListingSummaryResponse> res = listingService.searchListings(request);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/nearby")
    public ResponseEntity<CommonResponse<List<ListingSummaryResponse>>> getNearby(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "radiusMiles", required = false, defaultValue = "10") Double radiusMiles,
            @RequestParam(value = "categorySlug", required = false) String categorySlug) {

        List<ListingSummaryResponse> res =
                listingService.getNearbyListings(latitude, longitude, radiusMiles, categorySlug);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    /* -------------------- Counts -------------------- */

    @Override
    @GetMapping("/count/seller")
    public ResponseEntity<CommonResponse<Long>> getListingCountBySeller(
            @RequestParam("sellerPublicId") String sellerPublicId) {

        Long res = listingService.getListingCountBySeller(sellerPublicId);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/count/seller/status")
    public ResponseEntity<CommonResponse<Long>> getListingCountBySellerAndStatus(
            @RequestParam("sellerPublicId") String sellerPublicId,
            @RequestParam("status") ListingStatus status) {

        Long res = listingService.getListingCountBySellerAndStatus(sellerPublicId, status);

        return ResponseEntity.ok(CommonResponse.success(res));
    }
}
