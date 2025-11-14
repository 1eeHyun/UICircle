package edu.uic.marketplace.controller.search.docs;

import edu.uic.marketplace.dto.request.search.SaveSearchRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.search.SavedSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(
        name = "Search",
        description = "Operations for saving, retrieving, and deleting saved searches"
)
public interface SearchApiDocs {

    // ================= SAVE SEARCH =================
    @Operation(
            summary = "Save a search",
            description = """
                    Save a search entry for the authenticated user.
                    If the combination of (query + filters) already exists,
                    the existing saved search will be updated with the new name.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search saved successfully",
                    content = @Content(
                            schema = @Schema(implementation = SavedSearchResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or malformed filter JSON"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<SavedSearchResponse>> saveSearch(
            @RequestBody(
                    description = "Search name, query string, and filter JSON",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SaveSearchRequest.class))
            )
            SaveSearchRequest request
    );

    // ================= GET SAVED SEARCHES =================
    @Operation(
            summary = "Get saved searches",
            description = "Retrieve all saved searches for the authenticated user, ordered by creation date (newest first)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of saved searches",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = SavedSearchResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<List<SavedSearchResponse>>> getSavedSearches();

    // ================= DELETE SINGLE SEARCH =================
    @Operation(
            summary = "Delete a saved search",
            description = "Delete a saved search using its publicId."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not own this saved search"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Saved search not found"
            )
    })
    ResponseEntity<CommonResponse<Void>> deleteSavedSearch(
            @Parameter(
                    description = "Public identifier of the saved search",
                    example = "ss_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String publicId
    );

    // ================= DELETE ALL SEARCHES =================
    @Operation(
            summary = "Delete all saved searches",
            description = "Delete all saved searches for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "All saved searches deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<Void>> deleteAllSavedSearches();
}
