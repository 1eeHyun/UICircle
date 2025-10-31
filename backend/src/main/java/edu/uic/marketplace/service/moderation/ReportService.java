package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.request.moderation.ResolveReportRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;

import java.util.Optional;

/**
 * Report management service interface
 */
public interface ReportService {
    
    /**
     * Create new report
     * @param reporterId Reporter user ID
     * @param request Create report request
     * @return Created report response
     */
    ReportResponse createReport(Long reporterId, CreateReportRequest request);
    
    /**
     * Get report by ID
     * @param reportId Report ID
     * @return Report entity
     */
    Optional<Report> findById(Long reportId);
    
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
     * @param targetType Target type (e.g., "user", "listing")
     * @param targetId Target ID
     * @return List of reports
     */
    PageResponse<ReportResponse> getReportsByTarget(String targetType, Long targetId, Integer page, Integer size);
    
    /**
     * Get user's submitted reports
     * @param userId Reporter user ID
     * @param page Page number
     * @param size Page size
     * @return Paginated report responses
     */
    PageResponse<ReportResponse> getUserReports(Long userId, Integer page, Integer size);
    
    /**
     * Resolve report (Admin only)
     * @param reportId Report ID
     * @param adminId Admin user ID
     * @param request Resolve request
     * @return Updated report response
     */
    ReportResponse resolveReport(Long reportId, Long adminId, ResolveReportRequest request);
    
    /**
     * Check if user has already reported target
     * @param userId User ID
     * @param targetType Target type
     * @param targetId Target ID
     * @return true if already reported, false otherwise
     */
    boolean hasReported(Long userId, String targetType, Long targetId);
    
    /**
     * Get pending report count (Admin dashboard)
     * @return Number of pending reports
     */
    Long getPendingReportCount();
    
    /**
     * Get report count for target
     * @param targetType Target type
     * @param targetId Target ID
     * @return Number of reports
     */
    Long getReportCountForTarget(String targetType, Long targetId);
}
