package edu.uic.marketplace.repository.search;

import edu.uic.marketplace.model.search.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {
    
    /**
     * Find saved searches by user
     */
    List<SavedSearch> findByUser_UserId(Long userId);
    
    /**
     * Find saved searches by user ordered by created date
     */
    List<SavedSearch> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Check if user has saved search with query hash
     */
    boolean existsByUser_UserIdAndQueryHash(Long userId, String queryHash);
    
    /**
     * Count saved searches by user
     */
    Long countByUser_UserId(Long userId);
    
    /**
     * Delete saved searches by user
     */
    void deleteByUser_UserId(Long userId);
}
