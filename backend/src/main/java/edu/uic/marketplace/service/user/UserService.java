package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.user.User;

import java.util.Optional;

/**
 * User management service interface
 */
public interface UserService {
    
    /**
     * Find user by ID
     * @param userId User ID
     * @return User entity
     */
    Optional<User> findById(Long userId);
    
    /**
     * Find user by email
     * @param email User email
     * @return User entity
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Get user response by ID
     * @param userId User ID
     * @return UserResponse DTO
     */
    UserResponse getUserById(Long userId);
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Delete user account (soft delete)
     * @param userId User ID
     */
    void deleteUser(Long userId);
    
    /**
     * Suspend user account
     * @param userId User ID
     * @param reason Suspension reason
     */
    void suspendUser(Long userId, String reason);
    
    /**
     * Activate suspended user
     * @param userId User ID
     */
    void activateUser(Long userId);
}
