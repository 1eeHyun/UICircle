package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.BadgeResponse;
import edu.uic.marketplace.model.user.Badge;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserBadge;
import edu.uic.marketplace.repository.user.BadgeRepository;
import edu.uic.marketplace.repository.user.UserBadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BadgeService Test")
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private BadgeServiceImpl badgeService;

    private Badge firstSaleBadge;
    private Badge trustedSellerBadge;
    private User testUser;
    private UserBadge userBadge;

    @BeforeEach
    void setUp() {
        firstSaleBadge = Badge.builder()
                .badgeId(1L)
                .code("FIRST_SALE")
                .name("First Sale")
                .description("Made your first sale")
                .iconUrl("https://example.com/first-sale.png")
                .build();

        trustedSellerBadge = Badge.builder()
                .badgeId(2L)
                .code("TRUSTED_SELLER")
                .name("Trusted Seller")
                .description("Completed 10 sales")
                .iconUrl("https://example.com/trusted-seller.png")
                .build();

        testUser = User.builder()
                .userId(1L)
                .email("john.doe@uic.edu")
                .build();

        userBadge = UserBadge.builder()
                .user(testUser)
                .badge(firstSaleBadge)
                .awardedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Retrieve all badges")
    void findAll_Success() {

        // Given
        List<Badge> badges = Arrays.asList(firstSaleBadge, trustedSellerBadge);
        when(badgeRepository.findAll()).thenReturn(badges);

        // When
        List<Badge> result = badgeService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(firstSaleBadge, trustedSellerBadge);
        verify(badgeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Find badge by code - Success")
    void findByCode_Success() {

        // Given
        when(badgeRepository.findByCode("FIRST_SALE")).thenReturn(Optional.of(firstSaleBadge));

        // When
        Optional<Badge> result = badgeService.findByCode("FIRST_SALE");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("FIRST_SALE");
        verify(badgeRepository, times(1)).findByCode("FIRST_SALE");
    }

    @Test
    @DisplayName("Find badge by code - Not found")
    void findByCode_NotFound() {

        // Given
        when(badgeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // When
        Optional<Badge> result = badgeService.findByCode("INVALID_CODE");

        // Then
        assertThat(result).isEmpty();
        verify(badgeRepository, times(1)).findByCode("INVALID_CODE");
    }

    @Test
    @DisplayName("Get user badges")
    void getUserBadges_Success() {

        // Given
        List<UserBadge> userBadges = Arrays.asList(userBadge);
        when(userBadgeRepository.findByUser_UserId(1L)).thenReturn(userBadges);

        // When
        List<BadgeResponse> result = badgeService.getUserBadges(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("FIRST_SALE");
        verify(userBadgeRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("Award badge to user - Success")
    void awardBadge_Success() {

        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByCode("FIRST_SALE")).thenReturn(Optional.of(firstSaleBadge));
        when(userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(1L, 1L)).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(userBadge);

        // When
        BadgeResponse result = badgeService.awardBadge(1L, "FIRST_SALE");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("FIRST_SALE");
        verify(userService, times(1)).findById(1L);
        verify(badgeRepository, times(1)).findByCode("FIRST_SALE");
        verify(userBadgeRepository, times(1)).save(any(UserBadge.class));
    }

    @Test
    @DisplayName("Award badge to user - Already has badge")
    void awardBadge_AlreadyHas_ThrowsException() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByCode("FIRST_SALE")).thenReturn(Optional.of(firstSaleBadge));
        when(userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> badgeService.awardBadge(1L, "FIRST_SALE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User already has this badge");
        
        verify(userBadgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Award badge to user - User not found")
    void awardBadge_UserNotFound_ThrowsException() {
        // Given
        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> badgeService.awardBadge(999L, "FIRST_SALE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(badgeRepository, never()).findByCode(anyString());
        verify(userBadgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Award badge to user - Badge not found")
    void awardBadge_BadgeNotFound_ThrowsException() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> badgeService.awardBadge(1L, "INVALID_CODE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Badge not found");
        
        verify(userBadgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Check if user has badge - True")
    void userHasBadge_True() {
        // Given
        when(badgeRepository.findByCode("FIRST_SALE")).thenReturn(Optional.of(firstSaleBadge));
        when(userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(1L, 1L)).thenReturn(true);

        // When
        boolean result = badgeService.userHasBadge(1L, "FIRST_SALE");

        // Then
        assertThat(result).isTrue();
        verify(badgeRepository, times(1)).findByCode("FIRST_SALE");
        verify(userBadgeRepository, times(1)).existsByUser_UserIdAndBadge_BadgeId(1L, 1L);
    }

    @Test
    @DisplayName("Check if user has badge - False")
    void userHasBadge_False() {
        // Given
        when(badgeRepository.findByCode("FIRST_SALE")).thenReturn(Optional.of(firstSaleBadge));
        when(userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(1L, 1L)).thenReturn(false);

        // When
        boolean result = badgeService.userHasBadge(1L, "FIRST_SALE");

        // Then
        assertThat(result).isFalse();
        verify(badgeRepository, times(1)).findByCode("FIRST_SALE");
        verify(userBadgeRepository, times(1)).existsByUser_UserIdAndBadge_BadgeId(1L, 1L);
    }

    @Test
    @DisplayName("Check and award auto badges - First sale")
    void checkAndAwardAutoBadges_FirstSale() {
        // Given
        when(profileService.findByUserId(1L)).thenReturn(Optional.of(
                edu.uic.marketplace.model.user.Profile.builder()
                        .soldCount(1)
                        .build()
        ));
        when(badgeRepository.findByCode("FIRST_SALE")).thenReturn(Optional.of(firstSaleBadge));
        when(userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(1L, 1L)).thenReturn(false);
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(userBadge);

        // When
        badgeService.checkAndAwardAutoBadges(1L);

        // Then
        verify(userBadgeRepository, times(1)).save(any(UserBadge.class));
    }

    @Test
    @DisplayName("Check and award auto badges - No condition met")
    void checkAndAwardAutoBadges_NoConditionMet() {
        // Given
        when(profileService.findByUserId(1L)).thenReturn(Optional.of(
                edu.uic.marketplace.model.user.Profile.builder()
                        .soldCount(0)
                        .build()
        ));

        // When
        badgeService.checkAndAwardAutoBadges(1L);

        // Then
        verify(userBadgeRepository, never()).save(any());
    }
}
