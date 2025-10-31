package edu.uic.marketplace.dto.response.moderation;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.moderation.Block;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockResponse {

    private UserResponse blocker;
    private UserResponse blocked;
    private Instant blockedAt;

    public static BlockResponse from(Block block) {
        return BlockResponse.builder()
                .blocker(UserResponse.from(block.getBlocker()))
                .blocked(UserResponse.from(block.getBlocked()))
                .blockedAt(block.getBlockedAt())
                .build();
    }
}