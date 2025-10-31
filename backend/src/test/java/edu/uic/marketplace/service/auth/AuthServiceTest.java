package edu.uic.marketplace.service.auth;

import edu.uic.marketplace.dto.request.auth.*;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.auth.TokenResponse;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.model.verification.PasswordReset;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.repository.verification.EmailVerificationRepository;
import edu.uic.marketplace.repository.verification.PasswordResetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Test")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Signup - Success")
    void signup_Success() {

        // Given
        SignupRequest request = SignupRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@uic.edu")
                .password("Password123!")
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        authService.signup(request);

        // Then
        verify(userRepository, times(1)).existsByEmail("john.doe@uic.edu");
        verify(passwordEncoder, times(1)).encode("Password123!");
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailVerificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Signup - Duplicate email")
    void signup_DuplicateEmail() {

        // Given
        SignupRequest request = SignupRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@uic.edu")
                .password("Password123!")
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository, times(1)).existsByEmail("john.doe@uic.edu");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login - Success")
    void login_Success() {

        // Given
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@uic.edu")
                .password("Password123!")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        verify(userRepository, times(1)).findByEmail("john.doe@uic.edu");
        verify(passwordEncoder, times(1)).matches("Password123!", "hashed_password");
    }

    @Test
    @DisplayName("Login - Wrong password")
    void login_InvalidPassword() {

        // Given
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@uic.edu")
                .password("WrongPassword!")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository, times(1)).findByEmail("john.doe@uic.edu");
        verify(passwordEncoder, times(1)).matches("WrongPassword!", "hashed_password");
    }

    @Test
    @DisplayName("Login - Email not verified")
    void login_EmailNotVerified() {

        // Given
        testUser.setEmailVerified(false);
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@uic.edu")
                .password("Password123!")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email not verified");

        verify(userRepository, times(1)).findByEmail("john.doe@uic.edu");
    }

    @Test
    @DisplayName("Login - Suspended Account")
    void login_SuspendedAccount() {

        // Given
        testUser.setStatus(UserStatus.SUSPENDED);
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@uic.edu")
                .password("Password123!")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Account is suspended");

        verify(userRepository, times(1)).findByEmail("john.doe@uic.edu");
    }

    @Test
    @DisplayName("Verify email - Success")
    void verifyEmail_Success() {

        // Given
        String token = "valid_token";
        when(emailVerificationRepository.findByToken(anyString())).thenReturn(Optional.of(any()));

        // When
        authService.verifyEmail(token);

        // Then
        verify(emailVerificationRepository, times(1)).findByToken(token);
    }

    @Test
    @DisplayName("Request password reset - Success")
    void requestPasswordReset_Success() {

        // Given
        PasswordResetCodeRequest request = PasswordResetCodeRequest.builder()
                .email("john.doe@uic.edu")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When
        authService.requestPasswordReset(request);

        // Then
        verify(userRepository, times(1)).findByEmail("john.doe@uic.edu");
    }

    @Test
    @DisplayName("Reset password - Success")
    void resetPassword_Success() {

        // Given
        PasswordResetRequest request = PasswordResetRequest.builder()
                .token("valid_reset_token")
                .newPassword("NewPassword123!")
                .build();

        PasswordReset passwordReset = PasswordReset.builder()
                .user(testUser)
                .token("valid_reset_token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(passwordResetRepository.findByToken("valid_reset_token"))
                .thenReturn(Optional.of(passwordReset));
        when(passwordEncoder.encode(anyString())).thenReturn("new_hashed_password");

        // When
        authService.resetPassword(request);

        // Then
        verify(passwordResetRepository, times(1)).findByToken("valid_reset_token");
        verify(passwordEncoder, times(1)).encode("NewPassword123!");
        verify(userRepository, times(1)).save(testUser);
        verify(passwordResetRepository, times(1)).save(passwordReset);
    }

    @Test
    @DisplayName("Change password - Success")
    void changePassword_Success() {

        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("Password123!")
                .newPassword("NewPassword123!")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("new_hashed_password");

        // When
        authService.changePassword(1L, request);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("Password123!", "hashed_password");
        verify(passwordEncoder, times(1)).encode("NewPassword123!");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Change password - Invalid current password")
    void changePassword_InvalidCurrentPassword() {

        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("WrongPassword!")
                .newPassword("NewPassword123!")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.changePassword(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("WrongPassword!", "hashed_password");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Refresh token - Success")
    void refreshToken_Success() {

        // Given
        String refreshToken = "valid_refresh_token";

        // When
        TokenResponse response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
//        assertThat(response.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("Logout - Success")
    void logout_Success() {

        // Given
        Long userId = 1L;
        String token = "access_token";

        // When
        authService.logout(userId, token);

        // Then
        // logout logic verification (ex: Invalidate token in Redis)
        verify(userRepository, never()).save(any());
    }
}
