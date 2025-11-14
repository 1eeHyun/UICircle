package edu.uic.marketplace.controller.transaction.docs;

import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(
        name = "Price Offers",
        description = "Operations for creating, accepting, rejecting, canceling, and viewing price offers"
)
public interface PriceOfferApiDocs {

    // ========= Create offer (buyer) =========

    @Operation(
            summary = "Create an offer for a listing",
            description = """
                    Create a new price offer for a listing as the currently authenticated buyer.
                    A buyer cannot create an offer on their own listing and cannot have more than one pending offer per listing.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer created successfully",
                    content = @Content(schema = @Schema(implementation = PriceOfferResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or business rule violation (e.g., already has a pending offer)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<PriceOfferResponse>> createOffer(
            @Parameter(
                    description = "Public id of the listing you want to make an offer on",
                    example = "lst_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String listingPublicId,

            @RequestBody(
                    description = "Offer amount and optional message",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateOfferRequest.class))
            )
            CreateOfferRequest request
    );

    // ========= Accept offer (seller) =========

    @Operation(
            summary = "Accept an offer (seller)",
            description = """
                    Accept a pending offer for your listing.
                    When an offer is accepted:
                      - The offer status becomes ACCEPTED
                      - The listing can be marked as 'in progress'
                      - All other pending offers for this listing are automatically rejected.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer accepted successfully",
                    content = @Content(schema = @Schema(implementation = PriceOfferResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status or offer is not pending"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not the seller of this listing"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found"
            )
    })
    ResponseEntity<CommonResponse<PriceOfferResponse>> acceptOffer(
            @Parameter(
                    description = "Public id of the offer to accept",
                    example = "off_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String offerPublicId,

            @RequestBody(
                    description = "Status must be ACCEPTED, optional seller note",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateOfferStatusRequest.class))
            )
            UpdateOfferStatusRequest request
    );

    // ========= Reject offer (seller) =========

    @Operation(
            summary = "Reject an offer (seller)",
            description = "Reject a pending offer for your listing. Listing status is not changed by rejection."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer rejected successfully",
                    content = @Content(schema = @Schema(implementation = PriceOfferResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status or offer is not pending"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not the seller of this listing"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found"
            )
    })
    ResponseEntity<CommonResponse<PriceOfferResponse>> rejectOffer(
            @Parameter(
                    description = "Public id of the offer to reject",
                    example = "off_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String offerPublicId,

            @RequestBody(
                    description = "Status must be REJECTED, optional seller note",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateOfferStatusRequest.class))
            )
            UpdateOfferStatusRequest request
    );

    // ========= Cancel offer (buyer) =========

    @Operation(
            summary = "Cancel an offer (buyer)",
            description = "Cancel your own pending offer. Only the buyer who created the offer can cancel it."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer canceled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Offer is not pending"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not the buyer of this offer"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found"
            )
    })
    ResponseEntity<CommonResponse<Void>> cancelOffer(
            @Parameter(
                    description = "Public id of the offer to cancel",
                    example = "off_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String offerPublicId
    );

    // ========= Listing offers (for seller) =========

    @Operation(
            summary = "Get all offers for a listing (seller)",
            description = "Retrieve all offers for a given listing. Only the seller of the listing can access this data."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of offers for the listing",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PriceOfferResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not the seller of this listing"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Listing not found"
            )
    })
    ResponseEntity<CommonResponse<List<PriceOfferResponse>>> getOffersForListing(
            @Parameter(
                    description = "Public id of the listing",
                    example = "lst_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String listingPublicId
    );

    // ========= My sent offers (buyer) =========

    @Operation(
            summary = "Get offers sent by the current user",
            description = "Retrieve all offers created by the currently authenticated user (as a buyer)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of offers sent by the user",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PriceOfferResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<List<PriceOfferResponse>>> getUserSentOffers();

    // ========= My received offers (seller) =========

    @Operation(
            summary = "Get offers received by the current user",
            description = "Retrieve all offers received by the currently authenticated user as a seller."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of offers received by the user",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PriceOfferResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<List<PriceOfferResponse>>> getUserReceivedOffers();

    // ========= Single offer detail =========

    @Operation(
            summary = "Get offer detail",
            description = "Retrieve a single offer by public id. Only the buyer or the seller involved in the offer can view it."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer detail",
                    content = @Content(schema = @Schema(implementation = PriceOfferResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not buyer or seller of this offer"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found"
            )
    })
    ResponseEntity<CommonResponse<PriceOfferResponse>> getOffer(
            @Parameter(
                    description = "Public id of the offer",
                    example = "off_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String offerPublicId
    );

    // ========= Check pending offer for current buyer =========

    @Operation(
            summary = "Check if current user has a pending offer on a listing",
            description = "Returns true if the currently authenticated user already has a pending offer for the given listing."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pending offer status",
                    content = @Content(schema = @Schema(implementation = Boolean.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<Boolean>> hasPendingOffer(
            @Parameter(
                    description = "Public id of the listing",
                    example = "lst_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String listingPublicId
    );

    // ========= Get accepted offer for listing =========

    @Operation(
            summary = "Get accepted offer for a listing",
            description = """
                    Return the latest accepted offer for the given listing, if any.
                    Useful to show that the listing is currently being purchased by a buyer.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Accepted offer, if exists",
                    content = @Content(schema = @Schema(implementation = PriceOfferResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    ResponseEntity<CommonResponse<PriceOfferResponse>> getAcceptedOffer(
            @Parameter(
                    description = "Public id of the listing",
                    example = "lst_01JDE7AZP2YQW7EJG1Z9B2C3D4"
            )
            @PathVariable String listingPublicId
    );
}
