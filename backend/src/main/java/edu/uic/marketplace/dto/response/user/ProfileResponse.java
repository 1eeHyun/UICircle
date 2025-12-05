package edu.uic.marketplace.dto.response.user;

import edu.uic.marketplace.model.user.Profile;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private String publicId;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String bannerUrl;
    private String bio;
    private String major;
    private Integer soldCount;
    private Integer buyCount;
    private Instant createdAt;

    public static ProfileResponse from(Profile profile) {
        return ProfileResponse.builder()
                .publicId(profile.getPublicId())
                .username(profile.getUser() != null ? profile.getUser().getUsername() : null)
                .displayName(profile.getDisplayName())
                .avatarUrl(profile.getAvatarUrl())
                .bannerUrl(profile.getBannerUrl())
                .bio(profile.getBio())
                .major(profile.getMajor())
                .soldCount(profile.getSoldCount())
                .buyCount(profile.getBuyCount())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
