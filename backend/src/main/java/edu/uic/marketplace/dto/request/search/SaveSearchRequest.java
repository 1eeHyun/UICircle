package edu.uic.marketplace.dto.request.search;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveSearchRequest {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Query must be less than 500 characters")
    private String query;

    private String filters;  // JSON string of filters
}
