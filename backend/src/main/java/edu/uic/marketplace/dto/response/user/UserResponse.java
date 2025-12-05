package edu.uic.marketplace.dto.response.user;

import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String publicId;
    private String username;
    private String email;
    private String name;
    private String firstName;
    private String lastName;
    private UserRole role;
    private UserStatus status;
    private Instant createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getFullName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}