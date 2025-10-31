package edu.uic.marketplace.service.auth;

import edu.uic.marketplace.dto.request.auth.*;
import edu.uic.marketplace.dto.response.auth.LoginResponse;
import edu.uic.marketplace.dto.response.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public void signup(SignupRequest request) {
    }

    @Override
    public void verifyEmail(String token) {

    }

    @Override
    public void resendVerificationEmail(String email) {

    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public void logout(Long userId, String token) {

    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public void requestPasswordReset(PasswordResetCodeRequest request) {

    }

    @Override
    public void resetPassword(PasswordResetRequest request) {

    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {

    }

    @Override
    public Long validateToken(String token) {
        return null;
    }
}
