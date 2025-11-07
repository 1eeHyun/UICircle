package edu.uic.marketplace.validator.listing;

import edu.uic.marketplace.exception.listing.CategoryNotFoundException;
import edu.uic.marketplace.exception.listing.CategoryNotSubCategoryException;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryValidatorTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryValidator validator;

    // ---------- helpers ----------
    private Category make(String name, String slug) {
        Category c = Category.builder().name(name).slug(slug).build();
        if (c.getChildren() == null) c.setChildren(new ArrayList<>());
        return c;
    }

    private Category linkParent(Category child, Category parent) {
        child.setParent(parent);
        if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
        parent.getChildren().add(child);
        return child;
    }

    // ---------- tests ----------

    @Test
    @DisplayName("validateCategoryBySlug: returns category when found")
    void validateCategoryBySlug_ok() {
        Category books = make("Books", "books");
        given(categoryRepository.findBySlug("books")).willReturn(Optional.of(books));

        Category found = validator.validateCategoryBySlug("books");
        assertThat(found.getSlug()).isEqualTo("books");
    }

    @Test
    @DisplayName("validateCategoryBySlug: throws CategoryNotFoundException when not found")
    void validateCategoryBySlug_notFound() {
        given(categoryRepository.findBySlug("nope")).willReturn(Optional.empty());

        assertThatThrownBy(() -> validator.validateCategoryBySlug("nope"))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("validateLeafCategory: throws when category has children; passes when leaf")
    void validateLeafCategory_behavior() {
        Category root = make("Books", "books");
        Category child = make("Engineering", "eng");
        linkParent(child, root);

        // non-leaf
        given(categoryRepository.findBySlug("books")).willReturn(Optional.of(root));
        assertThatThrownBy(() -> validator.validateLeafCategory("books"))
                .isInstanceOf(CategoryNotSubCategoryException.class);

        // leaf
        given(categoryRepository.findBySlug("eng")).willReturn(Optional.of(child));
        assertThat(validator.validateLeafCategory("eng").getSlug()).isEqualTo("eng");
    }

    @Test
    @DisplayName("validateRootCategory: passes for root; throws for non-root")
    void validateRootCategory_behavior() {
        Category root = make("Books", "books");
        Category child = make("Engineering", "eng");
        linkParent(child, root);

        given(categoryRepository.findBySlug("books")).willReturn(Optional.of(root));
        assertThat(validator.validateRootCategory("books").getSlug()).isEqualTo("books");

        given(categoryRepository.findBySlug("eng")).willReturn(Optional.of(child));
        assertThatThrownBy(() -> validator.validateRootCategory("eng"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a root");
    }

    @Test
    @DisplayName("validateSubCategory: passes for non-root; throws for root")
    void validateSubCategory_behavior() {
        Category root = make("Books", "books");
        Category child = make("Engineering", "eng");
        linkParent(child, root);

        given(categoryRepository.findBySlug("books")).willReturn(Optional.of(root));
        assertThatThrownBy(() -> validator.validateSubCategory("books"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("root category");

        given(categoryRepository.findBySlug("eng")).willReturn(Optional.of(child));
        assertThat(validator.validateSubCategory("eng").getSlug()).isEqualTo("eng");
    }

    @Test
    @DisplayName("validateSubCategoryOfParent: passes when child belongs to parent; throws otherwise")
    void validateSubCategoryOfParent_behavior() {
        Category root = make("Books", "books");
        root.setCategoryId(1L);
        Category eng = make("Engineering", "eng");
        eng.setCategoryId(2L);
        linkParent(eng, root);

        Category otherRoot = make("Electronics", "elec");
        otherRoot.setCategoryId(3L);

        given(categoryRepository.findBySlug("books")).willReturn(Optional.of(root));
        given(categoryRepository.findBySlug("eng")).willReturn(Optional.of(eng));
        given(categoryRepository.findBySlug("elec")).willReturn(Optional.of(otherRoot));

        // ok
        assertThat(validator.validateSubCategoryOfParent("books", "eng").getSlug()).isEqualTo("eng");

        // wrong parent
        assertThatThrownBy(() -> validator.validateSubCategoryOfParent("elec", "eng"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a subcategory of");
    }

    @Test
    @DisplayName("validateSlugUnique: throws when slug exists; no-op when not exists")
    void validateSlugUnique_behavior() {
        given(categoryRepository.existsBySlug("books")).willReturn(true);
        assertThatThrownBy(() -> validator.validateSlugUnique("books"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        given(categoryRepository.existsBySlug("new-slug")).willReturn(false);
        validator.validateSlugUnique("new-slug"); // should not throw
    }

    @Test
    @DisplayName("validateCategoryDeletable: throws when category has children; no-op for leaf")
    void validateCategoryDeletable_behavior() {
        Category root = make("Books", "books");
        Category ch = make("Engineering", "eng");
        linkParent(ch, root); // now root has children

        assertThatThrownBy(() -> validator.validateCategoryDeletable(root))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("has subcategories");

        Category leaf = make("Leaf", "leaf");
        validator.validateCategoryDeletable(leaf); // should not throw
    }

    @Test
    @DisplayName("validateCategoryDepth: throws when depth exceeds max; no-op otherwise")
    void validateCategoryDepth_behavior() {
        Category root = make("Root", "root");
        root.setCategoryId(1L);
        Category lvl1 = make("L1", "l1");
        linkParent(lvl1, root);
        Category lvl2 = make("L2", "l2");
        linkParent(lvl2, lvl1);

        // depth(root)=0, depth(l1)=1, depth(l2)=2
        validator.validateCategoryDepth(root, 2); // ok
        validator.validateCategoryDepth(lvl1, 2); // ok

        assertThatThrownBy(() -> validator.validateCategoryDepth(lvl2, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot exceed");
    }

    @Test
    @DisplayName("getRootCategory: returns top-most ancestor")
    void getRootCategory_ok() {
        Category root = make("Root", "root");
        Category lvl1 = make("L1", "l1");
        Category lvl2 = make("L2", "l2");
        linkParent(lvl1, root);
        linkParent(lvl2, lvl1);

        assertThat(validator.getRootCategory(lvl2).getSlug()).isEqualTo("root");
    }

    @Test
    @DisplayName("validateCategoryByIdInternal: returns entity when found; throws when not found")
    void validateCategoryByIdInternal_behavior() {
        Category cat = make("Books", "books");
        cat.setCategoryId(10L);

        given(categoryRepository.findById(10L)).willReturn(Optional.of(cat));
        assertThat(validator.validateCategoryByIdInternal(10L).getCategoryId()).isEqualTo(10L);

        given(categoryRepository.findById(404L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> validator.validateCategoryByIdInternal(404L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("404");
    }
}
