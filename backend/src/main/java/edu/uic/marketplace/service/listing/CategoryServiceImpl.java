package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final AuthValidator authValidator;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        List<Category> all = categoryRepository.findAll();

        return all.stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Override
    public Category findById(Long categoryId) {

        Optional<Category> found = categoryRepository.findById(categoryId);
        return found.orElse(null);
    }

    @Override
    public List<CategoryResponse> getTopLevelCategories() {
        return null;
    }

    @Override
    public List<CategoryResponse> getSubcategories(Long parentId) {
        return null;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(Long userId, String name, Long parentId) {

        // 1) authorize
        authValidator.validateAdminById(userId);

        // 2) validate input
        String trimmed = (name == null) ? "" : name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Category name must not be blank.");
        }
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Category name must be <= 100 characters.");
        }

        // 3) optional parent
        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + parentId));
        }

//        // 4) uniqueness (parent_id + name)
//        if (categoryRepository.existsByParentAndNameIgnoreCase(parent, trimmed)) {
//            throw new IllegalStateException("Category with the same name already exists under the given parent.");
//        }

        // 5) persist
        Category toSave = Category.builder()
                .name(trimmed)
                .parent(parent)
                .build();

        if (parent != null) {
            parent.getChildren().add(toSave);
        }

        Category saved = categoryRepository.save(toSave);

        // 6) map to DTO
        return CategoryResponse.from(saved);
    }


    @Override
    public CategoryResponse updateCategory(Long categoryId, String name) {
        return null;
    }

    @Override
    public void deleteCategory(Long categoryId) {

    }

    @Override
    public boolean existsById(Long categoryId) {
        return false;
    }
}
