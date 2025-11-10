package edu.uic.marketplace.controller.listing.docs;

import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(
        name = "Listing Favorite",
        description = "Endpoints for toggling favorite and retrieving favorite lists"
)
public interface FavoriteApiDocs {

    @Operation(
            summary = "Toggle favorite",
            description = "Toggle favorite status for the given listing. If it is already favorited, it will be removed; otherwise it will be added. Requires authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Toggled successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Listing not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<Void>> toggle(
            @Parameter(description = "Public ID of the listing to toggle favorite", required = true)
            @PathVariable("publicId") String listingPublicId
    );

    @Operation(
            summary = "Get my favorite listings (paged)",
            description = "Returns a paginated list of the current user's favorited listings. Only ACTIVE & non-deleted listings are included.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Favorites retrieved",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<PageResponse<ListingSummaryResponse>>> getMyFavorites(
            @Parameter(description = "Page number (0-based)") @RequestParam(value = "page", required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(value = "size", required = false) Integer size
    );

    @Operation(
            summary = "Get favorite count for a listing",
            description = "Returns the current favorite count stored on the listing.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count retrieved",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Integer.class))),
                    @ApiResponse(responseCode = "404", description = "Listing not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<Integer>> getFavoriteCount(
            @Parameter(description = "Public ID of the listing", required = true)
            @PathVariable("publicId") String listingPublicId
    );

    @Operation(
            summary = "Get my favorite listing public IDs",
            description = "Returns the list of public IDs for all listings the current user has favorited.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "IDs retrieved",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<List<String>>> getMyFavoriteListingIds();
}
