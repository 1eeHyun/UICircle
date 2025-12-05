package edu.uic.marketplace.controller.moderation.docs;

import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.request.moderation.ResolveReportRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.moderation.ReportTargetType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Reports",
        description = "Endpoints for creating and managing reports"
)
public interface ReportApiDocs {

    @Operation(
            summary = "Create a new report",
            description = "Allows a user to report a target (listing, comment, user, etc.)."
    )
    ResponseEntity<CommonResponse<ReportResponse>> createReport(
            @RequestBody @Parameter(description = "Report creation data") CreateReportRequest request
    );

    @Operation(
            summary = "Get all reports",
            description = "Returns a paginated list of reports. This is for admin/moderator use."
    )
    ResponseEntity<CommonResponse<PageResponse<ReportResponse>>> getAllReports(
            @Parameter(description = "Report status filter (PENDING, RESOLVED)") ReportStatus status,
            @Parameter(description = "Page number (0-indexed)") Integer page,
            @Parameter(description = "Page size") Integer size
    );

    @Operation(
            summary = "Get reports for a target",
            description = "Returns reports for a specific target by type and public id."
    )
    ResponseEntity<CommonResponse<PageResponse<ReportResponse>>> getReportsByTarget(
            @Parameter(description = "Target type") ReportTargetType targetType,
            @Parameter(description = "Target public id") String targetPublicId,
            @Parameter(description = "Page number (0-indexed)") Integer page,
            @Parameter(description = "Page size") Integer size
    );

    @Operation(
            summary = "Get user-created reports",
            description = "Returns a paginated list of reports created by the authenticated user."
    )
    ResponseEntity<CommonResponse<PageResponse<ReportResponse>>> getMyReports(
            @Parameter(description = "Page number (0-indexed)") Integer page,
            @Parameter(description = "Page size") Integer size
    );

    @Operation(
            summary = "Resolve a report (admin only)",
            description = "Marks a report as resolved by admin with decision details."
    )
    ResponseEntity<CommonResponse<ReportResponse>> resolveReport(
            @Parameter(description = "Report public id") String reportPublicId,
            @Parameter(description = "Admin username") String adminUsername,
            @RequestBody @Parameter(description = "Resolve action details") ResolveReportRequest request
    );

    @Operation(
            summary = "Check if a user has already reported a target",
            description = "Returns true if the user has already reported the target."
    )
    ResponseEntity<CommonResponse<Boolean>> hasReported(
            @Parameter(description = "Authenticated username") String username,
            @Parameter(description = "Target type") ReportTargetType targetType,
            @Parameter(description = "Target public id") String targetPublicId
    );

    @Operation(
            summary = "Get number of pending reports",
            description = "Returns the count of unresolved reports (PENDING)."
    )
    ResponseEntity<CommonResponse<Long>> getPendingReportCount();

    @Operation(
            summary = "Get report count for specific target",
            description = "Returns how many reports exist for the given target."
    )
    ResponseEntity<CommonResponse<Long>> getReportCountForTarget(
            @Parameter(description = "Target type") ReportTargetType targetType,
            @Parameter(description = "Target public id") String targetPublicId
    );
}
