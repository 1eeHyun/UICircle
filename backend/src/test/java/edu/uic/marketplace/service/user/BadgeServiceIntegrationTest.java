package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.BadgeResponse;
import edu.uic.marketplace.model.user.Badge;
import edu.uic.marketplace.model.user.Profile;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserBadge;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.user.BadgeRepository;
import edu.uic.marketplace.repository.user.ProfileRepository;
import edu.uic.marketplace.repository.user.UserBadgeRepository;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BadgeService Integration Test")
class BadgeServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("Create and find all badges")
    void createAndFindAllBadges() {

        // Given
        createBadge("FIRST_SALE", "First Sale", "Made your first sale");
        createBadge("TRUSTED_SELLER", "Trusted Seller", "Completed 10 sales");

        // When
        List<Badge> badges = badgeService.findAll();

        // Then
        assertThat(badges).hasSize(2);
        assertThat(badges).extracting("code").containsExactlyInAnyOrder("FIRST_SALE", "TRUSTED_SELLER");
    }

    @Test
    @DisplayName("Find badge by code")
    void findByCode() {

        // Given
        createBadge("VERIFIED_STUDENT", "Verified Student", "Email verified");

        // When
        Optional<Badge> foundBadge = badgeService.findByCode("VERIFIED_STUDENT");

        // Then
        assertThat(foundBadge).isPresent();
        assertThat(foundBadge.get().getCode()).isEqualTo("VERIFIED_STUDENT");
        assertThat(foundBadge.get().getName()).isEqualTo("Verified Student");
    }

    @Test
    @DisplayName("Award badge to user")
    void awardBadge() {

        // Given
        User user = createUser("john.doe@uic.edu", "John", "Doe");
        createBadge("EARLY_ADOPTER", "Early Adopter", "Joined early");

        // When
        BadgeResponse response = badgeService.awardBadge(user.getUserId(), "EARLY_ADOPTER");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("EARLY_ADOPTER");
        assertThat(response.getName()).isEqualTo("Early Adopter");

        // Verify in database
        List<UserBadge> userBadges = userBadgeRepository.findByUser_UserId(user.getUserId());
        assertThat(userBadges).hasSize(1);
        assertThat(userBadges.get(0).getBadge().getCode()).isEqualTo("EARLY_ADOPTER");
    }

    @Test
    @DisplayName("Award duplicate badge to user")
    void awardBadge_Duplicate() {

        // Given
        User user = createUser("jane.smith@uic.edu", "Jane", "Smith");
        createBadge("FIRST_POST", "First Post", "Created first listing");
        
        badgeService.awardBadge(user.getUserId(), "FIRST_POST");

        // When & Then
        assertThatThrownBy(() -> badgeService.awardBadge(user.getUserId(), "FIRST_POST"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already has this badge");
    }

    @Test
    @DisplayName("Get user badges")
    void getUserBadges() {

        // Given
        User user = createUser("bob.johnson@uic.edu", "Bob", "Johnson");
        createBadge("BADGE_1", "Badge One", "First badge");
        createBadge("BADGE_2", "Badge Two", "Second badge");
        createBadge("BADGE_3", "Badge Three", "Third badge");

        badgeService.awardBadge(user.getUserId(), "BADGE_1");
        badgeService.awardBadge(user.getUserId(), "BADGE_2");

        // When
        List<BadgeResponse> userBadges = badgeService.getUserBadges(user.getUserId());

        // Then
        assertThat(userBadges).hasSize(2);
        assertThat(userBadges).extracting("code").containsExactlyInAnyOrder("BADGE_1", "BADGE_2");
    }

    @Test
    @DisplayName("Check if user has badge")
    void userHasBadge() {

        // Given
        User user = createUser("alice.williams@uic.edu", "Alice", "Williams");
        createBadge("ACTIVE_USER", "Active User", "Active for 30 days");
        
        badgeService.awardBadge(user.getUserId(), "ACTIVE_USER");

        // When
        boolean hasBadge = badgeService.userHasBadge(user.getUserId(), "ACTIVE_USER");
        boolean hasOtherBadge = badgeService.userHasBadge(user.getUserId(), "NONEXISTENT");

        // Then
        assertThat(hasBadge).isTrue();
        assertThat(hasOtherBadge).isFalse();
    }

    @Test
    @DisplayName("Check and award auto badges - First sale")
    void checkAndAwardAutoBadges_FirstSale() {

        // Given
        User user = createUser("charlie.brown@uic.edu", "Charlie", "Brown");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("CharlieB")
                .bio("Bio")
                .major("CS")
                .avatarUrl("url")
                .soldCount(1)
                .buyCount(0)
                .build();
        profileRepository.save(profile);
        
        createBadge("FIRST_SALE", "First Sale", "Made your first sale");

        // When
        badgeService.checkAndAwardAutoBadges(user.getUserId());

        // Then
        boolean hasFirstSaleBadge = badgeService.userHasBadge(user.getUserId(), "FIRST_SALE");
        assertThat(hasFirstSaleBadge).isTrue();
    }

    @Test
    @DisplayName("Check and award auto badges - No condition met")
    void checkAndAwardAutoBadges_NoConditionMet() {

        // Given
        User user = createUser("david.lee@uic.edu", "David", "Lee");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("DavidL")
                .bio("Bio")
                .major("CS")
                .avatarUrl("url")
                .soldCount(0)
                .buyCount(0)
                .build();
        profileRepository.save(profile);
        
        createBadge("FIRST_SALE", "First Sale", "Made your first sale");

        // When
        badgeService.checkAndAwardAutoBadges(user.getUserId());

        // Then
        List<BadgeResponse> userBadges = badgeService.getUserBadges(user.getUserId());
        assertThat(userBadges).isEmpty();
    }

    @Test
    @DisplayName("Check and award auto badges - Already has badge")
    void checkAndAwardAutoBadges_AlreadyHas() {

        // Given
        User user = createUser("emma.davis@uic.edu", "Emma", "Davis");
        Profile profile = Profile.builder()
                .user(user)
                .displayName("EmmaD")
                .bio("Bio")
                .major("CS")
                .avatarUrl("url")
                .soldCount(1)
                .buyCount(0)
                .build();
        profileRepository.save(profile);
        
        createBadge("FIRST_SALE", "First Sale", "Made your first sale");
        badgeService.awardBadge(user.getUserId(), "FIRST_SALE");

        // When - Should not throw exception
        badgeService.checkAndAwardAutoBadges(user.getUserId());

        // Then
        List<BadgeResponse> userBadges = badgeService.getUserBadges(user.getUserId());
        assertThat(userBadges).hasSize(1);
    }

    @Test
    @DisplayName("Award same badge to multiple users")
    void awardSameBadgeToMultipleUsers() {
        // Given
        User user1 = createUser("user1@uic.edu", "User", "One");
        User user2 = createUser("user2@uic.edu", "User", "Two");
        User user3 = createUser("user3@uic.edu", "User", "Three");
        
        createBadge("POPULAR", "Popular", "Popular badge");

        // When
        badgeService.awardBadge(user1.getUserId(), "POPULAR");
        badgeService.awardBadge(user2.getUserId(), "POPULAR");
        badgeService.awardBadge(user3.getUserId(), "POPULAR");

        // Then
        assertThat(badgeService.userHasBadge(user1.getUserId(), "POPULAR")).isTrue();
        assertThat(badgeService.userHasBadge(user2.getUserId(), "POPULAR")).isTrue();
        assertThat(badgeService.userHasBadge(user3.getUserId(), "POPULAR")).isTrue();
    }

    @Test
    @DisplayName("Award badge with nonexistent code")
    void awardBadge_BadgeNotFound() {

        // Given
        User user = createUser("frank.miller@uic.edu", "Frank", "Miller");

        // When & Then
        assertThatThrownBy(() -> badgeService.awardBadge(user.getUserId(), "NONEXISTENT_BADGE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Badge not found");
    }

    @Test
    @DisplayName("Award badge to nonexistent user")
    void awardBadge_UserNotFound() {

        // Given
        createBadge("TEST_BADGE", "Test Badge", "Test");

        // When & Then
        assertThatThrownBy(() -> badgeService.awardBadge(999L, "TEST_BADGE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // Helper methods
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

    private Badge createBadge(String code, String name, String description) {
        Badge badge = Badge.builder()
                .code(code)
                .name(name)
                .description(description)
                .iconUrl("https://example.com/" + code.toLowerCase() + ".png")
                .build();
        
        return badgeRepository.save(badge);
    }
}
