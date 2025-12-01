package edu.uic.marketplace.dto.request.moderation;

import edu.uic.marketplace.model.moderation.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolveReportRequest {

    @NotNull(message = "Status is required")
    private ReportStatus status;

    @Size(max = 1000, message = "Resolution note must be less than 1000 characters")
    private String resolutionNote;
}
