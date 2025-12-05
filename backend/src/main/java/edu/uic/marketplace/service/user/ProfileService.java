package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Profile management service interface
 */
public interface ProfileService {

    /**
     * Create a new profile
     * @param user User entity
     */
    void createProfile(User user);

    /**
     * Get profile by username
     * @param username Username
     * @return Profile entity
     */
    Optional<Profile> findByUsername(String username);

    /**
     * Get profile by public ID
     * @param publicId Profile public ID
     * @return Profile entity
     */
    Optional<Profile> findByPublicId(String publicId);
    
    /**
     * Get profile response by username
     * @param username Username
     * @return ProfileResponse DTO
     */
    ProfileResponse getProfileByUsername(String username);

    /**
     * Get public profile by public ID
     * @param publicId Profile public ID
     * @return ProfileResponse DTO
     */
    ProfileResponse getPublicProfile(String publicId);
    
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
     * Upload avatar image file
     * @param username Username
     * @param file Image file
     * @return Updated ProfileResponse
     */
    ProfileResponse uploadAvatarFile(String username, MultipartFile file);

    /**
     * Upload banner image file
     * @param username Username
     * @param file Image file
     * @return Updated ProfileResponse
     */
    ProfileResponse uploadBannerFile(String username, MultipartFile file);
    
    /**
     * Increment sold count
     * @param username Username
     */
    void incrementSoldCount(String username);
    
    /**
     * Increment buy count
     * @param username Username
     */
    void incrementBuyCount(String username);
    
    /**
     * Check if display name is available
     * @param displayName Display name to check
     * @return true if available, false otherwise
     */
    boolean isDisplayNameAvailable(String displayName);
}
