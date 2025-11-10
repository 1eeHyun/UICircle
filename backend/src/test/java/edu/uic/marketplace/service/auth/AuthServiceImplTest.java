package edu.uic.marketplace.service.auth;

import edu.uic.marketplace.dto.request.auth.LoginRequest;
import edu.uic.marketplace.dto.request.auth.SignupRequest;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.exception.auth.UserNotFoundException;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.repository.verification.EmailVerificationRepository;
import edu.uic.marketplace.security.JwtTokenProvider;
import edu.uic.marketplace.validator.auth.AuthValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Unit Tests")
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private EmailVerificationRepository emailVerificationRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthValidator authValidator;

    @InjectMocks
    private AuthServiceImpl authService;

    /* ------------------------ Helper Methods ------------------------ */

    private SignupRequest signupReq(String username, String email, String rawPw) {

        SignupRequest r = new SignupRequest();
        r.setUsername(username);
        r.setEmail(email);
        r.setPassword(rawPw);
        r.setFirstName("Donghyeon");
        r.setMiddleName(null);
        r.setLastName("Lee");
        r.setPhoneNumber("123-456-7890");
        r.setRole(UserRole.USER);
        return r;
    }

    private User userEntity(String username, String email, String pwHash, UserStatus status) {
        return User.builder()
                .userId(1L)
                .username(username)
                .email(email)
                .passwordHash(pwHash)
                .role(UserRole.USER)
                .status(status)
                .emailVerified(false)
                .createdAt(Instant.now())
                .build();
    }

    /* ===================== SignUp Tests ===================== */

    @Nested
    @DisplayName("signup()")
    class SignupTests {

        @Test
        @DisplayName("Success: saves new user, encodes password, status ACTIVE")
        void signup_success() {

            // given
            SignupRequest req = signupReq("lee", "lee@example.com", "pw1234");

            when(userRepository.existsByEmail("lee@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("lee")).thenReturn(false);
            when(passwordEncoder.encode("pw1234")).thenReturn("ENCODED");

            // Capture the saved User entity for verification
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            when(userRepository.save(any(User.class)))
                    .thenAnswer(inv -> inv.getArgument(0)); // return the same saved entity

            // when
            authService.signup(req);

            // then
            verify(userRepository).existsByEmail("lee@example.com");
            verify(userRepository).existsByUsername("lee");
            verify(passwordEncoder).encode("pw1234");
            verify(userRepository).save(captor.capture());

            User saved = captor.getValue();
            assertThat(saved.getUsername()).isEqualTo("lee");
            assertThat(saved.getEmail()).isEqualTo("lee@example.com");
            assertThat(saved.getPasswordHash()).isEqualTo("ENCODED");
            assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(saved.getEmailVerified()).isFalse();
            assertThat(saved.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Fail: duplicate email → IllegalArgumentException")
        void signup_emailDuplicate() {

            // given
            SignupRequest req = signupReq("lee", "dup@example.com", "pw");
            when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

            // when/then
            assertThatThrownBy(() -> authService.signup(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already in use");

            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: duplicate username → IllegalArgumentException")
        void signup_usernameDuplicate() {

            // given
            SignupRequest req = signupReq("dupUser", "ok@example.com", "pw");
            when(userRepository.existsByEmail("ok@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("dupUser")).thenReturn(true);

            // when/then
            assertThatThrownBy(() -> authService.signup(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Username already in use");

            verify(userRepository, never()).save(any());
        }
    }

    /* ====================== Login Tests ===================== */

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("Success: issues JWTs, computes expiresIn, updates lastLoginAt")
        void login_success() {

            // given
            LoginRequest req = new LoginRequest();
            req.setEmailOrUsername(" lee "); // extra whitespace -> should be trimmed internally
            req.setPassword("pw1234");

            User user = userEntity("lee", "lee@example.com", "ENC", UserStatus.ACTIVE);

            when(authValidator.validateLogin("lee", "pw1234")).thenReturn(user);
            when(jwtTokenProvider.generateToken("lee")).thenReturn("ATOKEN").thenReturn("RTOKEN");
            when(jwtTokenProvider.getJwtExpirationInMs()).thenReturn(900_000L); // 15 minutes

            // when
            LoginResponse res = authService.login(req);

            // then
            assertThat(res.getAccessToken()).isEqualTo("ATOKEN");
            assertThat(res.getRefreshToken()).isEqualTo("RTOKEN");
            assertThat(res.getTokenType()).isEqualTo("Bearer");
            assertThat(res.getExpiresIn()).isEqualTo(900_000 / 1000);
            assertThat(res.getUser()).extracting(UserResponse::getUsername).isEqualTo("lee");

            verify(authValidator).validateLogin("lee", "pw1234");
            verify(jwtTokenProvider, times(2)).generateToken("lee");
            verify(jwtTokenProvider).getJwtExpirationInMs();

            // lastLoginAt should be updated
            assertThat(user.getLastLoginAt()).isNotNull();
        }

        @Test
        @DisplayName("Fail: invalid credentials → UserNotFoundException")
        void login_invalidCredentials() {

            // given
            LoginRequest req = new LoginRequest();
            req.setEmailOrUsername("lee");
            req.setPassword("wrong");

            when(authValidator.validateLogin("lee", "wrong"))
                    .thenThrow(new UserNotFoundException("Invalid email/username or password"));

            // when / then
            assertThatThrownBy(() -> authService.login(req))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Invalid email/username or password");

            verify(jwtTokenProvider, never()).generateToken(anyString());
        }
    }
}
