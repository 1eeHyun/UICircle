package edu.uic.marketplace.validator.listing;

import edu.uic.marketplace.exception.listing.CategoryNotFoundException;
import edu.uic.marketplace.exception.listing.CategoryNotSubCategoryException;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    // =================================================================
    // External API Validation - Use slug
    // =================================================================

    /**
     * Validate that category exists by slug
     */
    public Category validateCategoryBySlug(String slug) {

        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException("Category with slug '" + slug + "' not found"));
    }

    /**
     * Validate that category is a leaf category (subcategory with no children)
     * Listings can only be assigned to leaf categories
     */
    public Category validateLeafCategory(String slug) {

        Category category = validateCategoryBySlug(slug);

        if (category.hasChildren()) {
            throw new CategoryNotSubCategoryException(
                    "Category '" + slug + "' has subcategories. Listings must be assigned to leaf categories only.");
        }

        return category;
    }

    /**
     * Validate that category is a root category (no parent)
     */
    public Category validateRootCategory(String slug) {
        Category category = validateCategoryBySlug(slug);

        if (!category.isRootCategory()) {
            throw new IllegalArgumentException(
                    "Category '" + slug + "' is not a root category");
        }

        return category;
    }

    /**
     * Validate that category has a parent (is not a root category)
     */
    public Category validateSubCategory(String slug) {

        Category category = validateCategoryBySlug(slug);

        if (category.isRootCategory()) {
            throw new IllegalArgumentException(
                    "Category '" + slug + "' is a root category, not a subcategory");
        }

        return category;
    }

    /**
     * Validate that parent category exists and retrieve subcategory
     */
    public Category validateSubCategoryOfParent(String parentSlug, String childSlug) {

        Category parent = validateCategoryBySlug(parentSlug);
        Category child = validateCategoryBySlug(childSlug);

        if (child.getParent() == null || !child.getParent().getCategoryId().equals(parent.getCategoryId())) {
            throw new IllegalArgumentException(
                    "Category '" + childSlug + "' is not a subcategory of '" + parentSlug + "'");
        }

        return child;
    }

    /**
     * Validate that slug is unique
     */
    public void validateSlugUnique(String slug) {
        if (categoryRepository.existsBySlug(slug)) {

            throw new IllegalArgumentException("Category with slug '" + slug + "' already exists");
        }
    }

    /**
     * Validate that category can be deleted
     * A category can only be deleted if it has no subcategories and no listings
     */
    public void validateCategoryDeletable(Category category) {

        if (category.hasChildren()) {
            throw new IllegalStateException(
                    "Cannot delete category '" + category.getSlug() + "' because it has subcategories");
        }

        // Note: You would also need to check if any listings use this category
        // This requires a ListingRepository method like: existsByCategory_Slug(String slug)
    }

    /**
     * Validate category hierarchy depth
     * Ensures that categories don't exceed maximum nesting level
     */
    public void validateCategoryDepth(Category category, int maxDepth) {

        int depth = calculateCategoryDepth(category);

        if (depth > maxDepth) {
            throw new IllegalArgumentException(
                    "Category hierarchy cannot exceed " + maxDepth + " levels");
        }
    }

    // =================================================================
    // Helper Methods
    // =================================================================

    /**
     * Calculate the depth of a category in the hierarchy
     * Root categories have depth 0
     */
    private int calculateCategoryDepth(Category category) {

        int depth = 0;
        Category current = category;

        while (current.getParent() != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }

    /**
     * Get the root category of a given category
     */
    public Category getRootCategory(Category category) {

        Category current = category;

        while (current.getParent() != null) {
            current = current.getParent();
        }

        return current;
    }

    // =================================================================
    // Internal Methods - Use Long ID only for FK relationships
    // =================================================================

    /**
     * Validate category by internal ID (for internal FK operations only)
     * Do not expose this in external APIs
     */
    Category validateCategoryByIdInternal(Long categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with internal ID " + categoryId + " not found"));
    }
}
