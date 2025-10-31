package edu.uic.marketplace.dto.response.listing;

import edu.uic.marketplace.model.listing.ItemCondition;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingSummaryResponse {

    private Long listingId;
    private String title;
    private BigDecimal price;
    private ItemCondition condition;
    private ListingStatus status;
    private String thumbnailUrl;
    private Integer viewCount;
    private Integer favoriteCount;
    private Boolean isFavorite;
    private Instant createdAt;

    public static ListingSummaryResponse from(Listing listing) {
        return from(listing, false);
    }

    public static ListingSummaryResponse from(Listing listing, boolean isFavorite) {
        String thumbnailUrl = listing.getImages().isEmpty()
                ? null
                : listing.getImages().get(0).getImageUrl();

        return ListingSummaryResponse.builder()
                .listingId(listing.getListingId())
                .title(listing.getTitle())
                .price(listing.getPrice())
                .condition(listing.getCondition())
                .status(listing.getStatus())
                .thumbnailUrl(thumbnailUrl)
                .viewCount(listing.getViewCount())
                .favoriteCount(listing.getFavoriteCount())
                .isFavorite(isFavorite)
                .createdAt(listing.getCreatedAt())
                .build();
    }
}