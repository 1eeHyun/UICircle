package edu.uic.marketplace.dto.response.auth;

import edu.uic.marketplace.dto.response.user.UserResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private UserResponse user;
}