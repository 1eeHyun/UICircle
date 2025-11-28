package edu.uic.marketplace.service.auth;

import edu.uic.marketplace.dto.request.auth.*;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.auth.TokenResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.repository.verification.EmailVerificationRepository;
import edu.uic.marketplace.security.JwtTokenProvider;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthValidator authValidator;

    @Override
    @Transactional
    public void signup(SignupRequest request) {

        // 1) validate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        // 2) create a new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .emailVerified(false)
                .build();

        userRepository.save(user);

        // TODO: send verificaton code to the email, future feature

        // 3) send a verification code
//        EmailVerification verification = EmailVerification.builder()
//                .user(user)
//                .token(UUID.randomUUID().toString())
//                .expiresAt(Instant.now().plusSeconds(24 * 3600)) // 24hrs
//                .build();
//
//        emailVerificationRepository.save(verification);

//        String verifyLink = "https://your-frontend.com/verify?token=" + verification.getToken();
//        mailService.sendVerificationEmail(user.getEmail(), verifyLink);
    }

    @Override
    public void verifyEmail(String token) {
        // TODO: future feature
    }

    @Override
    public void resendVerificationEmail(String email) {

        // TODO: future feature
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        String input = request.getEmailOrUsername() == null ? "" : request.getEmailOrUsername().trim();

        User user = authValidator.validateLogin(input, request.getPassword());

        // JWT
        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateToken(user.getUsername());
        int expiresIn = (int) (jwtTokenProvider.getJwtExpirationInMs() / 1000);

        // Update lastLoginAt
        updateLastLoginAt(user.getUserId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void updateLastLoginAt(Long userId) {
        userRepository.updateLastLoginAt(userId, Instant.now());
    }

    @Override
    public void logout(Long userId, String token) {

    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {

        // TODO: future feature
        return null;
    }

    @Override
    public void requestPasswordReset(PasswordResetCodeRequest request) {

        // TODO: future feature

    }

    @Override
    public void resetPassword(PasswordResetRequest request) {

        // TODO: future feature

    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {

        // TODO: future feature
    }

    @Override
    public Long validateToken(String token) {

        // TODO: future feature
        return null;
    }
}
