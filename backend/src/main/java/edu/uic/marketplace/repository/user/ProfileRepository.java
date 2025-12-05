package edu.uic.marketplace.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uic.marketplace.model.user.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    /**
     * Find profile by user ID
     */
    Optional<Profile> findByUser_UserId(Long userId);

    /**
     * Find profile by public ID
     */
    Optional<Profile> findByPublicId(String publicId);

    /**
     * Find profile by public ID with user eagerly loaded
     */
    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.user WHERE p.publicId = :publicId")
    Optional<Profile> findByPublicIdWithUser(@Param("publicId") String publicId);

    /**
     * Find profile by username
     */
    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.user u WHERE u.username = :username")
    Optional<Profile> findByUsername(@Param("username") String username);
    
    /**
     * Check if display name exists
     */
    boolean existsByDisplayName(String displayName);
    
    /**
     * Delete profile by user ID
     */
    void deleteByUser_UserId(Long userId);
}
