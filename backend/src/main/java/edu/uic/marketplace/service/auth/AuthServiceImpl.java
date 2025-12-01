package edu.uic.marketplace.service.auth;

import edu.uic.marketplace.dto.request.auth.*;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.auth.TokenResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.model.verification.EmailVerification;
import edu.uic.marketplace.model.verification.PasswordReset;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.repository.verification.EmailVerificationRepository;
import edu.uic.marketplace.repository.verification.PasswordResetRepository;
import edu.uic.marketplace.security.JwtTokenProvider;
import edu.uic.marketplace.service.email.EmailService;
import edu.uic.marketplace.service.user.ProfileService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetRepository passwordResetRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final ProfileService profileService;

    private final AuthValidator authValidator;

    @Override
    @Transactional
    public void signup(SignupRequest request) {

        // 1) Validate email and username
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        // 2) Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .status(UserStatus.PENDING)
                .createdAt(Instant.now())
                .emailVerified(false)
                .build();

        userRepository.save(user);

        // 3) Create profile for the new user
        profileService.createProfile(user);

        // 4) Create verification token
        String token = UUID.randomUUID().toString();
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(24 * 3600)) // 24 hours
                .build();

        emailVerificationRepository.save(verification);


        // 5) Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), token);
        } catch (Exception e) {
            // Log error but don't fail signup
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        // 1) Find verification by token
        EmailVerification verification = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        // 2) Validate using helper methods
        if (verification.isExpired()) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        if (verification.isVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }

        // 3) Mark user email as verified and activate account
        User user = verification.getUser();
        userRepository.updateEmailVerified(user.getUserId(), true);
        userRepository.updateStatus(user.getUserId(), UserStatus.ACTIVE);

        // 4) Mark verification as complete
        verification.verify();
        emailVerificationRepository.save(verification);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {

        // 1) Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2) Check if already verified
        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }

        // 3) Invalidate old verifications
        emailVerificationRepository.deleteByUser_Username(user.getUsername());

        // 4) Create new verification token
        String token = UUID.randomUUID().toString();
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(24 * 3600))
                .build();

        emailVerificationRepository.save(verification);

        // 5) Send email
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        String input = request.getEmailOrUsername() == null ? "" : request.getEmailOrUsername().trim();

        User user = authValidator.validateLogin(input, request.getPassword());
        // Check user's verification status
        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalStateException("Email verification required");
        }

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
        // TODO: Implement token blacklist if needed
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        // TODO: Implement refresh token logic
        return null;
    }

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetCodeRequest request) {

        // 1) Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2) Invalidate old password reset requests
        passwordResetRepository.deleteByUser_UserId(user.getUserId());

        // 3) Create new password reset token
        String token = UUID.randomUUID().toString();
        PasswordReset passwordReset = PasswordReset.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(3600)) // 1 hour
                .build();

        passwordResetRepository.save(passwordReset);

        // 4) Send password reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (Exception e) {
            // Log error
            log.error("Failed to send verification email", e);
            throw new RuntimeException("Failed to send password reset email");
        }
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest request) {

        // 1) Find password reset by token
        PasswordReset passwordReset = passwordResetRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        // 2) Validate using helper methods
        if (passwordReset.isExpired()) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        if (passwordReset.isUsed()) {
            throw new IllegalArgumentException("Password reset token already used");
        }

        // 3) Update password
        User user = passwordReset.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 4) Mark token as used
        passwordReset.markAsUsed();
        passwordResetRepository.save(passwordReset);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

        // 1) Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2) Verify old password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        // 3) Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Long validateToken(String token) {

        String username = jwtTokenProvider.getUsernameFromJWT(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        return user.getUserId();
    }
}