package edu.uic.marketplace.dto.response.listing;

import edu.uic.marketplace.model.listing.Category;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long categoryId;
    private String name;
    private Long parentId;

    @Builder.Default
    private List<CategoryResponse> children = new ArrayList<>();

    public static CategoryResponse from(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getCategoryId() : null)
                .children(category.getChildren().stream()
                        .map(CategoryResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}