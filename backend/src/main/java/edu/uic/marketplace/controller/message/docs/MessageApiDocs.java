package edu.uic.marketplace.controller.message.docs;

import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Messages",
        description = "Endpoints for sending, reading, and deleting messages inside a conversation."
)
public interface MessageApiDocs {

    // ---------------------- Send Message ----------------------

    @Operation(
            summary = "Send a message",
            description = "Sends a new message in the specified conversation."
    )
    ResponseEntity<CommonResponse<MessageResponse>> sendMessage(
            @Parameter(description = "Conversation public ID") String conversationId,
            @RequestBody(
                    description = "Message payload",
                    required = true
            )
            SendMessageRequest request
    );

    // ---------------------- Get Messages in Conversation ----------------------

    @Operation(
            summary = "Get messages in a conversation",
            description = "Retrieve paginated messages belonging to the specified conversation.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Messages retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<CommonResponse<PageResponse<MessageResponse>>> getMessages(
            @Parameter(description = "Conversation public ID") String conversationId,
            @Parameter(description = "Page number (0-indexed)") int page,
            @Parameter(description = "Page size") int size
    );

    // ---------------------- Get Unread Count in a Conversation ----------------------

    @Operation(
            summary = "Get unread message count for this conversation",
            description = "Returns how many messages are unread inside this conversation for the authenticated user."
    )
    ResponseEntity<CommonResponse<Long>> getUnreadCountInConversation(
            @Parameter(description = "Conversation public ID") String conversationId
    );

    // ---------------------- Mark All Messages as Read ----------------------

    @Operation(
            summary = "Mark all messages in this conversation as read",
            description = "Marks every message in the conversation as read for the authenticated user."
    )
    ResponseEntity<CommonResponse<Void>> markConversationAsRead(
            @Parameter(description = "Conversation public ID") String conversationId
    );

    // ---------------------- Mark a Single Message as Read ----------------------

    @Operation(
            summary = "Mark a single message as read",
            description = "Marks one specific message as read. Must be the receiver of the message."
    )
    ResponseEntity<CommonResponse<Void>> markMessageAsRead(
            @Parameter(description = "Conversation public ID") String conversationId,
            @Parameter(description = "Message public ID") String messageId
    );

    // ---------------------- Delete a Message (Soft Delete) ----------------------

    @Operation(
            summary = "Delete a message",
            description = "Soft-deletes a message sent by the authenticated user."
    )
    ResponseEntity<CommonResponse<Void>> deleteMessage(
            @Parameter(description = "Conversation public ID") String conversationId,
            @Parameter(description = "Message public ID") String messageId
    );
}
