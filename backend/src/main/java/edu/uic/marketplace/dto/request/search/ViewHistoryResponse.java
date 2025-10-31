package edu.uic.marketplace.dto.response.search;

import edu.uic.marketplace.dto.response.listing.ListingSummaryResponse;
import edu.uic.marketplace.model.search.ViewHistory;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewHistoryResponse {

    private Long userId;
    private ListingSummaryResponse listing;
    private Instant viewedAt;

    public static ViewHistoryResponse from(ViewHistory viewHistory) {
        return ViewHistoryResponse.builder()
                .userId(viewHistory.getUser().getUserId())
                .listing(ListingSummaryResponse.from(viewHistory.getListing()))
                .viewedAt(viewHistory.getViewedAt())
                .build();
    }
}