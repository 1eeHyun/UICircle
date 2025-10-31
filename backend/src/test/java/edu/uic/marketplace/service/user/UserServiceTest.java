package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@uic.edu")
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Find user by ID - Success")
    void findById_Success() {

        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
        assertThat(result.get().getEmail()).isEqualTo("john.doe@uic.edu");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Find user by ID - Not found")
    void findById_NotFound() {

        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Find user by email - Success")
    void findByEmail_Success() {

        // Given
        when(userRepository.findByEmail("john.doe@uic.edu")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail("john.doe@uic.edu");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john.doe@uic.edu");
        verify(userRepository, times(1)).findByEmail("john.doe@uic.edu");
    }

    @Test
    @DisplayName("Find user by email - Not found")
    void findByEmail_NotFound() {

        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByEmail("notfound@uic.edu");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByEmail("notfound@uic.edu");
    }

    @Test
    @DisplayName("Get UserResponse - Success")
    void getUserById_Success() {

        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserResponse result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@uic.edu");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get UserResponse - User not found exception")
    void getUserById_NotFound_ThrowsException() {

        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Check if email exists - True")
    void existsByEmail_True() {
        // Given
        when(userRepository.existsByEmail("john.doe@uic.edu")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("john.doe@uic.edu");

        // Then
        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsByEmail("john.doe@uic.edu");
    }

    @Test
    @DisplayName("Check if email exists - False")
    void existsByEmail_False() {

        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // When
        boolean result = userService.existsByEmail("notfound@uic.edu");

        // Then
        assertThat(result).isFalse();
        verify(userRepository, times(1)).existsByEmail("notfound@uic.edu");
    }

    @Test
    @DisplayName("Delete user (Soft Delete) - Success")
    void deleteUser_Success() {

        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.deleteUser(1L);

        // Then
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Delete user - User not found exception")
    void deleteUser_NotFound_ThrowsException() {

        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Suspend user - Success")
    void suspendUser_Success() {

        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.suspendUser(1L, "Violation of terms");

        // Then
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Suspend user - User not found exception")
    void suspendUser_NotFound_ThrowsException() {

        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.suspendUser(999L, "Reason"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Activate user - Success")
    void activateUser_Success() {

        // Given
        testUser.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.activateUser(1L);

        // Then
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Activate user - User not found exception")
    void activateUser_NotFound_ThrowsException() {

        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.activateUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any());
    }
}
