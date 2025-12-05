package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.request.moderation.ResolveReportRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.moderation.ReportTargetType;

import java.util.Optional;

public interface ReportService {

    /**
     * Create new report
     * @param reporterUsername Reporter username
     * @param request Create report request
     * @return Created report response
     */
    ReportResponse createReport(String reporterUsername, CreateReportRequest request);

    /**
     * Get report by Public ID
     * @param reportPublicId Report Public ID
     * @return Report entity
     */
    Optional<Report> findById(String reportPublicId);

    /**
     * Get all reports (Admin only)
     * @param status Filter by status (nullable)
     * @param page Page number
     * @param size Page size
     * @return Paginated report responses
     */
    PageResponse<ReportResponse> getAllReports(ReportStatus status, Integer page, Integer size);

    /**
     * Get reports by target
     * @param targetType Target type enum
     * @param targetPublicId Target Public ID (UUID)
     * @param page Page number
     * @param size Page size
     * @return Paginated report responses
     */
    PageResponse<ReportResponse> getReportsByTarget(
            ReportTargetType targetType,
            String targetPublicId,
            Integer page,
            Integer size
    );

    /**
     * Get user's submitted reports
     * @param username Reporter username
     * @param page Page number
     * @param size Page size
     * @return Paginated report responses
     */
    PageResponse<ReportResponse> getUserReports(String username, Integer page, Integer size);

    /**
     * Resolve report (Admin only)
     * @param reportPublicId Report Public ID
     * @param adminUsername Admin username
     * @param request Resolve request
     * @return Updated report response
     */
    ReportResponse resolveReport(String reportPublicId, String adminUsername, ResolveReportRequest request);

    /**
     * Check if user has already reported target
     * @param username Username
     * @param targetType Target type enum
     * @param targetPublicId Target Public ID (UUID)
     * @return true if already reported, false otherwise
     */
    boolean hasReported(String username, ReportTargetType targetType, String targetPublicId);

    /**
     * Get pending report count (Admin dashboard)
     * @return Number of pending reports
     */
    Long getPendingReportCount();

    /**
     * Get report count for target
     * @param targetType Target type enum
     * @param targetPublicId Target Public ID (UUID)
     * @return Number of reports
     */
    Long getReportCountForTarget(ReportTargetType targetType, String targetPublicId);
}
