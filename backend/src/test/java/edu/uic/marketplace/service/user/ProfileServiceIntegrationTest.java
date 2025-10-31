package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.user.ProfileRepository;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProfileService Integration Test")
class ProfileServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Create and find profile")
    void createAndFindProfile() {

        // Given
        User user = createUser("john.doe@uic.edu", "John", "Doe");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("JohnDoe123")
                .bio("Computer Science student")
                .major("Computer Science")
                .avatarUrl("https://example.com/avatar.jpg")
                .soldCount(0)
                .buyCount(0)
                .build();
        
        profileRepository.save(profile);

        // When
        Optional<Profile> foundProfile = profileService.findByUserId(user.getUserId());

        // Then
        assertThat(foundProfile).isPresent();
        assertThat(foundProfile.get().getDisplayName()).isEqualTo("JohnDoe123");
        assertThat(foundProfile.get().getBio()).isEqualTo("Computer Science student");
        assertThat(foundProfile.get().getMajor()).isEqualTo("Computer Science");
    }

    @Test
    @DisplayName("Get ProfileResponse - Success")
    void getProfileByUserId_Success() {

        // Given
        User user = createUser("jane.smith@uic.edu", "Jane", "Smith");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("JaneSmith456")
                .bio("Math major")
                .major("Mathematics")
                .avatarUrl("https://example.com/jane.jpg")
                .soldCount(5)
                .buyCount(3)
                .build();
        
        profileRepository.save(profile);

        // When
        ProfileResponse response = profileService.getProfileByUserId(user.getUserId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(user.getUserId());
        assertThat(response.getDisplayName()).isEqualTo("JaneSmith456");
        assertThat(response.getBio()).isEqualTo("Math major");
        assertThat(response.getSoldCount()).isEqualTo(5);
        assertThat(response.getBuyCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Update profile")
    void updateProfile() {

        // Given
        User user = createUser("bob.johnson@uic.edu", "Bob", "Johnson");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("BobJ")
                .bio("Original bio")
                .major("Computer Science")
                .avatarUrl("https://example.com/bob.jpg")
                .soldCount(0)
                .buyCount(0)
                .build();
        
        profileRepository.save(profile);

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .displayName("NewBobJ")
                .bio("Updated bio")
                .major("Data Science")
                .build();

        // When
        ProfileResponse response = profileService.updateProfile(user.getUserId(), request);

        // Then
        assertThat(response.getDisplayName()).isEqualTo("NewBobJ");
        assertThat(response.getBio()).isEqualTo("Updated bio");
        assertThat(response.getMajor()).isEqualTo("Data Science");

        // Verify from database
        Profile updatedProfile = profileRepository.findByUser_UserId(user.getUserId()).orElseThrow();
        assertThat(updatedProfile.getDisplayName()).isEqualTo("NewBobJ");
        assertThat(updatedProfile.getBio()).isEqualTo("Updated bio");
        assertThat(updatedProfile.getMajor()).isEqualTo("Data Science");
    }

    @Test
    @DisplayName("Upload avatar")
    void uploadAvatar() {

        // Given
        User user = createUser("alice.williams@uic.edu", "Alice", "Williams");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("AliceW")
                .bio("Bio")
                .major("Physics")
                .avatarUrl("https://example.com/old.jpg")
                .soldCount(0)
                .buyCount(0)
                .build();
        
        profileRepository.save(profile);

        String newAvatarUrl = "https://example.com/new-avatar.jpg";

        // When
        ProfileResponse response = profileService.uploadAvatar(user.getUserId(), newAvatarUrl);

        // Then
        assertThat(response.getAvatarUrl()).isEqualTo(newAvatarUrl);

        // Verify from database
        Profile updatedProfile = profileRepository.findByUser_UserId(user.getUserId()).orElseThrow();
        assertThat(updatedProfile.getAvatarUrl()).isEqualTo(newAvatarUrl);
    }

    @Test
    @DisplayName("Increment sold count")
    void incrementSoldCount() {

        // Given
        User user = createUser("charlie.brown@uic.edu", "Charlie", "Brown");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("CharlieB")
                .bio("Bio")
                .major("Engineering")
                .avatarUrl("https://example.com/charlie.jpg")
                .soldCount(5)
                .buyCount(0)
                .build();
        
        profileRepository.save(profile);

        // When
        profileService.incrementSoldCount(user.getUserId());

        // Then
        Profile updatedProfile = profileRepository.findByUser_UserId(user.getUserId()).orElseThrow();
        assertThat(updatedProfile.getSoldCount()).isEqualTo(6);
    }

    @Test
    @DisplayName("Increment buy count")
    void incrementBuyCount() {

        // Given
        User user = createUser("david.lee@uic.edu", "David", "Lee");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("DavidL")
                .bio("Bio")
                .major("Business")
                .avatarUrl("https://example.com/david.jpg")
                .soldCount(0)
                .buyCount(3)
                .build();
        
        profileRepository.save(profile);

        // When
        profileService.incrementBuyCount(user.getUserId());

        // Then
        Profile updatedProfile = profileRepository.findByUser_UserId(user.getUserId()).orElseThrow();
        assertThat(updatedProfile.getBuyCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("Check if display name is available")
    void isDisplayNameAvailable() {

        // Given
        User user = createUser("emma.davis@uic.edu", "Emma", "Davis");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("EmmaD")
                .bio("Bio")
                .major("Chemistry")
                .avatarUrl("https://example.com/emma.jpg")
                .soldCount(0)
                .buyCount(0)
                .build();
        
        profileRepository.save(profile);

        // When
        boolean existingName = profileService.isDisplayNameAvailable("EmmaD");
        boolean newName = profileService.isDisplayNameAvailable("NewUniqueName");

        // Then
        assertThat(existingName).isFalse();
        assertThat(newName).isTrue();
    }

    @Test
    @DisplayName("Get profile by user ID - Not found")
    void getProfileByUserId_NotFound() {
        // When & Then
        assertThatThrownBy(() -> profileService.getProfileByUserId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");
    }

    @Test
    @DisplayName("Increment both sold and buy counts")
    void incrementBothCounts() {

        // Given
        User user = createUser("frank.miller@uic.edu", "Frank", "Miller");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("FrankM")
                .bio("Bio")
                .major("Art")
                .avatarUrl("https://example.com/frank.jpg")
                .soldCount(10)
                .buyCount(5)
                .build();
        
        profileRepository.save(profile);

        // When
        profileService.incrementSoldCount(user.getUserId());
        profileService.incrementBuyCount(user.getUserId());

        // Then
        Profile updatedProfile = profileRepository.findByUser_UserId(user.getUserId()).orElseThrow();
        assertThat(updatedProfile.getSoldCount()).isEqualTo(11);
        assertThat(updatedProfile.getBuyCount()).isEqualTo(6);
    }

    // Helper method
    private User createUser(String email, String firstName, String lastName) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        return userRepository.save(user);
    }
}
