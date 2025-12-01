package edu.uic.marketplace.controller.user.docs;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(
        name = "Profiles",
        description = "Operations for viewing and updating user profiles"
)
public interface ProfileApiDocs {

    // ========= Get profile by username =========

    @Operation(
            summary = "Get profile by username",
            description = """
                    Retrieve the public profile information for the given username.
                    This endpoint can be used to display profile pages.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile found",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or profile not found"
            )
    })
    ResponseEntity<CommonResponse<ProfileResponse>> getProfileByUsername(
            @Parameter(
                    description = "Username of the profile owner",
                    example = "cs_student01"
            )
            @PathVariable String username
    );

    // ========= Get current user's profile =========

    @Operation(
            summary = "Get current user's profile",
            description = """
                    Retrieve the profile information of the currently authenticated user.
                    This endpoint is useful for 'My Profile' page.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile found",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<ProfileResponse>> getMyProfile();

    // ========= Update profile =========

    @Operation(
            summary = "Update profile",
            description = """
                    Update the profile information of the currently authenticated user.
                    Only the owner of the profile can perform this operation.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not allowed to update this profile"
            )
    })
    ResponseEntity<CommonResponse<ProfileResponse>> updateMyProfile(
            @RequestBody(
                    description = "Fields to update in the profile (display name, bio, major, etc.)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateProfileRequest.class))
            )
            UpdateProfileRequest request
    );

    // ========= Upload / update avatar =========

    @Operation(
            summary = "Update profile avatar",
            description = """
                    Update the avatar image URL for the currently authenticated user.
                    The image should be uploaded separately (e.g., to S3), then this endpoint
                    stores the final accessible URL in the profile.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Avatar updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid image URL"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<ProfileResponse>> updateAvatar(
            @RequestBody(
                    description = "Avatar image URL to set on the profile",
                    required = true,
                    content = @Content(schema = @Schema(implementation = String.class))
            )
            String imageUrl
    );
}
