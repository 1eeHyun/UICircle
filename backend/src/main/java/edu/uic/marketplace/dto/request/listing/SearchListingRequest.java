package edu.uic.marketplace.dto.request.listing;

import edu.uic.marketplace.model.listing.ItemCondition;
import edu.uic.marketplace.model.listing.ListingStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchListingRequest {

    private String keyword;
    private String categorySlug;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private ItemCondition condition;

    @Builder.Default
    private ListingStatus status = ListingStatus.ACTIVE;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortOrder = "desc";

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 20;
}