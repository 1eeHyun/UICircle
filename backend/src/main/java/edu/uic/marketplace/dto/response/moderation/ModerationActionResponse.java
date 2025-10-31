package edu.uic.marketplace.dto.response.moderation;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.moderation.ModerationAction;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationActionResponse {

    private Long actionId;
    private UserResponse admin;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String note;
    private Instant createdAt;

    public static ModerationActionResponse from(ModerationAction action) {
        return ModerationActionResponse.builder()
                .actionId(action.getActionId())
                .admin(UserResponse.from(action.getAdmin()))
                .actionType(action.getActionType())
                .targetType(action.getTargetType())
                .targetId(action.getTargetId())
                .note(action.getNote())
                .createdAt(action.getCreatedAt())
                .build();
    }
}