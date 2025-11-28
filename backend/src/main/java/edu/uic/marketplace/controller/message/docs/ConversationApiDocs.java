package edu.uic.marketplace.controller.message.docs;

import edu.uic.marketplace.dto.request.message.CreateConversationRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Conversations",
        description = "Endpoints for creating, listing, and managing conversations between buyers and sellers"
)
public interface ConversationApiDocs {

    // ---------------------- Create Conversation ----------------------

    @Operation(
            summary = "Create a conversation for a listing",
            description = """
                    Creates a new conversation between the authenticated user (buyer) 
                    and the listing owner (seller). If a conversation already exists 
                    for this listing and user pair, the existing conversation is returned.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Conversation created or existing conversation returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ConversationResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<CommonResponse<ConversationResponse>> createConversation(
            @RequestBody(
                    description = "Conversation creation payload containing the target listing ID and optional initial message",
                    required = true
            )
            CreateConversationRequest request
    );

    // ---------------------- Get Conversation List ----------------------

    @Operation(
            summary = "Get conversations for current user",
            description = """
                    Retrieve paginated conversations for the authenticated user. 
                    Only conversations that the user has not left (soft-deleted) are returned.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Conversations retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<CommonResponse<PageResponse<ConversationResponse>>> getConversations(
            @Parameter(description = "Page number (0-indexed)") int page,
            @Parameter(description = "Page size") int size
    );

    // ---------------------- Get Conversation Detail ----------------------

    @Operation(
            summary = "Get conversation detail",
            description = """
                    Retrieve a single conversation by its public ID. 
                    The authenticated user must be a participant (buyer or seller).
                    """
    )
    ResponseEntity<CommonResponse<ConversationResponse>> getConversation(
            @Parameter(description = "Conversation public ID") String conversationId
    );

    // ---------------------- Unread Conversation Count ----------------------

    @Operation(
            summary = "Get unread conversation count",
            description = "Returns the number of conversations that contain unread messages for the authenticated user."
    )
    ResponseEntity<CommonResponse<Long>> getUnreadConversationCount();

    // ---------------------- Leave Conversation ----------------------

    @Operation(
            summary = "Leave a conversation",
            description = """
                    Marks the conversation as left for the authenticated user (soft delete). 
                    The conversation will no longer appear in their conversation list and 
                    their unread count for this conversation will be reset to 0.
                    """
    )
    ResponseEntity<CommonResponse<Void>> leaveConversation(
            @Parameter(description = "Conversation public ID") String conversationId
    );
}
