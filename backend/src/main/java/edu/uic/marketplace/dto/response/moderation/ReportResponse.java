package edu.uic.marketplace.dto.response.moderation;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.moderation.Report;
import edu.uic.marketplace.model.moderation.ReportReason;
import edu.uic.marketplace.model.moderation.ReportStatus;
import edu.uic.marketplace.model.moderation.ReportTargetType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private String publicId;
    private UserResponse reporter;

    private ReportTargetType targetType;
    private String targetPublicId;

    private ReportReason reason;

    private String description;
    private ReportStatus status;
    private UserResponse resolvedBy;
    private String resolutionNote;
    private Instant resolvedAt;
    private Instant createdAt;

    /**
     * Convert Report entity to ReportResponse DTO
     */
    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .publicId(report.getPublicId())
                .reporter(UserResponse.from(report.getReporter()))
                .targetType(report.getTargetType())
                .targetPublicId(report.getTargetPublicId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .resolvedBy(report.getResolvedBy() != null ? UserResponse.from(report.getResolvedBy()) : null)
                .resolutionNote(report.getResolutionNote())
                .resolvedAt(report.getResolvedAt())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
