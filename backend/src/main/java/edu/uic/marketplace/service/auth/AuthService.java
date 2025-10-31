package edu.uic.marketplace.service.auth;

import edu.uic.marketplace.dto.request.auth.SignupRequest;
import edu.uic.marketplace.dto.request.auth.LoginRequest;
import edu.uic.marketplace.dto.request.auth.PasswordResetRequest;
import edu.uic.marketplace.dto.request.auth.PasswordResetCodeRequest;
import edu.uic.marketplace.dto.request.auth.ChangePasswordRequest;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.auth.TokenResponse;

/**
 * Authentication service interface
 */
public interface AuthService {
    
    /**
     * Register new user with email verification
     * @param request Signup request
     */
    void signup(SignupRequest request);
    
    /**
     * Verify email with token
     * @param token Verification token
     */
    void verifyEmail(String token);
    
    /**
     * Resend email verification
     * @param email User email
     */
    void resendVerificationEmail(String email);
    
    /**
     * Login with email and password
     * @param request Login request
     * @return Login response with tokens
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * Logout (invalidate session/token)
     * @param userId User ID
     * @param token Access token
     */
    void logout(Long userId, String token);
    
    /**
     * Refresh access token
     * @param refreshToken Refresh token
     * @return New token response
     */
    TokenResponse refreshToken(String refreshToken);
    
    /**
     * Request password reset (send email with code)
     * @param request Password reset code request
     */
    void requestPasswordReset(PasswordResetCodeRequest request);
    
    /**
     * Reset password with code
     * @param request Password reset request
     */
    void resetPassword(PasswordResetRequest request);
    
    /**
     * Change password (authenticated user)
     * @param userId User ID
     * @param request Change password request
     */
    void changePassword(Long userId, ChangePasswordRequest request);
    
    /**
     * Validate access token
     * @param token Access token
     * @return User ID if valid
     */
    Long validateToken(String token);
}
