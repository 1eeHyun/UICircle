package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock private CategoryValidator categoryValidator;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl service;

    // ---------- helpers ----------
    private Category make(String name, String slug) {
        Category c = Category.builder().name(name).slug(slug).build();
        if (c.getChildren() == null) c.setChildren(new ArrayList<>());
        return c;
    }

    private Category makeWithChildren(String name, String slug, Category... children) {
        Category p = make(name, slug);
        for (Category ch : children) {
            if (ch.getChildren() == null) ch.setChildren(new ArrayList<>());
            ch.setParent(p);
            p.getChildren().add(ch);
        }
        return p;
    }

    // ---------- tests ----------

    @Test
    @DisplayName("getAllCategories: returns only root categories (with children) sorted case-insensitively")
    void getAllCategories_rootsOnly_sorted_caseInsensitive() {
        Category eng = make("engineering", "engineering");       // child
        Category books = makeWithChildren("Books", "books", eng); // root
        Category laptops = make("Laptops / Tablets", "laptops");  // child
        Category tech = makeWithChildren("electronics & tech", "electronics", laptops); // root

        given(categoryRepository.findAllWithChildren())
                .willReturn(List.of(books, eng, tech, laptops));

        List<CategoryResponse> result = service.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CategoryResponse::getName)
                .containsExactly("Books", "electronics & tech");

        assertThat(result.get(0).getChildren())
                .extracting(CategoryResponse::getName)
                .containsExactly("engineering");
        assertThat(result.get(1).getChildren())
                .extracting(CategoryResponse::getName)
                .containsExactly("Laptops / Tablets");
    }

    @Test
    @DisplayName("findBySlug: delegates to CategoryValidator and returns entity")
    void findBySlug_ok() {
        Category cat = make("Books", "books");
        given(categoryValidator.validateCategoryBySlug("books")).willReturn(cat);

        Category found = service.findBySlug("books");

        assertThat(found.getSlug()).isEqualTo("books");
        then(categoryValidator).should().validateCategoryBySlug("books");
    }

    @Test
    @DisplayName("getTopLevelCategories: returns parent=null categories sorted case-insensitively")
    void getTopLevelCategories_ok_sorted() {
        Category a = make("appliances", "appliances");
        Category b = make("Books", "books");
        Category c = make("clothes", "clothes");

        given(categoryRepository.findByParentIsNull()).willReturn(List.of(c, a, b));

        List<CategoryResponse> roots = service.getTopLevelCategories();

        assertThat(roots).extracting(CategoryResponse::getName)
                .containsExactly("appliances", "Books", "clothes");
    }

    @Nested
    @DisplayName("getSubcategories")
    class GetSubcategories {

        @Test
        @DisplayName("returns direct children after parent validation, sorted case-insensitively")
        void subcategories_ok_sorted() {
            String parentSlug = "books";
            Category parent = make("Books", parentSlug);
            Category ch1 = make("engineering", "engineering");
            Category ch2 = make("Business & Economics", "biz");

            given(categoryValidator.validateCategoryBySlug(parentSlug)).willReturn(parent);
            given(categoryRepository.findByParent_Slug(parentSlug)).willReturn(List.of(ch1, ch2));

            List<CategoryResponse> subs = service.getSubcategories(parentSlug);

            assertThat(subs).extracting(CategoryResponse::getName)
                    .containsExactly("Business & Economics", "engineering");

            then(categoryValidator).should().validateCategoryBySlug(parentSlug);
            then(categoryRepository).should().findByParent_Slug(parentSlug);
        }

        @Test
        @DisplayName("propagates validator exception when parent slug is invalid")
        void subcategories_parentInvalid_propagates() {
            given(categoryValidator.validateCategoryBySlug(anyString()))
                    .willThrow(new IllegalArgumentException("Category with slug 'x' not found"));

            assertThatThrownBy(() -> service.getSubcategories("x"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Test
    @DisplayName("TODO methods: placeholders (no-op/null/false) until implemented")
    void todos_notImplementedYet() {
        assertThat(service.existsById("any")).isFalse();
        service.deleteCategory("pub-1"); // no-op
        assertThat(service.updateCategory("pub-1", "NewName")).isNull();
        assertThat(service.createCategory("user1", "Name", 1L)).isNull();
    }
}
