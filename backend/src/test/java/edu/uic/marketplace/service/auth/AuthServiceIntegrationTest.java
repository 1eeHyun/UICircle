//package edu.uic.marketplace.service.auth;
//
//import edu.uic.marketplace.dto.request.auth.*;
//import edu.uic.marketplace.dto.response.auth.LoginResponse;
//import edu.uic.marketplace.model.user.User;
//import edu.uic.marketplace.model.user.UserStatus;
//import edu.uic.marketplace.model.verification.PasswordReset;
//import edu.uic.marketplace.repository.user.UserRepository;
//import edu.uic.marketplace.repository.verification.PasswordResetRepository;
//import edu.uic.marketplace.support.IntegrationTestSupport;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.Instant;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@DisplayName("AuthService Integration Test")
//class AuthServiceIntegrationTest extends IntegrationTestSupport {
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordResetRepository passwordResetRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Test
//    @DisplayName("Signup full flow")
//    void signup_FullFlow() {
//
//        // Given
//        SignupRequest request = SignupRequest.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .email("john.doe@uic.edu")
//                .password("Password123!")
//                .build();
//
//        // When
//        authService.signup(request);
//
//        // Then
//        User savedUser = userRepository.findByEmail("john.doe@uic.edu").orElseThrow();
//        assertThat(savedUser.getFirstName()).isEqualTo("John");
//        assertThat(savedUser.getLastName()).isEqualTo("Doe");
//        assertThat(savedUser.getEmail()).isEqualTo("john.doe@uic.edu");
//        assertThat(savedUser.getEmailVerified()).isFalse();
//        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
//    }
//
//    @Test
//    @DisplayName("Login full flow")
//    void login_FullFlow() {
//
//        // Given
//        SignupRequest signupRequest = SignupRequest.builder()
//                .firstName("Jane")
//                .lastName("Smith")
//                .email("jane.smith@uic.edu")
//                .password("Password123!")
//                .build();
//        authService.signup(signupRequest);
//
//        // Verify email
//        User user = userRepository.findByEmail("jane.smith@uic.edu").orElseThrow();
//        user.setEmailVerified(true);
//        userRepository.save(user);
//
//        LoginRequest loginRequest = LoginRequest.builder()
//                .email("jane.smith@uic.edu")
//                .password("Password123!")
//                .build();
//
//        // When
//        LoginResponse response = authService.login(loginRequest);
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.getAccessToken()).isNotNull();
//        assertThat(response.getRefreshToken()).isNotNull();
//        assertThat(response.getUser()).isNotNull();
//        assertThat(response.getUser().getEmail()).isEqualTo("jane.smith@uic.edu");
//    }
//
//    @Test
//    @DisplayName("Reset password full flow")
//    void resetPassword_FullFlow() {
//
//        // Given
//        SignupRequest signupRequest = SignupRequest.builder()
//                .firstName("Bob")
//                .lastName("Johnson")
//                .email("bob.johnson@uic.edu")
//                .password("OldPassword123!")
//                .build();
//        authService.signup(signupRequest);
//
//        User user = userRepository.findByEmail("bob.johnson@uic.edu").orElseThrow();
//
//        // Request password reset
//        PasswordResetCodeRequest resetCodeRequest = PasswordResetCodeRequest.builder()
//                .email("bob.johnson@uic.edu")
//                .build();
//        authService.requestPasswordReset(resetCodeRequest);
//
//        // Get the generated token from the database
//        PasswordReset passwordReset = passwordResetRepository.findByUser_UserId(user.getUserId()).orElseThrow();
//        String token = passwordReset.getToken();
//
//        // Reset password with token
//        PasswordResetRequest resetRequest = PasswordResetRequest.builder()
//                .token(token)
//                .newPassword("NewPassword123!")
//                .build();
//
//        // When
//        authService.resetPassword(resetRequest);
//
//        // Then
//        User updatedUser = userRepository.findByEmail("bob.johnson@uic.edu").orElseThrow();
//        assertThat(passwordEncoder.matches("NewPassword123!", updatedUser.getPasswordHash())).isTrue();
//
//        // Check that the token was marked as used
//        PasswordReset usedReset = passwordResetRepository.findByToken(token).orElseThrow();
//        assertThat(usedReset.isUsed()).isTrue();
//    }
//
//    @Test
//    @DisplayName("Change password full flow")
//    void changePassword_FullFlow() {
//        // Given
//        SignupRequest signupRequest = SignupRequest.builder()
//                .firstName("Alice")
//                .lastName("Williams")
//                .email("alice.williams@uic.edu")
//                .password("OldPassword123!")
//                .build();
//        authService.signup(signupRequest);
//
//        User user = userRepository.findByEmail("alice.williams@uic.edu").orElseThrow();
//
//        ChangePasswordRequest changeRequest = ChangePasswordRequest.builder()
//                .currentPassword("OldPassword123!")
//                .newPassword("NewPassword123!")
//                .build();
//
//        // When
//        authService.changePassword(user.getUserId(), changeRequest);
//
//        // Then
//        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
//        assertThat(passwordEncoder.matches("NewPassword123!", updatedUser.getPasswordHash())).isTrue();
//        assertThat(passwordEncoder.matches("OldPassword123!", updatedUser.getPasswordHash())).isFalse();
//    }
//
//    @Test
//    @DisplayName("signup duplicate email")
//    void signup_DuplicateEmail() {
//
//        // Given
//        SignupRequest request1 = SignupRequest.builder()
//                .firstName("User")
//                .lastName("One")
//                .email("duplicate@uic.edu")
//                .password("Password123!")
//                .build();
//        authService.signup(request1);
//
//        SignupRequest request2 = SignupRequest.builder()
//                .firstName("User")
//                .lastName("Two")
//                .email("duplicate@uic.edu")
//                .password("Password123!")
//                .build();
//
//        // When & Then
//        assertThatThrownBy(() -> authService.signup(request2))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Email already exists");
//    }
//
//    @Test
//    @DisplayName("Login suspended account")
//    void login_SuspendedAccount() {
//
//        // Given
//        SignupRequest signupRequest = SignupRequest.builder()
//                .firstName("Charlie")
//                .lastName("Brown")
//                .email("charlie.brown@uic.edu")
//                .password("Password123!")
//                .build();
//        authService.signup(signupRequest);
//
//        User user = userRepository.findByEmail("charlie.brown@uic.edu").orElseThrow();
//        user.setEmailVerified(true);
//        user.setStatus(UserStatus.SUSPENDED);
//        userRepository.save(user);
//
//        LoginRequest loginRequest = LoginRequest.builder()
//                .email("charlie.brown@uic.edu")
//                .password("Password123!")
//                .build();
//
//        // When & Then
//        assertThatThrownBy(() -> authService.login(loginRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Account is suspended");
//    }
//
//    @Test
//    @DisplayName("Reset password with expired token")
//    void resetPassword_ExpiredToken() {
//        // Given
//        SignupRequest signupRequest = SignupRequest.builder()
//                .firstName("David")
//                .lastName("Lee")
//                .email("david.lee@uic.edu")
//                .password("Password123!")
//                .build();
//        authService.signup(signupRequest);
//
//        User user = userRepository.findByEmail("david.lee@uic.edu").orElseThrow();
//
//        // Create an expired password reset token
//        PasswordReset expiredReset = PasswordReset.builder()
//                .user(user)
//                .token("expired_token")
//                .expiresAt(Instant.now().minusSeconds(3600)) // Expired 1 hour ago
//                .build();
//        passwordResetRepository.save(expiredReset);
//
//        PasswordResetRequest resetRequest = PasswordResetRequest.builder()
//                .token("expired_token")
//                .newPassword("NewPassword123!")
//                .build();
//
//        // When & Then
//        assertThatThrownBy(() -> authService.resetPassword(resetRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("expired");
//    }
//
//    @Test
//    @DisplayName("Reset password with already used token")
//    void resetPassword_UsedToken() {
//        // Given
//        SignupRequest signupRequest = SignupRequest.builder()
//                .firstName("Emma")
//                .lastName("Davis")
//                .email("emma.davis@uic.edu")
//                .password("Password123!")
//                .build();
//        authService.signup(signupRequest);
//
//        User user = userRepository.findByEmail("emma.davis@uic.edu").orElseThrow();
//
//        // Create a used password reset token
//        PasswordReset usedReset = PasswordReset.builder()
//                .user(user)
//                .token("used_token")
//                .expiresAt(Instant.now().plusSeconds(3600))
//                .usedAt(Instant.now()) // Already used
//                .build();
//        passwordResetRepository.save(usedReset);
//
//        PasswordResetRequest resetRequest = PasswordResetRequest.builder()
//                .token("used_token")
//                .newPassword("NewPassword123!")
//                .build();
//
//        // When & Then
//        assertThatThrownBy(() -> authService.resetPassword(resetRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("used");
//    }
//}
