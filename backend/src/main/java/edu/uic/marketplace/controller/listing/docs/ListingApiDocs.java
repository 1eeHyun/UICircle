package edu.uic.marketplace.controller.listing.docs;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.SearchListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.listing.ListingStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name = "Listings",
        description = "Endpoints for creating, updating, browsing, and retrieving marketplace listings"
)
public interface ListingApiDocs {

    // ---------------------------------------------------------------------
    // Create / Update / Delete & Status transitions
    // ---------------------------------------------------------------------

    @Operation(
            summary = "Create a listing",
            description = "Creates a new listing for the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListingResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<ListingResponse>> create(

            @Parameter(description = "Create listing request body", required = true)
            CreateListingRequest request,

            @RequestPart(value = "images", required = false) List<MultipartFile> images
    );

    @Operation(
            summary = "Update a listing",
            description = "Updates an existing listing. Only the seller or an authorized admin can update.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListingResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<ListingResponse>> update(
            @Parameter(description = "Listing public ID", required = true) String publicId,
            @Parameter(description = "Update listing request body", required = true) UpdateListingRequest request,

            @RequestPart(value = "images", required = false) List<MultipartFile> images
    );

    @Operation(
            summary = "Delete (soft) a listing",
            description = "Soft-deletes a listing. Only the seller or an authorized admin can delete.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing soft-deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<Void>> delete(
            @Parameter(description = "Listing public ID", required = true) String publicId
    );

    @Operation(
            summary = "Inactivate a listing",
            description = "Temporarily hides the listing from public feed.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing inactivated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<Void>> inactivate(
            @Parameter(description = "Listing public ID", required = true) String publicId
    );

    @Operation(
            summary = "Reactivate a listing",
            description = "Makes an inactive listing visible again.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing reactivated"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<Void>> reactivate(
            @Parameter(description = "Listing public ID", required = true) String publicId
    );

    @Operation(
            summary = "Mark a listing as sold",
            description = "Marks the listing as SOLD. Only the seller can perform this action.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing marked as sold"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<Void>> markAsSold(
            @Parameter(description = "Listing public ID", required = true) String publicId
    );

    // ---------------------------------------------------------------------
    // Retrieval
    // ---------------------------------------------------------------------

    @Operation(
            summary = "Get listing (public view)",
            description = "Returns a listing by public ID for public view and increments view count.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListingResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<ListingResponse>> getByPublicId(
            @Parameter(description = "Listing public ID", required = true) String publicId
    );

    @Operation(
            summary = "Get listing (seller view)",
            description = "Returns a listing by public ID with seller-specific data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListingResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<ListingResponse>> getForSeller(
            @Parameter(description = "Listing public ID", required = true) String publicId
    );

    @Operation(
            summary = "Get listing (admin view)",
            description = "Returns a listing by public ID with admin-level data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Listing found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListingResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Listing not found")
            }
    )
    ResponseEntity<CommonResponse<ListingResponse>> getForAdmin(
            @Parameter(description = "Listing public ID", required = true) String publicId
    );

    // ---------------------------------------------------------------------
    // Search & Browse
    // ---------------------------------------------------------------------

    @Operation(
            summary = "Get all active listings",
            description = "Returns paginated active listings for the public feed.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved active listings",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getAllActiveListings(

            @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @Parameter(description = "Page size", example = "20") int size,
            @Parameter(description = "Sort field (e.g., createdAt, price)", example = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc, desc)", example = "desc") String sortDirection
    );

    @Operation(
            summary = "Get listings by category",
            description = "Returns paginated listings under the given category slug.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved category listings",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getByCategory(

            @Parameter(description = "Category slug", required = true, example = "electronics") String categorySlug,
            @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @Parameter(description = "Page size", example = "20") int size,
            @Parameter(description = "Sort field (e.g., createdAt, price)", example = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc, desc)", example = "desc") String sortDirection
    );

    @Operation(
            summary = "Get listings by seller",
            description = "Returns paginated listings created by the given seller. Optional filter by status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved seller listings",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Seller not found")
            }
    )
    ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getBySeller(
            @Parameter(description = "Seller public ID", required = true) String sellerPublicId,
            @Parameter(description = "Listing status filter (optional)") ListingStatus status,
            @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @Parameter(description = "Page size", example = "20") int size
    );

    @Operation(
            summary = "Search listings",
            description = "Full search on listings with keyword and filters.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error")
            }
    )
    ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> search(
            @Parameter(description = "Search request with keyword and filters", required = true)
            SearchListingRequest request
    );

    @Operation(
            summary = "Get nearby listings",
            description = "Returns nearby listings within a given radius (in miles) from the provided location. Category filter is optional.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nearby listings",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ListingSummaryResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Validation error")
            }
    )
    ResponseEntity<CommonResponse<List<ListingSummaryResponse>>> getNearby(
            @Parameter(description = "Latitude", required = true, example = "41.8781") Double latitude,
            @Parameter(description = "Longitude", required = true, example = "-87.6298") Double longitude,
            @Parameter(description = "Radius in miles", required = true, example = "10") Double radiusMiles,
            @Parameter(description = "Optional category slug to filter", example = "books") String categorySlug
    );

    // ---------------------------------------------------------------------
    // Statistics / Counters
    // ---------------------------------------------------------------------

    @Operation(
            summary = "Get seller's total listing count",
            description = "Returns the total number of listings for the given seller.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "404", description = "Seller not found")
            }
    )
    ResponseEntity<CommonResponse<Long>> getListingCountBySeller(
            @Parameter(description = "Seller public ID", required = true) String sellerPublicId
    );

    @Operation(
            summary = "Get seller's listing count by status",
            description = "Returns the number of listings for the given seller and status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "404", description = "Seller not found")
            }
    )
    ResponseEntity<CommonResponse<Long>> getListingCountBySellerAndStatus(
            @Parameter(description = "Seller public ID", required = true) String sellerPublicId,
            @Parameter(description = "Listing status", required = true) ListingStatus status
    );
}
