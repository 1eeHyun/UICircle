package edu.uic.marketplace.common.util;

import edu.uic.marketplace.dto.response.common.PageResponse;
import org.springframework.data.domain.Page;

public class PageMapper {

    private PageMapper() {} // Singleton

    public static <T, R> PageResponse<R> toPageResponse(Page<T> page, java.util.List<R> content) {

        return PageResponse.<R>builder()
                .content(content)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPage(page.getNumber())
                .size(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
