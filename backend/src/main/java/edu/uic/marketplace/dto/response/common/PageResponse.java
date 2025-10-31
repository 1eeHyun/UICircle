package edu.uic.marketplace.dto.response.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int size;
    private boolean first;
    private boolean last;
    private boolean empty;

    /**
     * Get the content list (for compatibility with Spring Data Page)
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * Get total number of elements
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * Get total number of pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Get current page number (0-indexed)
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get page size
     */
    public int getSize() {
        return size;
    }

    /**
     * Check if this is the first page
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * Check if this is the last page
     */
    public boolean isLast() {
        return last;
    }

    /**
     * Check if the content is empty
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Static factory method to create PageResponse
     */
    public static <T> PageResponse<T> of(List<T> content, int currentPage, int totalPages,
                                         long totalElements, int size) {

        return PageResponse.<T>builder()
                .content(content)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .size(size)
                .first(currentPage == 0)
                .last(currentPage == totalPages - 1 || totalPages == 0)
                .empty(content.isEmpty())
                .build();
    }

    /**
     * Convenience method for Spring Data Page conversion
     */
    public static <T> PageResponse<T> fromPage(org.springframework.data.domain.Page<T> page) {
        return of(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}
