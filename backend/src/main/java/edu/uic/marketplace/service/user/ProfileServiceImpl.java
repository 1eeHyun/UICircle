package edu.uic.marketplace.service.user;


import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.exception.auth.UserNotAuthorizedException;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.user.ProfileRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final AuthValidator authValidator;

    @Override
    @Transactional
    public void createProfile(User user) {

        Profile profile = Profile.builder()
                .user(user)
                .displayName(user.getUsername())
                .avatarUrl(null)
                .bio("")
                .major(null)
                .build();

        profileRepository.save(profile);
    }

    @Override
    public Optional<Profile> findByUsername(String username) {

        User user = authValidator.validateUserByUsername(username);
        Optional<Profile> found = profileRepository.findByUser_UserId(user.getUserId());

        return found;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {

        Profile profile = __getProfileByUsername(username);

        return ProfileResponse.from(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(String username, UpdateProfileRequest request) {

        User user = authValidator.validateUserByUsername(username);
        Profile profile = profileRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Profile does not exist."));

        if (profile.getUser() != user) {
            throw new UserNotAuthorizedException("You're not authorized to do this action.");
        }

        profile.setDisplayName(request.getDisplayName());
        profile.setBio(request.getBio());
        profile.setMajor(request.getMajor());

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
        return false;
    }

    // Helper methods
    private Profile __getProfileByUsername(String username) {
        User user = authValidator.validateUserByUsername(username);
        return profileRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Profile does not exist."));
    }
}
