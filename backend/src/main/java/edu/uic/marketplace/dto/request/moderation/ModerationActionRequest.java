package edu.uic.marketplace.dto.request.moderation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationActionRequest {

    @NotBlank(message = "Action type is required")
    private String actionType;  // SUSPEND_USER, DELETE_LISTING, DELETE_MESSAGE

    @NotBlank(message = "Target type is required")
    private String targetType;  // USER, LISTING, MESSAGE

    @NotNull(message = "Target ID is required")
    private Long targetId;

    private String note;
}