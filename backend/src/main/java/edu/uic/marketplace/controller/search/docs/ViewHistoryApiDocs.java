package edu.uic.marketplace.controller.search.docs;

import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.dto.response.search.ViewHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(
        name = "View History",
        description = "Operations for retrieving and managing listing view history of the authenticated user"
)
public interface ViewHistoryApiDocs {

    // ================= GET PAGINATED VIEW HISTORY =================
    @Operation(
            summary = "Get paginated view history",
            description = """
                    Retrieve paginated view history entries for the authenticated user.
                    Results are ordered by the most recent view time by default.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated view history returned",
                    content = @Content(
                            schema = @Schema(implementation = PageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<PageResponse<ViewHistoryResponse>>> getUserViewHistory(
            @Parameter(
                    description = "Page number (0-based)",
                    example = "0"
            )
            Integer page,
            @Parameter(
                    description = "Page size",
                    example = "20"
            )
            Integer size,
            @Parameter(
                    description = "Sort field (allowed: createdAt, price, viewCount, favoriteCount)",
                    example = "createdAt"
            )
            String sortBy,
            @Parameter(
                    description = "Sort direction (ASC or DESC)",
                    example = "DESC"
            )
            String sortDirection
    );

    // ================= GET RECENTLY VIEWED LISTINGS =================
    @Operation(
            summary = "Get recently viewed listings",
            description = """
                    Retrieve a limited list of recently viewed listings for the authenticated user.
                    This is typically used to show a 'Recently viewed' section in the UI.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recently viewed listings returned",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ListingSummaryResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<List<ListingSummaryResponse>>> getRecentlyViewedListings(
            @Parameter(
                    description = "Maximum number of listings to return",
                    example = "10"
            )
            Integer limit
    );

    // ================= DELETE SINGLE VIEW HISTORY ENTRY =================
    @Operation(
            summary = "Delete a single view history entry",
            description = """
                    Delete a single view history entry for the authenticated user
                    using the listing's publicId.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "View history entry deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "View history entry not found for this user and listing"
            )
    })
    ResponseEntity<CommonResponse<Void>> deleteViewHistory(
            @Parameter(
                    description = "Public identifier of the listing",
                    example = "ls_01JDF3V0ZQ9RXT4K3A2M8N7P6Q"
            )
            @PathVariable String listingPublicId
    );

    // ================= DELETE ALL VIEW HISTORY =================
    @Operation(
            summary = "Clear all view history",
            description = "Delete all view history entries for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "All view history entries deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<Void>> clearViewHistory();

    // ================= CHECK IF LISTING HAS BEEN VIEWED =================
    @Operation(
            summary = "Check if a listing has been viewed",
            description = """
                    Check whether the authenticated user has viewed a specific listing
                    identified by its publicId.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Result of view check",
                    content = @Content(
                            schema = @Schema(implementation = Boolean.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<Boolean>> hasViewed(
            @Parameter(
                    description = "Public identifier of the listing",
                    example = "ls_01JDF3V0ZQ9RXT4K3A2M8N7P6Q"
            )
            @PathVariable String listingPublicId
    );
}
