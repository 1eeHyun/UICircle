package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.user.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Unit Test")
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User testUser;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@uic.edu")
                .build();

        testProfile = Profile.builder()
                .user(testUser)
                .displayName("JohnDoe123")
                .bio("Computer Science student")
                .major("Computer Science")
                .avatarUrl("https://example.com/avatar.jpg")
                .soldCount(5)
                .buyCount(3)
                .build();
    }

    @Test
    @DisplayName("Find profile by user ID - Success")
    void findByUserId_Success() {

        // Given
        when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(testProfile));

        // When
        Optional<Profile> result = profileService.findByUserId(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDisplayName()).isEqualTo("JohnDoe123");
        verify(profileRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("Find profile by user ID - Not found")
    void findByUserId_NotFound() {

        // Given
        when(profileRepository.findByUser_UserId(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Profile> result = profileService.findByUserId(999L);

        // Then
        assertThat(result).isEmpty();
        verify(profileRepository, times(1)).findByUser_UserId(999L);
    }

    @Test
    @DisplayName("Get ProfileResponse - Success")
    void getProfileByUserId_Success() {

        // Given
        when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(testProfile));

        // When
        ProfileResponse result = profileService.getProfileByUserId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getDisplayName()).isEqualTo("JohnDoe123");
        assertThat(result.getBio()).isEqualTo("Computer Science student");
        assertThat(result.getMajor()).isEqualTo("Computer Science");
        verify(profileRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("Get ProfileResponse - Profile not found exception")
    void getProfileByUserId_NotFound_ThrowsException() {

        // Given
        when(profileRepository.findByUser_UserId(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> profileService.getProfileByUserId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");
        
        verify(profileRepository, times(1)).findByUser_UserId(999L);
    }

    @Test
    @DisplayName("Update profile - Success")
    void updateProfile_Success() {

        // Given
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .displayName("NewDisplayName")
                .bio("Updated bio")
                .major("Data Science")
                .build();

        when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        // When
        ProfileResponse result = profileService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProfile.getDisplayName()).isEqualTo("NewDisplayName");
        assertThat(testProfile.getBio()).isEqualTo("Updated bio");
        assertThat(testProfile.getMajor()).isEqualTo("Data Science");
        verify(profileRepository, times(1)).findByUser_UserId(1L);
        verify(profileRepository, times(1)).save(testProfile);
    }

    @Test
    @DisplayName("Update profile - Profile not found exception")
    void updateProfile_NotFound_ThrowsException() {

        // Given
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .displayName("NewName")
                .build();
        when(profileRepository.findByUser_UserId(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> profileService.updateProfile(999L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");
        
        verify(profileRepository, times(1)).findByUser_UserId(999L);
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Upload avatar - Success")
    void uploadAvatar_Success() {

        // Given
        String newAvatarUrl = "https://example.com/new-avatar.jpg";
        when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        // When
        ProfileResponse result = profileService.uploadAvatar(1L, newAvatarUrl);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProfile.getAvatarUrl()).isEqualTo(newAvatarUrl);
        verify(profileRepository, times(1)).findByUser_UserId(1L);
        verify(profileRepository, times(1)).save(testProfile);
    }

    @Test
    @DisplayName("Increment sold count")
    void incrementSoldCount_Success() {

        // Given
        Integer initialCount = testProfile.getSoldCount();
        when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(testProfile));

        // When
        profileService.incrementSoldCount(1L);

        // Then
        assertThat(testProfile.getSoldCount()).isEqualTo(initialCount + 1);
        verify(profileRepository, times(1)).findByUser_UserId(1L);
        verify(profileRepository, times(1)).save(testProfile);
    }

    @Test
    @DisplayName("Increment buy count")
    void incrementBuyCount_Success() {

        // Given
        Integer initialCount = testProfile.getBuyCount();
        when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(testProfile));

        // When
        profileService.incrementBuyCount(1L);

        // Then
        assertThat(testProfile.getBuyCount()).isEqualTo(initialCount + 1);
        verify(profileRepository, times(1)).findByUser_UserId(1L);
        verify(profileRepository, times(1)).save(testProfile);
    }

    @Test
    @DisplayName("Check if display name is available - Available")
    void isDisplayNameAvailable_Available() {

        // Given
        when(profileRepository.existsByDisplayName("NewName")).thenReturn(false);

        // When
        boolean result = profileService.isDisplayNameAvailable("NewName");

        // Then
        assertThat(result).isTrue();
        verify(profileRepository, times(1)).existsByDisplayName("NewName");
    }

    @Test
    @DisplayName("Check if display name is available - Not available")
    void isDisplayNameAvailable_NotAvailable() {

        // Given
        when(profileRepository.existsByDisplayName("ExistingName")).thenReturn(true);

        // When
        boolean result = profileService.isDisplayNameAvailable("ExistingName");

        // Then
        assertThat(result).isFalse();
        verify(profileRepository, times(1)).existsByDisplayName("ExistingName");
    }
}
