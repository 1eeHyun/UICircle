package edu.uic.marketplace.repository.user;

import edu.uic.marketplace.model.user.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    /**
     * Find profile by user ID
     */
    Optional<Profile> findByUser_UserId(Long userId);
    
    /**
     * Check if display name exists
     */
    boolean existsByDisplayName(String displayName);
    
    /**
     * Delete profile by user ID
     */
    void deleteByUser_UserId(Long userId);
}
