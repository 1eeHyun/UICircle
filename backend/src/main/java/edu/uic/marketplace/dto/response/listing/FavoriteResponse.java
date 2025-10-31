package edu.uic.marketplace.dto.response.listing;

import edu.uic.marketplace.model.listing.Favorite;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long userId;
    private Long listingId;
    private ListingSummaryResponse listing;
    private Instant favoritedAt;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .userId(favorite.getUser().getUserId())
                .listingId(favorite.getListing().getListingId())
                .listing(ListingSummaryResponse.from(favorite.getListing()))
                .favoritedAt(favorite.getFavoritedAt())
                .build();
    }
}