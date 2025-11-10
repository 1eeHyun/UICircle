package edu.uic.marketplace.repository.user;

import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // =================================================================
    // External API Methods - Use publicId for all external operations
    // =================================================================

    /**
     * Find user by public ID (for external API calls)
     */
    Optional<User> findByPublicId(String publicId);

    /**
     * Find active user by public ID
     */
    Optional<User> findByPublicIdAndStatus(String publicId, UserStatus status);

    /**
     * Check if public ID exists
     */
    boolean existsByPublicId(String publicId);

    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Find user by username (for authentication)
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email (for authentication/password reset)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    // =================================================================
    // Internal Methods - Use Long ID only for FK relationships
    // =================================================================

    /**
     * Find user by internal ID (for internal FK operations only)
     * Do not expose this in external APIs
     */
    Optional<User> findById(Long userId);

    /**
     * Check if internal ID exists (for FK validation)
     */
    boolean existsById(Long userId);
}
