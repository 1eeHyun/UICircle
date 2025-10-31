package edu.uic.marketplace.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "users",
    indexes = {
            @Index(name = "idx_users_email", columnList = "email"),
            @Index(name = "idx_users_phone_number", columnList = "phone_number"),
            @Index(name = "idx_users_role", columnList = "role"),
            @Index(name = "idx_users_status", columnList = "status"),
            @Index(name = "idx_users_created_at", columnList = "created_at")
    },
    uniqueConstraints = {
            @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
            @UniqueConstraint(name = "uk_users_phone_number", columnNames = "phone_number"),
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * User's name fields
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "middle_name", length = 50)
    private String middleName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Email address (unique identifier)
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Email verification status
     */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Hashed password
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * Phone number (optional)
     */
    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    /**
     * User role (USER, PROFESSOR, ADMIN)
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    /**
     * Account status (ACTIVE, SUSPENDED, DELETED)
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Timestamps
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /**
     * Helper Methods
     */
    public String getFullName() {
        if (middleName != null && !middleName.isEmpty())
            return firstName + " " + middleName + " " + lastName;

        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE && deletedAt == null;
    }

    public void updateLastLogin() {
        this.lastLoginAt = Instant.now();
    }

    public void softDelete() {
        this.status = UserStatus.DELETED;
        this.deletedAt = Instant.now();
    }
}
