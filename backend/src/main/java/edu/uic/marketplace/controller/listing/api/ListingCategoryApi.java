package edu.uic.marketplace.controller.listing.api;

import edu.uic.marketplace.controller.listing.docs.ListingCategoryDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.listing.CategoryResponse;
import edu.uic.marketplace.service.listing.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ListingCategoryApi implements ListingCategoryDocs {

    private final CategoryService categoryService;

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getAllCategories() {

        List<CategoryResponse> res = categoryService.getAllCategories();
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/parent")
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getTopLevelCategories() {


        List<CategoryResponse> res = categoryService.getTopLevelCategories();
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getSubcategories(
            @PathVariable String parentSlug
    ) {

        List<CategoryResponse> res = categoryService.getSubcategories(parentSlug);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(
            String name,
            String parentSlug) {
        // TODO: future feature after launching the app
        return null;
    }
}
