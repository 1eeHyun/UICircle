package edu.uic.marketplace.controller.transaction.api;

import edu.uic.marketplace.controller.transaction.docs.PriceOfferApiDocs;
import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import edu.uic.marketplace.service.transaction.PriceOfferService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PriceOfferController implements PriceOfferApiDocs {

    private final AuthValidator authValidator;
    private final PriceOfferService priceOfferService;

    @Override
    @PostMapping("/listings/{listingPublicId}/offers")
    public ResponseEntity<CommonResponse<PriceOfferResponse>> createOffer(
            @PathVariable(value = "listingPublicId") String listingPublicId,
            @RequestBody CreateOfferRequest request) {

        String buyerUsername = authValidator.extractUsername();
        PriceOfferResponse res = priceOfferService.createOffer(listingPublicId, buyerUsername, request);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PostMapping("/offers/{offerPublicId}/accept")
    public ResponseEntity<CommonResponse<PriceOfferResponse>> acceptOffer(
            @PathVariable(value = "offerPublicId") String offerPublicId,
            @RequestBody UpdateOfferStatusRequest request) {

        String sellerUsername = authValidator.extractUsername();
        PriceOfferResponse res = priceOfferService.acceptOffer(offerPublicId, sellerUsername, request);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PostMapping("/offers/{offerPublicId}/reject")
    public ResponseEntity<CommonResponse<PriceOfferResponse>> rejectOffer(
            @PathVariable(value = "offerPublicId") String offerPublicId,
            @RequestBody UpdateOfferStatusRequest request) {

        String sellerUsername = authValidator.extractUsername();
        PriceOfferResponse res = priceOfferService.rejectOffer(offerPublicId, sellerUsername, request);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PostMapping("/offers/{offerPublicId}/cancel")
    public ResponseEntity<CommonResponse<Void>> cancelOffer(
            @PathVariable(value = "offerPublicId") String offerPublicId) {

        String buyerUsername = authValidator.extractUsername();
        priceOfferService.cancelOffer(offerPublicId, buyerUsername);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/listings/{listingPublicId}/offers")
    public ResponseEntity<CommonResponse<List<PriceOfferResponse>>> getOffersForListing(
            @PathVariable(value = "listingPublicId") String listingPublicId) {

        String sellerUsername = authValidator.extractUsername();
        List<PriceOfferResponse> res = priceOfferService.getOffersForListing(listingPublicId, sellerUsername);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/offers/sent")
    public ResponseEntity<CommonResponse<List<PriceOfferResponse>>> getUserSentOffers() {

        String username = authValidator.extractUsername();
        List<PriceOfferResponse> res = priceOfferService.getUserSentOffers(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/offers/received")
    public ResponseEntity<CommonResponse<List<PriceOfferResponse>>> getUserReceivedOffers() {

        String username = authValidator.extractUsername();
        List<PriceOfferResponse> res = priceOfferService.getUserReceivedOffers(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/offers/{offerPublicId}")
    public ResponseEntity<CommonResponse<PriceOfferResponse>> getOffer(
            @PathVariable(value = "offerPublicId") String offerPublicId) {

        String username = authValidator.extractUsername();
        Optional<PriceOfferResponse> opt = priceOfferService.getOffer(offerPublicId, username);
        PriceOfferResponse res = opt.orElse(null);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/listings/{listingPublicId}/offers/pending")
    public ResponseEntity<CommonResponse<Boolean>> hasPendingOffer(
            @PathVariable(value = "listingPublicId") String listingPublicId) {

        String username = authValidator.extractUsername();
        boolean res = priceOfferService.hasPendingOffer(username, listingPublicId);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/listings/{listingPublicId}/offers/accepted")
    public ResponseEntity<CommonResponse<PriceOfferResponse>> getAcceptedOffer(
            @PathVariable(value = "listingPublicId") String listingPublicId) {

//        String username = authValidator.extractUsername();
        Optional<PriceOfferResponse> opt = priceOfferService.getAcceptedOffer(listingPublicId);
        PriceOfferResponse res = opt.orElse(null);

        return ResponseEntity.ok(CommonResponse.success(res));
    }
}
