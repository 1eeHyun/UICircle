package edu.uic.marketplace.service.user;


import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.model.user.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    @Override
    public Optional<Profile> findByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public ProfileResponse getProfileByUserId(Long userId) {
        return null;
    }

    @Override
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        return null;
    }

    @Override
    public ProfileResponse uploadAvatar(Long userId, String imageUrl) {
        return null;
    }

    @Override
    public void incrementSoldCount(Long userId) {

    }

    @Override
    public void incrementBuyCount(Long userId) {

    }

    @Override
    public boolean isDisplayNameAvailable(String displayName) {
        return false;
    }
}
