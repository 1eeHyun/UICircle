package edu.uic.marketplace.validator.listing;

import edu.uic.marketplace.exception.listing.CategoryNotFoundException;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public Category validateCategoryExists(Long categoryId) {
        Optional<Category> found = categoryRepository.findById(categoryId);

        if (!found.isPresent())
            throw new CategoryNotFoundException();

        return found.get();
    }
}
