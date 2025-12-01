package edu.uic.marketplace.controller.moderation.api;

import edu.uic.marketplace.controller.moderation.docs.ReportApiDocs;
import edu.uic.marketplace.dto.request.moderation.CreateReportRequest;
import edu.uic.marketplace.dto.request.moderation.ResolveReportRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ReportResponse;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.moderation.ReportTargetType;
import edu.uic.marketplace.service.moderation.ReportService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderation/reports")
@RequiredArgsConstructor
public class ReportController implements ReportApiDocs {

    private final AuthValidator authValidator;
    private final ReportService reportService;

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<ReportResponse>> createReport(
            @RequestBody CreateReportRequest request) {

        String username = authValidator.extractUsername();
        ReportResponse res = reportService.createReport(username, request);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<ReportResponse>>> getAllReports(
            ReportStatus status, Integer page, Integer size) {

        // TODO: for admin
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<PageResponse<ReportResponse>>> getReportsByTarget(ReportTargetType targetType, String targetPublicId, Integer page, Integer size) {

        // TODO: for admin
        return null;
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<PageResponse<ReportResponse>>> getMyReports(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        String username = authValidator.extractUsername();
        PageResponse<ReportResponse> res = reportService.getUserReports(username, page, size);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    public ResponseEntity<CommonResponse<ReportResponse>> resolveReport(String reportPublicId, String adminUsername, ResolveReportRequest request) {

        // TODO: for admin
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<Boolean>> hasReported(String username, ReportTargetType targetType, String targetPublicId) {

        // TODO: for admin
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getPendingReportCount() {

        // TODO: for admin
        return null;
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getReportCountForTarget(ReportTargetType targetType, String targetPublicId) {

        // TODO: for admin
        return null;
    }
}
