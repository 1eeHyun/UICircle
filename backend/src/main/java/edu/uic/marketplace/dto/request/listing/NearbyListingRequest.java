package edu.uic.marketplace.dto.request.listing;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearbyListingRequest {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotNull(message = "Radius is required")
    @DecimalMin(value = "0.1", message = "Radius must be at least 0.1 miles")
    @DecimalMax(value = "62.1", message = "Radius must not exceed 62.1 miles") // 100km ≈ 62.1mi
    private Double radiusMiles;

    @NotNull(message = "Category is required")
    private String categorySlug;

    @Builder.Default
    private String unit = "mi";  // km or mi

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 20;
}