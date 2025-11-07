package edu.uic.marketplace.model.listing;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_categories_parent_name",
                        columnNames = {"parent_id", "name"}
                )
        },
        indexes = {
                @Index(name = "idx_categories_parent_id", columnList = "parent_id"),
                @Index(name = "idx_categories_name", columnList = "name"),
                @Index(name="idx_categories_slug", columnList="category_slug")

        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_slug", unique = true, nullable = false, length = 128)
    private String slug;

    /**
     * Category name
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Parent category (null for top-level categories)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_categories_parent"))
    private Category parent;

    /**
     * Subcategories
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (slug == null || slug.isBlank())
            this.slug = generateSlug(this.name);
    }

    /**
     * Helper Methods
     */
    public boolean isRootCategory() {
        return parent == null;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    private String generateSlug(String input) {
        return input
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }
}
