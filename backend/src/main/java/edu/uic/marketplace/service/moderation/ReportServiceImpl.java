package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.request.moderation.ResolveReportRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    @Override
    public ReportResponse createReport(Long reporterId, CreateReportRequest request) {
        return null;
    }

    @Override
    public Optional<Report> findById(Long reportId) {
        return Optional.empty();
    }

    @Override
    public PageResponse<ReportResponse> getAllReports(ReportStatus status, Integer page, Integer size) {
        return null;
    }

    @Override
    public PageResponse<ReportResponse> getReportsByTarget(String targetType, Long targetId, Integer page, Integer size) {
        return null;
    }

    @Override
    public PageResponse<ReportResponse> getUserReports(Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public ReportResponse resolveReport(Long reportId, Long adminId, ResolveReportRequest request) {
        return null;
    }

    @Override
    public boolean hasReported(Long userId, String targetType, Long targetId) {
        return false;
    }

    @Override
    public Long getPendingReportCount() {
        return null;
    }

    @Override
    public Long getReportCountForTarget(String targetType, Long targetId) {
        return null;
    }
}
