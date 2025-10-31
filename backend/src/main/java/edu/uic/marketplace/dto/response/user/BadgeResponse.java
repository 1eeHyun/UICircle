package edu.uic.marketplace.dto.response.user;

import edu.uic.marketplace.model.user.Badge;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeResponse {

    private Long badgeId;
    private String code;
    private String name;
    private String description;
    private String iconUrl;

    public static BadgeResponse from(Badge badge) {
        return BadgeResponse.builder()
                .badgeId(badge.getBadgeId())
                .code(badge.getCode())
                .name(badge.getName())
                .description(badge.getDescription())
                .iconUrl(badge.getIconUrl())
                .build();
    }
}