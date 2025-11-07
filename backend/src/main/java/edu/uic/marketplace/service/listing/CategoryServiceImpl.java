package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryValidator categoryValidator;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        // Load all and filter roots to avoid duplicate children at top level
        List<Category> allWithChildren = categoryRepository.findAllWithChildren();

        return allWithChildren.stream()
                .filter(Category::isRootCategory)
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .map(CategoryResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Category findBySlug(String categorySlug) {
        return categoryValidator.validateCategoryBySlug(categorySlug);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getTopLevelCategories() {

        List<Category> roots = categoryRepository.findByParentIsNull();

        return roots.stream()
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .map(CategoryResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(String parentSlug) {

        categoryValidator.validateCategoryBySlug(parentSlug);

        List<Category> subcategories = categoryRepository.findByParent_Slug(parentSlug);

        return subcategories.stream()
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .map(CategoryResponse::from)
                .toList();

    }

    /**
     * Create new category (Admin only) - deferred (not enforced in local profile).
     */
    @Override
    public CategoryResponse createCategory(String username, String name, Long parentId) {
        // TODO: future feature
        return null;
    }

    @Override
    public CategoryResponse updateCategory(String publicCategoryId, String name) {
        // TODO: future feature
        return null;
    }

    @Override
    public void deleteCategory(String publicCategoryId) {
        // TODO: future feature
    }

    @Override
    public boolean existsById(String publicCategoryId) {
        // TODO: future feature
        return false;
    }
}
