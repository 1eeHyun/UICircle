package edu.uic.marketplace.dto.response.search;

import edu.uic.marketplace.model.search.SavedSearch;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedSearchResponse {

    private Long savedSearchId;
    private String name;
    private String query;
    private String filters;
    private Instant createdAt;

    public static SavedSearchResponse from(SavedSearch savedSearch) {
        return SavedSearchResponse.builder()
                .savedSearchId(savedSearch.getSavedSearchId())
                .name(savedSearch.getName())
                .query(savedSearch.getQuery())
                .filters(savedSearch.getFilters())
                .createdAt(savedSearch.getCreatedAt())
                .build();
    }
}
