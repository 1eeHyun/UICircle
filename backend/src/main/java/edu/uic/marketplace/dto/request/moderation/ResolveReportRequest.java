package edu.uic.marketplace.dto.request.moderation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolveReportRequest {

    @NotBlank(message = "Action is required")
    private String action;  // RESOLVE or DISMISS

    private String note;
}