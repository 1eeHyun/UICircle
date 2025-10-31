package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserService Integration Test")
class UserServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Create and find user")
    void createAndFindUser() {

        // Given
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        User savedUser = userRepository.save(user);

        // When
        Optional<User> foundUser = userService.findById(savedUser.getUserId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@uic.edu");
        assertThat(foundUser.get().getFirstName()).isEqualTo("John");
        assertThat(foundUser.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("Find user by email")
    void findByEmail() {

        // Given
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        userRepository.save(user);

        // When
        Optional<User> foundUser = userService.findByEmail("jane.smith@uic.edu");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane.smith@uic.edu");
        assertThat(foundUser.get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("Find user by email - Not found")
    void findByEmail_NotFound() {

        // When
        Optional<User> foundUser = userService.findByEmail("notfound@uic.edu");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Get UserResponse - Success")
    void getUserById_Success() {

        // Given
        User user = User.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .email("bob.johnson@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        User savedUser = userRepository.save(user);

        // When
        UserResponse response = userService.getUserById(savedUser.getUserId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(response.getEmail()).isEqualTo("bob.johnson@uic.edu");
        assertThat(response.getFirstName()).isEqualTo("Bob");
        assertThat(response.getLastName()).isEqualTo("Johnson");
    }

    @Test
    @DisplayName("Get UserResponse - User not found")
    void getUserById_NotFound() {

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Check if email exists")
    void existsByEmail() {

        // Given
        User user = User.builder()
                .firstName("Alice")
                .lastName("Williams")
                .email("alice.williams@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        userRepository.save(user);

        // When
        boolean exists = userService.existsByEmail("alice.williams@uic.edu");
        boolean notExists = userService.existsByEmail("notfound@uic.edu");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Delete user (Soft Delete)")
    void deleteUser() {

        // Given
        User user = User.builder()
                .firstName("Charlie")
                .lastName("Brown")
                .email("charlie.brown@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        User savedUser = userRepository.save(user);

        // When
        userService.deleteUser(savedUser.getUserId());

        // Then
        User deletedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertThat(deletedUser.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(deletedUser.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Suspend user")
    void suspendUser() {

        // Given
        User user = User.builder()
                .firstName("David")
                .lastName("Lee")
                .email("david.lee@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        User savedUser = userRepository.save(user);

        // When
        userService.suspendUser(savedUser.getUserId(), "Violation of terms");

        // Then
        User suspendedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertThat(suspendedUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    @DisplayName("Activate user")
    void activateUser() {

        // Given
        User user = User.builder()
                .firstName("Emma")
                .lastName("Davis")
                .email("emma.davis@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.SUSPENDED)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        User savedUser = userRepository.save(user);

        // When
        userService.activateUser(savedUser.getUserId());

        // Then
        User activatedUser = userRepository.findById(savedUser.getUserId()).orElseThrow();
        assertThat(activatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Duplicate email constraint violation")
    void duplicateEmail_ThrowsException() {

        // Given
        User user1 = User.builder()
                .firstName("First")
                .lastName("User")
                .email("duplicate@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        userRepository.save(user1);

        User user2 = User.builder()
                .firstName("Second")
                .lastName("User")
                .email("duplicate@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();

        // When & Then
        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Transaction rollback test")
    void transactionRollback() {

        // Given
        User user = User.builder()
                .firstName("Test")
                .lastName("Rollback")
                .email("rollback@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();

        // When
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getUserId();

        // Then - Should be rolled back at the end of transaction
        // Automatic rollback on test method completion due to @Transactional
        assertThat(userRepository.findById(userId)).isPresent();
    }
}
