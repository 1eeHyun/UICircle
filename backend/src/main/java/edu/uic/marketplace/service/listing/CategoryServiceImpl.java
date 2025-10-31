package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.model.listing.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<CategoryResponse> getAllCategories() {
        return null;
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        return Optional.empty();
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
    public CategoryResponse createCategory(String name, Long parentId) {
        return null;
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
