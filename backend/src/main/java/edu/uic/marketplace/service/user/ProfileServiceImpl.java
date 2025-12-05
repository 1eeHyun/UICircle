package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.exception.auth.UserNotAuthorizedException;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.user.ProfileRepository;
import edu.uic.marketplace.service.common.S3Service;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final AuthValidator authValidator;
    private final S3Service s3Service;

    @Override
    @Transactional
    public void createProfile(User user) {
        Profile profile = Profile.builder()
                .user(user)
                .displayName(user.getUsername())
                .avatarUrl(null)
                .bannerUrl(null)
                .bio("")
                .major(null)
                .build();

        profileRepository.save(profile);
    }

    @Override
    public Optional<Profile> findByUsername(String username) {
        User user = authValidator.validateUserByUsername(username);
        return profileRepository.findByUser_UserId(user.getUserId());
    }

    @Override
    public Optional<Profile> findByPublicId(String publicId) {
        return profileRepository.findByPublicIdWithUser(publicId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {
        Profile profile = __getProfileByUsername(username);
        return ProfileResponse.from(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getPublicProfile(String publicId) {
        Profile profile = profileRepository.findByPublicIdWithUser(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        return ProfileResponse.from(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = authValidator.validateUserByUsername(username);
        Profile profile = profileRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Profile does not exist."));

        if (!profile.getUser().getUserId().equals(user.getUserId())) {
            throw new UserNotAuthorizedException("You're not authorized to do this action.");
        }

        if (request.getDisplayName() != null) {
            profile.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getMajor() != null) {
            profile.setMajor(request.getMajor());
        }

        return ProfileResponse.from(profile);
    }

    @Override
    @Transactional
    public ProfileResponse uploadAvatar(String username, String imageUrl) {
        Profile profile = __getProfileByUsername(username);
        profile.setAvatarUrl(imageUrl);
        return ProfileResponse.from(profile);
    }

    @Override
    @Transactional
    public ProfileResponse uploadAvatarFile(String username, MultipartFile file) {
        Profile profile = __getProfileByUsername(username);

        // Delete old avatar if exists
        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
            try {
                s3Service.deleteByUrl(profile.getAvatarUrl());
            } catch (Exception e) {
                log.warn("Failed to delete old avatar: {}", e.getMessage());
            }
        }

        // Upload new avatar
        String imageUrl = s3Service.upload(file);
        profile.setAvatarUrl(imageUrl);

        return ProfileResponse.from(profile);
    }

    @Override
    @Transactional
    public ProfileResponse uploadBannerFile(String username, MultipartFile file) {
        Profile profile = __getProfileByUsername(username);

        // Delete old banner if exists
        if (profile.getBannerUrl() != null && !profile.getBannerUrl().isEmpty()) {
            try {
                s3Service.deleteByUrl(profile.getBannerUrl());
            } catch (Exception e) {
                log.warn("Failed to delete old banner: {}", e.getMessage());
            }
        }

        // Upload new banner
        String imageUrl = s3Service.upload(file);
        profile.setBannerUrl(imageUrl);

        return ProfileResponse.from(profile);
    }

    @Override
    @Transactional
    public void incrementSoldCount(String username) {
        __getProfileByUsername(username).incrementSoldCount();
    }

    @Override
    @Transactional
    public void incrementBuyCount(String username) {
        __getProfileByUsername(username).incrementBuyCount();
    }

    @Override
    public boolean isDisplayNameAvailable(String displayName) {
        return !profileRepository.existsByDisplayName(displayName);
    }

    // Helper methods
    private Profile __getProfileByUsername(String username) {
        User user = authValidator.validateUserByUsername(username);
        return profileRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Profile does not exist."));
    }
}
