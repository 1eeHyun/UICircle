package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;

import java.util.Optional;

/**
 * Profile management service interface
 */
public interface ProfileService {

    /**
     * Create a new profile
     * @Param User user
     */
    void createProfile(User user);

    /**
     * Get profile by username
     * @param username Username
     * @return Profile entity
     */
    Optional<Profile> findByUsername(String username);
    
    /**
     * Get profile response by username
     * @param username User ID
     * @return ProfileResponse DTO
     */
    ProfileResponse getProfileByUsername(String username);
    
    /**
     * Update user profile
     * @param username Username
     * @param request Update profile request
     * @return Updated ProfileResponse
     */
    ProfileResponse updateProfile(String username, UpdateProfileRequest request);
    
    /**
     * Upload avatar image
     * @param username Username
     * @param imageUrl Image URL
     * @return Updated ProfileResponse
     */
    ProfileResponse uploadAvatar(String username, String imageUrl);
    
    /**
     * Increment sold count
     * @param username
     */
    void incrementSoldCount(String username);
    
    /**
     * Increment buy count
     * @param username
     */
    void incrementBuyCount(String username);
    
    /**
     * Check if display name is available
     * @param displayName Display name to check
     * @return true if available, false otherwise
     */
    boolean isDisplayNameAvailable(String displayName);
}
