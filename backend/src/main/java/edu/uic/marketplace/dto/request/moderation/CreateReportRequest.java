package edu.uic.marketplace.dto.request.moderation;

import edu.uic.marketplace.model.moderation.ReportReason;
import edu.uic.marketplace.model.moderation.ReportTargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReportRequest {

    @NotNull(message = "Target type is required")
    private ReportTargetType targetType;

    @NotNull(message = "Target public ID is required")
    @Size(min = 36, max = 36, message = "Target public ID must be a valid UUID")
    private String targetPublicId;

    @NotNull(message = "Reason is required")
    private ReportReason reason;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
}
