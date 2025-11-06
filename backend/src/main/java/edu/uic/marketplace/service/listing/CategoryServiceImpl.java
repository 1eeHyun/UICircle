package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final AuthValidator authValidator;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        // Load all and filter roots to avoid duplicate children at top level
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .filter(Category::isRootCategory)
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .map(CategoryResponse::from) // recursive mapping
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long categoryId) {

        if (categoryId == null) throw new IllegalArgumentException("categoryId must not be null");

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getTopLevelCategories() {

        return categoryRepository.findByParentIsNullOrderByNameAsc().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(Long parentId) {

        if (parentId == null) throw new IllegalArgumentException("parentId must not be null");

        // Ensure parent exists (clear error message)
        categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + parentId));

        return categoryRepository.findByParent_CategoryIdOrderByNameAsc(parentId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    /**
     * Create new category (Admin only) - deferred (not enforced in local profile).
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(Long userId, String name, Long parentId) {
        // TODO: implement after auth/role enforcement is enabled
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, String name) {
        // TODO: implement after auth/role enforcement is enabled
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // TODO: implement after auth/role enforcement is enabled
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long categoryId) {
        return categoryId != null && categoryRepository.existsById(categoryId);
    }
}
