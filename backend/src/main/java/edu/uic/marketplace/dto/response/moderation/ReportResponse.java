package edu.uic.marketplace.dto.response.moderation;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private Long reportId;
    private UserResponse reporter;
    private String targetType;
    private Long targetId;
    private String reason;
    private String description;
    private ReportStatus status;
    private UserResponse resolvedBy;
    private Instant resolvedAt;
    private Instant createdAt;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporter(UserResponse.from(report.getReporter()))
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .resolvedBy(report.getResolvedBy() != null ? UserResponse.from(report.getResolvedBy()) : null)
                .resolvedAt(report.getResolvedAt())
                .createdAt(report.getCreatedAt())
                .build();
    }
}