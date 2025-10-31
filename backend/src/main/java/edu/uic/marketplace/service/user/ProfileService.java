package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.model.user.Profile;

import java.util.Optional;

/**
 * Profile management service interface
 */
public interface ProfileService {
    
    /**
     * Get profile by user ID
     * @param userId User ID
     * @return Profile entity
     */
    Optional<Profile> findByUserId(Long userId);
    
    /**
     * Get profile response by user ID
     * @param userId User ID
     * @return ProfileResponse DTO
     */
    ProfileResponse getProfileByUserId(Long userId);
    
    /**
     * Update user profile
     * @param userId User ID
     * @param request Update profile request
     * @return Updated ProfileResponse
     */
    ProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    
    /**
     * Upload avatar image
     * @param userId User ID
     * @param imageUrl Image URL
     * @return Updated ProfileResponse
     */
    ProfileResponse uploadAvatar(Long userId, String imageUrl);
    
    /**
     * Increment sold count
     * @param userId User ID
     */
    void incrementSoldCount(Long userId);
    
    /**
     * Increment buy count
     * @param userId User ID
     */
    void incrementBuyCount(Long userId);
    
    /**
     * Check if display name is available
     * @param displayName Display name to check
     * @return true if available, false otherwise
     */
    boolean isDisplayNameAvailable(String displayName);
}
