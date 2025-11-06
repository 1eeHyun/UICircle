package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class CategoryServiceTest {

    private AuthValidator authValidator;
    private CategoryRepository categoryRepository;
    private CategoryServiceImpl service;

    @BeforeEach
    void setUp() {
        authValidator = mock(AuthValidator.class);
        categoryRepository = mock(CategoryRepository.class);
        service = new CategoryServiceImpl(authValidator, categoryRepository);
    }

    private Category make(String name) {
        return Category.builder().name(name).build();
    }

    private Category makeWithChildren(String name, Category... children) {
        Category p = make(name);
        for (Category c : children) {
            c.setParent(p);
            p.getChildren().add(c);
        }
        return p;
    }

    @Test
    @DisplayName("getAllCategories: only root categories are top-level, children included recursively")
    void getAllCategories_returnsRootsWithChildren() {
        Category eng = make("Engineering");
        Category books = makeWithChildren("Books", eng);
        Category laptops = make("Laptops / Tablets");
        Category tech = makeWithChildren("Electronics & Tech", laptops);

        given(categoryRepository.findAllByOrderByNameAsc())
                .willReturn(List.of(books, eng, tech, laptops));

        List<CategoryResponse> tree = service.getAllCategories();

        // Only 2 roots are top-level
        assertThat(tree).hasSize(2);
        assertThat(tree).extracting(CategoryResponse::getName)
                .containsExactlyInAnyOrder("Books", "Electronics & Tech");

        // Verify children
        CategoryResponse booksResp = tree.stream()
                .filter(r -> r.getName().equals("Books"))
                .findFirst().orElseThrow();
        assertThat(booksResp.getChildren()).extracting(CategoryResponse::getName)
                .containsExactly("Engineering");

        CategoryResponse techResp = tree.stream()
                .filter(r -> r.getName().equals("Electronics & Tech"))
                .findFirst().orElseThrow();
        assertThat(techResp.getChildren()).extracting(CategoryResponse::getName)
                .containsExactly("Laptops / Tablets");
    }

    @Test
    @DisplayName("findById: returns entity when present")
    void findById_found() {
        Category cat = make("Books");
        cat.setCategoryId(1L);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(cat));

        Category found = service.findById(1L);
        assertThat(found.getName()).isEqualTo("Books");
    }

    @Test
    @DisplayName("findById: throws IllegalArgumentException when not found")
    void findById_notFound() {
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    @DisplayName("getTopLevelCategories: returns only categories with parent=null (recursively includes children)")
    void getTopLevelCategories_ok() {
        Category child = make("Engineering");
        Category root = makeWithChildren("Books", child);

        given(categoryRepository.findByParentIsNullOrderByNameAsc()).willReturn(List.of(root));

        List<CategoryResponse> roots = service.getTopLevelCategories();
        assertThat(roots).hasSize(1);
        assertThat(roots.get(0).getName()).isEqualTo("Books");
        assertThat(roots.get(0).getChildren()).extracting(CategoryResponse::getName)
                .containsExactly("Engineering");
    }

    @Nested
    class Subcategories {
        @Test
        @DisplayName("getSubcategories: returns direct children (each including recursive children)")
        void subcategories_ok() {
            Category parent = make("Books");
            parent.setCategoryId(10L);

            Category c1 = make("Engineering");
            Category c2 = make("Business & Economics");

            given(categoryRepository.findById(10L)).willReturn(Optional.of(parent));

            given(categoryRepository.findByParent_CategoryIdOrderByNameAsc(10L))
                    .willReturn(List.of(c1, c2));

            List<CategoryResponse> subs = service.getSubcategories(10L);

            assertThat(subs).extracting(CategoryResponse::getName)
                    .containsExactlyInAnyOrder("Engineering", "Business & Economics");

            then(categoryRepository).should().findById(10L);
            then(categoryRepository).should().findByParent_CategoryIdOrderByNameAsc(10L);
        }

        @Test
        @DisplayName("getSubcategories: throws IllegalArgumentException when parent not found")
        void subcategories_parentNotFound() {
            given(categoryRepository.findById(404L)).willReturn(Optional.empty());
            assertThatThrownBy(() -> service.getSubcategories(404L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Parent category not found");
        }
    }

    @Test
    @DisplayName("existsById: returns true/false correctly")
    void existsById_ok() {
        given(categoryRepository.existsById(1L)).willReturn(true);
        given(categoryRepository.existsById(2L)).willReturn(false);

        assertThat(service.existsById(1L)).isTrue();
        assertThat(service.existsById(2L)).isFalse();
        assertThat(service.existsById(null)).isFalse();
    }
}
