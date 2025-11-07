package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.model.listing.Category;

import java.util.List;

/**
 * Category management service interface
 */
public interface CategoryService {
    
    /**
     * Get all categories
     * @return List of all categories
     */
    List<CategoryResponse> getAllCategories();
    
    /**
     * Get category by Slug
     * @param categorySlug Category Slug
     * @return Category entity
     */
    Category findBySlug(String categorySlug);

    
    /**
     * Get top-level categories (parent_id is null)
     * @return List of top-level categories
     */
    List<CategoryResponse> getTopLevelCategories();
    
    /**
     * Get subcategories of a parent category
     * @param parentSlug Parent category ID
     * @return List of subcategories
     */
    List<CategoryResponse> getSubcategories(String parentSlug);
    
    /**
     * Create new category (Admin only)
     * @param name Category name
     * @param parentId Parent category ID (nullable for top-level)
     * @return Created category response
     */
    CategoryResponse createCategory(String username, String name, Long parentId);
    
    /**
     * Update category name (Admin only)
     * @param categoryId Category ID
     * @param name New category name
     * @return Updated category response
     */
    CategoryResponse updateCategory(String publicCategoryId, String name);
    
    /**
     * Delete category (Admin only)
     * Cannot delete if it has listings or subcategories
     * @param categoryId Category ID
     */
    void deleteCategory(String publicCategoryId);
    
    /**
     * Check if category exists
     * @param categoryId Category ID
     * @return true if exists, false otherwise
     */
    boolean existsById(String publicCategoryId);
}
