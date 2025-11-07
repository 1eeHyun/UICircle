package edu.uic.marketplace.controller.listing.docs;

import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(
        name = "Listing Categories",
        description = "Endpoints for browsing, managing, and retrieving categories"
)
public interface ListingCategoryDocs {

    @Operation(
            summary = "Get all categories (tree structure)",
            description = "Returns all top-level categories with their subcategories recursively.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved all categories",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<List<CategoryResponse>>> getAllCategories();


    @Operation(
            summary = "Get top-level categories",
            description = "Returns all categories that do not have a parent.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved top-level categories",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<CommonResponse<List<CategoryResponse>>> getTopLevelCategories();


    @Operation(
            summary = "Get subcategories by parent ID",
            description = "Retrieve all direct subcategories under a given parent category.",
            parameters = {
                    @Parameter(name = "parentId", description = "Parent category ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved subcategories",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Parent category not found")
            }
    )
    ResponseEntity<CommonResponse<List<CategoryResponse>>> getSubcategories(
            @PathVariable String parentSlug);


    @Operation(
            summary = "Create a new category (Admin only)",
            description = "Creates a new category. Only admins can perform this operation.",
            parameters = {
                    @Parameter(name = "userId", description = "Admin user ID", required = true),
                    @Parameter(name = "name", description = "Category name", required = true),
                    @Parameter(name = "parentId", description = "Optional parent category ID", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
            }
    )
    ResponseEntity<CommonResponse<CategoryResponse>> createCategory(
            String name,
            String parentSlug);
}
