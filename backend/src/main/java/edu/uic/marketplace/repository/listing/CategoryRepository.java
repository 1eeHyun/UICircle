package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // =================================================================
    // External API Methods - Use slug for all external operations
    // =================================================================

    /**
     * Find category by slug (for external API calls)
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Check if slug exists
     */
    boolean existsBySlug(String slug);

    /**
     * Find all root categories (categories with no parent)
     */
    List<Category> findByParentIsNull();

    /**
     * Find subcategories by parent category slug
     */
    List<Category> findByParent_Slug(String parentSlug);

    /**
     * Find all root categories with children efficiently using batch fetch
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.name")
    List<Category> findRootCategories();


    /**
     * Find all categories with their children (for category tree)
     */
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.children")
    List<Category> findAllWithChildren();

    /**
     * Check if category has any subcategories
     */
    boolean existsByParent_Slug(String parentSlug);

    /**
     * Count subcategories of a category
     */
    Long countByParent_Slug(String parentSlug);

    // =================================================================
    // Internal Methods - Use Long ID only for FK relationships
    // =================================================================

    /**
     * Find category by internal ID (for internal FK operations only)
     * Do not expose this in external APIs
     */
    Optional<Category> findById(Long categoryId);

    /**
     * Find categories by parent internal ID (for internal operations)
     */
    List<Category> findByParent_CategoryId(Long parentId);

    /**
     * Check if category exists by internal ID
     */
    boolean existsById(Long categoryId);
}
