package edu.uic.marketplace.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email/Username is required")
    private String emailOrUsername;

    @NotBlank(message = "Password is required")
    private String password;
}