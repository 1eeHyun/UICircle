package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name
     */
    Optional<Category> findById(Long id);
    
    /**
     * Find category by name
     */
    Optional<Category> findByName(String name);
    
    /**
     * Find top-level categories (no parent)
     */
    List<Category> findByParentIsNull();
    
    /**
     * Find subcategories by parent
     */
    List<Category> findByParent(Category parent);
    
    /**
     * Check if category has subcategories
     */
    boolean existsByParent(Category parent);
    
    /**
     * Check if category name exists
     */
    boolean existsByName(String name);
}
