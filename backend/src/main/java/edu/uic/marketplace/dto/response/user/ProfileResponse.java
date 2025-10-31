package edu.uic.marketplace.dto.response.user;

import edu.uic.marketplace.model.user.Profile;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Long userId;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String major;
    private Integer soldCount;
    private Integer buyCount;

    public static ProfileResponse from(Profile profile) {
        return ProfileResponse.builder()
                .userId(profile.getUserId())
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .major(profile.getMajor())
                .soldCount(profile.getSoldCount())
                .buyCount(profile.getBuyCount())
                .build();
    }
}
