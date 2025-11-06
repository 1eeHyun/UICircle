package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name (case-insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findByParent_CategoryIdOrderByNameAsc(Long parentId);

    /**
     * Find top-level categories (parent_id IS NULL)
     */
    List<Category> findByParentIsNullOrderByNameAsc();

    /**
     * Find subcategories by parent
     */
    List<Category> findByParentOrderByNameAsc(Category parent);

    /**
     * Check if category has subcategories
     */
    boolean existsByParent(Category parent);

    /**
     * Check if a category name already exists under the same parent (unique constraint)
     */
    boolean existsByParentAndNameIgnoreCase(Category parent, String name);

    /**
     * Find all categories with their children loaded (to avoid N+1)
     */
    @EntityGraph(attributePaths = {"children"})
    List<Category> findAllByOrderByNameAsc();
}
