package edu.uic.marketplace.repository.user;

import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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

    // =================================================================
    // Authentication Methods - Optimized
    // =================================================================

    /**
     * Find user by email or username (case-insensitive) for login
     * Single query instead of two separate queries
     * OPTIMIZED: Reduces 2 queries to 1 query
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:input) OR LOWER(u.username) = LOWER(:input)")
    Optional<User> findByEmailOrUsername(@Param("input") String input);

    /**
     * Find user by email or username with active status check
     * OPTIMIZED: Single query with status validation
     */
    @Query("SELECT u FROM User u WHERE (LOWER(u.email) = LOWER(:input) OR LOWER(u.username) = LOWER(:input)) AND u.status = :status")
    Optional<User> findByEmailOrUsernameAndStatus(@Param("input") String input, @Param("status") UserStatus status);

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Find user by username (case-insensitive)
     */
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
     * Check if email exists (optimized - no entity loading)
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists (optimized - no entity loading)
     */
    boolean existsByUsername(String username);

    /**
     * Check if email or username exists (optimized - single query)
     * OPTIMIZED: Reduces 2 exists checks to 1 query
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.email) = LOWER(:input) OR LOWER(u.username) = LOWER(:input)")
    boolean existsByEmailOrUsername(@Param("input") String input);

    // =================================================================
    // Update Methods - Optimized for performance
    // =================================================================

    /**
     * Update last login time efficiently without loading entire entity
     * OPTIMIZED: Bulk update query, no SELECT needed
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.userId = :userId")
    void updateLastLoginAt(@Param("userId") Long userId, @Param("loginTime") Instant loginTime);

    /**
     * Update user status efficiently
     * OPTIMIZED: Bulk update query
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.userId = :userId")
    void updateStatus(@Param("userId") Long userId, @Param("status") UserStatus status);

    /**
     * Update email verification status efficiently
     * OPTIMIZED: Bulk update query
     */
    @Modifying
    @Query("UPDATE User u SET u.emailVerified = :verified WHERE u.userId = :userId")
    void updateEmailVerified(@Param("userId") Long userId, @Param("verified") boolean verified);

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