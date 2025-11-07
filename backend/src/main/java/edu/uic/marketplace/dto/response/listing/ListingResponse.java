package edu.uic.marketplace.dto.response.listing;

import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.listing.ItemCondition;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingResponse {

    private String publicId;
    private String title;
    private String description;
    private BigDecimal price;
    private ItemCondition condition;
    private ListingStatus status;
    private UserResponse seller;
    private CategoryResponse category;
    private Double latitude;
    private Double longitude;
    private Boolean isNegotiable;
    private Integer viewCount;
    private Integer favoriteCount;
    private Boolean isFavorited;
    private List<ListingImageResponse> images;
    private Instant createdAt;
    private Instant updatedAt;

    public static ListingResponse from(Listing listing) {
        return from(listing, false);
    }

    public static ListingResponse from(Listing listing, boolean isFavorited) {
        return ListingResponse.builder()
                .publicId(listing.getPublicId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .condition(listing.getCondition())
                .status(listing.getStatus())
                .seller(UserResponse.from(listing.getSeller()))
                .category(CategoryResponse.from(listing.getCategory()))
                .latitude(listing.getLatitude())
                .longitude(listing.getLongitude())
                .isNegotiable(listing.getIsNegotiable())
                .viewCount(listing.getViewCount())
                .favoriteCount(listing.getFavoriteCount())
                .isFavorited(isFavorited)
                .images(listing.getImages().stream()
                        .map(ListingImageResponse::from)
                        .collect(Collectors.toList()))
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .build();
    }
}