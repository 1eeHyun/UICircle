package edu.uic.marketplace.repository.user;

import edu.uic.marketplace.model.user.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    /**
     * Find badge by code
     */
    Optional<Badge> findByCode(String code);
    
    /**
     * Check if badge code exists
     */
    boolean existsByCode(String code);
}
