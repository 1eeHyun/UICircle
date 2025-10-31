package edu.uic.marketplace.dto.response.listing;

import edu.uic.marketplace.model.listing.ListingImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingImageResponse {

    private Long imageId;
    private String imageUrl;
    private Integer displayOrder;

    public static ListingImageResponse from(ListingImage image) {
        return ListingImageResponse.builder()
                .imageId(image.getImageId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .build();
    }
}