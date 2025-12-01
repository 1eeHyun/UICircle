package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.common.util.PageMapper;
import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.request.moderation.ResolveReportRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.moderation.ReportTargetType;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.moderation.ReportRepository;
import edu.uic.marketplace.service.common.Utils;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final AuthValidator authValidator;

    @Override
    @Transactional
    public ReportResponse createReport(String reporterUsername, CreateReportRequest request) {

        User user = authValidator.validateUserByUsername(reporterUsername);

        boolean isExist = reportRepository.existsByReporter_UsernameAndTargetTypeAndTargetPublicId(
                reporterUsername, request.getTargetType(), request.getTargetPublicId());

        if (isExist) {
            throw new IllegalArgumentException("You already reported it.");
        }

        Report build = Report.builder()
                .reporter(user)
                .targetType(request.getTargetType())
                .targetPublicId(request.getTargetPublicId())
                .reason(request.getReason())
                .description(request.getDescription())
                .createdAt(Instant.now())
                .build();

        reportRepository.save(build);
        return ReportResponse.from(build);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findById(String reportPublicId) {
        return reportRepository.findByPublicId(reportPublicId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getAllReports(ReportStatus status, Integer page, Integer size) {

        Pageable pageable = Utils.buildPageable(page, size, "createdAt", "desc");

        Page<Report> reportPage;
        if (status != null) {
            reportPage = reportRepository.findByStatusWithDetails(status, pageable);
        } else {
            reportPage = reportRepository.findAllWithDetails(pageable);
        }

        List<ReportResponse> content = reportPage.getContent().stream()
                .map(ReportResponse::from)
                .toList();

        return PageMapper.toPageResponse(reportPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getReportsByTarget(ReportTargetType targetType, String targetPublicId, Integer page, Integer size) {

        Pageable pageable = Utils.buildPageable(page, size, "createdAt", "desc");

        Page<Report> reportPage = reportRepository
                .findByTargetTypeAndTargetPublicIdWithDetails(targetType, targetPublicId, pageable);

        List<ReportResponse> content = reportPage.getContent().stream()
                .map(ReportResponse::from)
                .toList();

        return PageMapper.toPageResponse(reportPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getUserReports(String username, Integer page, Integer size) {

        Pageable pageable = Utils.buildPageable(page, size, "createdAt", "desc");

        authValidator.validateUserByUsername(username);

        Page<Report> reportPage = reportRepository
                .findByReporter_UsernameWithDetails(username, pageable);

        List<ReportResponse> content = reportPage.getContent().stream()
                .map(ReportResponse::from)
                .toList();

        return PageMapper.toPageResponse(reportPage, content);
    }

    @Override
    public ReportResponse resolveReport(String reportPublicId, String adminUsername, ResolveReportRequest request) {
        return null;
    }

    @Override
    public boolean hasReported(String username, ReportTargetType targetType, String targetPublicId) {
        return false;
    }

    @Override
    public Long getPendingReportCount() {
        return null;
    }

    @Override
    public Long getReportCountForTarget(ReportTargetType targetType, String targetPublicId) {
        return null;
    }
}
