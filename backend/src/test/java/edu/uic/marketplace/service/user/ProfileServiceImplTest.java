package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.request.user.UpdateProfileRequest;
import edu.uic.marketplace.dto.response.user.ProfileResponse;
import edu.uic.marketplace.exception.auth.UserNotAuthorizedException;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.user.ProfileRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private AuthValidator authValidator;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User createUser(Long id, String username) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        return user;
    }

    private Profile createProfile(User user) {
        return Profile.builder()
                .user(user)
                .displayName(user.getUsername())
                .bio("bio")
                .major("CS")
                .avatarUrl("https://example.com/avatar.png")
                .build();
    }

    @Nested
    @DisplayName("createProfile")
    class CreateProfileTests {

        @Test
        @DisplayName("Should create a profile with default values")
        void createProfile_success() {
            // Given
            User user = createUser(1L, "testuser");

            // When
            profileService.createProfile(user);

            // Then
            ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
            verify(profileRepository, times(1)).save(captor.capture());

            Profile saved = captor.getValue();
            assertEquals(user, saved.getUser());
            assertEquals("testuser", saved.getDisplayName());
            assertEquals("", saved.getBio());
            assertNull(saved.getMajor());
            assertNull(saved.getAvatarUrl());
        }
    }

    @Nested
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return profile when it exists")
        void findByUsername_found() {
            // Given
            User user = createUser(1L, "testuser");
            Profile profile = createProfile(user);

            given(authValidator.validateUserByUsername("testuser")).willReturn(user);
            given(profileRepository.findByUser_UserId(user.getUserId()))
                    .willReturn(Optional.of(profile));

            // When
            Optional<Profile> result = profileService.findByUsername("testuser");

            // Then
            assertTrue(result.isPresent());
            assertEquals(profile, result.get());
        }

        @Test
        @DisplayName("Should return empty when profile does not exist")
        void findByUsername_notFound() {
            // Given
            User user = createUser(1L, "testuser");

            given(authValidator.validateUserByUsername("testuser")).willReturn(user);
            given(profileRepository.findByUser_UserId(user.getUserId()))
                    .willReturn(Optional.empty());

            // When
            Optional<Profile> result = profileService.findByUsername("testuser");

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update profile fields successfully")
        void updateProfile_success() {
            // Given
            User user = createUser(1L, "testuser");
            Profile profile = createProfile(user);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setDisplayName("New Name");
            request.setBio("New Bio");
            request.setMajor("New Major");

            given(authValidator.validateUserByUsername("testuser")).willReturn(user);
            given(profileRepository.findByUser_UserId(user.getUserId()))
                    .willReturn(Optional.of(profile));

            // When
            ProfileResponse result = profileService.updateProfile("testuser", request);

            // Then
            assertEquals("New Name", profile.getDisplayName());
            assertEquals("New Bio", profile.getBio());
            assertEquals("New Major", profile.getMajor());

            assertEquals("New Name", result.getDisplayName());
        }

        @Test
        @DisplayName("Should throw when trying to update someone else's profile")
        void updateProfile_notOwner() {
            // Given
            User requester = createUser(1L, "requestUser");
            User owner = createUser(2L, "ownerUser");
            Profile profile = createProfile(owner);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setDisplayName("New Name");

            given(authValidator.validateUserByUsername("requestUser")).willReturn(requester);
            given(profileRepository.findByUser_UserId(requester.getUserId()))
                    .willReturn(Optional.of(profile));

            // Then
            assertThrows(UserNotAuthorizedException.class,
                    () -> profileService.updateProfile("requestUser", request));
        }
    }

    @Nested
    @DisplayName("incrementSoldCount / incrementBuyCount")
    class IncrementCountsTests {

        @Test
        @DisplayName("Should increase sold count")
        void incrementSoldCount_success() {
            // Given
            User user = createUser(1L, "seller");
            Profile profile = createProfile(user);
            profile.setSoldCount(5);

            given(authValidator.validateUserByUsername("seller")).willReturn(user);
            given(profileRepository.findByUser_UserId(user.getUserId()))
                    .willReturn(Optional.of(profile));

            // When
            profileService.incrementSoldCount("seller");

            // Then
            assertEquals(6, profile.getSoldCount());
        }

        @Test
        @DisplayName("Should increase buy count")
        void incrementBuyCount_success() {
            // Given
            User user = createUser(1L, "buyer");
            Profile profile = createProfile(user);
            profile.setBuyCount(2);

            given(authValidator.validateUserByUsername("buyer")).willReturn(user);
            given(profileRepository.findByUser_UserId(user.getUserId()))
                    .willReturn(Optional.of(profile));

            // When
            profileService.incrementBuyCount("buyer");

            // Then
            assertEquals(3, profile.getBuyCount());
        }

        @Test
        @DisplayName("Should throw when profile does not exist")
        void incrementCount_profileNotFound() {
            // Given
            User user = createUser(1L, "user");

            given(authValidator.validateUserByUsername("user")).willReturn(user);
            given(profileRepository.findByUser_UserId(user.getUserId()))
                    .willReturn(Optional.empty());

            // Then
            assertThrows(IllegalArgumentException.class,
                    () -> profileService.incrementSoldCount("user"));
        }
    }
}
