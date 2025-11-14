package edu.uic.marketplace.repository.search;

import edu.uic.marketplace.model.search.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {

    /**
     * Find saved searches by user
     */
    List<SavedSearch> findByUser_Username(String username);

    /**
     * Find saved searches by user
     */
   Optional<SavedSearch> findByPublicId(String publicId);


    /**
     * Find saved searches by user ordered by created date
     */
    List<SavedSearch> findByUser_UsernameOrderByCreatedAtDesc(String username);

    /**
     * Check if user has saved search with query hash
     */
    boolean existsByUser_UsernameAndQueryHash(String username, String queryHash);

    /**
     * Count saved searches by user
     */
    Long countByUser_Username(String username);

    /**
     * Delete saved searches by user
     */
    void deleteByUser_Username(String username);

    /**
     * username + queryHash
     */
    Optional<SavedSearch> findByUser_UsernameAndQueryHash(String username, String queryHash);
}
