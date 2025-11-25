package edu.uic.marketplace.controller.notification.docs;

import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.notification.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Notifications",
        description = "Endpoints for fetching, reading, and deleting user notifications"
)
public interface NotificationApiDocs {

    // ---------------------- Get All Notifications ----------------------

    @Operation(
            summary = "Get all notifications",
            description = "Retrieve paginated notifications for the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notifications retrieved",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class)))
            }
    )
    ResponseEntity<CommonResponse<PageResponse<NotificationResponse>>> getNotifications(
            @Parameter(description = "Page number (0-indexed)") int page,
            @Parameter(description = "Page size") int size
    );

    // ---------------------- Get Unread Notifications ----------------------

    @Operation(
            summary = "Get unread notifications",
            description = "Retrieve only unread notifications.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unread notifications retrieved")
            }
    )
    ResponseEntity<CommonResponse<PageResponse<NotificationResponse>>> getUnreadNotifications(
            @Parameter(description = "Page number (0-indexed)") int page,
            @Parameter(description = "Page size") int size
    );

    // ---------------------- Unread Count ----------------------

    @Operation(
            summary = "Get unread notification count",
            description = "Returns the number of unread notifications for the authenticated user."
    )
    ResponseEntity<CommonResponse<Long>> getUnreadCount();

    // ---------------------- Mark as Read ----------------------

    @Operation(
            summary = "Mark a notification as read",
            description = "Marks a specific notification as read."
    )
    ResponseEntity<CommonResponse<NotificationResponse>> markAsRead(
            @Parameter(description = "Notification ID") String notificationId
    );

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all notifications as read."
    )
    ResponseEntity<CommonResponse<Void>> markAllAsRead();

    // ---------------------- Delete ----------------------

    @Operation(
            summary = "Delete a notification",
            description = "Deletes a specific notification for the authenticated user."
    )
    ResponseEntity<CommonResponse<Void>> deleteNotification(
            @Parameter(description = "Notification ID") String notificationId
    );

    @Operation(
            summary = "Delete all notifications",
            description = "Deletes all notifications for the authenticated user."
    )
    ResponseEntity<CommonResponse<Void>> deleteAllNotifications();
}
