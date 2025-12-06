package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.OfferStatus;
import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.transaction.PriceOffer;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.transaction.PriceOfferRepository;
import edu.uic.marketplace.repository.transaction.TransactionRepository;
import edu.uic.marketplace.service.message.ConversationService;
import edu.uic.marketplace.service.message.MessageService;
import edu.uic.marketplace.service.notification.NotificationService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import edu.uic.marketplace.validator.transaction.PriceOfferValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceOfferServiceImpl implements PriceOfferService {

    private final AuthValidator authValidator;
    private final ListingValidator listingValidator;
    private final PriceOfferValidator priceOfferValidator;

    private final PriceOfferRepository priceOfferRepository;

    private final NotificationService notificationService;

    private final ConversationService conversationService;
    private final MessageService messageService;

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;


    // ==================== create offer ====================

    @Override
    @Transactional
    public PriceOfferResponse createOffer(String listingPublicId, String buyerUsername, CreateOfferRequest request) {

        // 1) Validate buyer and listing
        User buyer = authValidator.validateUserByUsername(buyerUsername);
        Listing listing = listingValidator.validateListingByPublicId(listingPublicId);

        // 2) Validate price
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Offer amount must be greater than zero.");
        }

        if (listing.getPrice() != null && request.getAmount().compareTo(listing.getPrice()) > 0) {
            throw new IllegalArgumentException("Offer amount cannot exceed listing price.");
        }

        // 3) Prevent buyer from offering on their own listing
        if (listing.getSeller().getUserId().equals(buyer.getUserId())) {
            throw new IllegalStateException("You cannot make an offer on your own listing.");
        }

        // 4) Check if buyer already has a pending offer for this listing
        boolean pendingExists = priceOfferRepository
                .existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                        buyerUsername,
                        listingPublicId,
                        OfferStatus.PENDING
                );

        if (pendingExists) {
            throw new IllegalStateException("You already have a pending offer for this listing.");
        }

        // 5) Create offer
        PriceOffer newOffer = PriceOffer.builder()
                .listing(listing)
                .buyer(buyer)
                .amount(request.getAmount())
                .message(request.getMessage())
                .status(OfferStatus.PENDING)
                .build();

        PriceOffer saved = priceOfferRepository.save(newOffer);

        // 6) Send notification to seller
        notificationService.notifyNewOffer(
                listing.getSeller().getUsername(),
                buyer.getUsername(),
                listing.getPublicId()
        );

        // 7) Return response
        return PriceOfferResponse.from(saved);
    }

    // ==================== accept offer (seller) ====================

    @Override
    @Transactional
    public PriceOfferResponse acceptOffer(String offerPublicId, String sellerUsername, UpdateOfferStatusRequest request) {

        // 1) Validate offer exists
        PriceOffer offer = priceOfferValidator.validatePriceOfferByPublicId(offerPublicId);

        // 2) Validate seller
        User seller = authValidator.validateUserByUsername(sellerUsername);

        // 3) Ensure seller is the owner of the listing
        priceOfferValidator.validateOfferRightSeller(offer.getListing().getSeller(), seller);

        // 4) Validate requested status == ACCEPTED
        if (request.getStatus() != OfferStatus.ACCEPTED) {
            throw new IllegalArgumentException("Invalid status. Use rejectOffer() for rejection.");
        }

        // 5) Only PENDING offers can be accepted
        if (!offer.isPending()) {
            throw new IllegalStateException("Only pending offers can be accepted.");
        }

        // 6) Accept the offer
        offer.accept();  // sets status = ACCEPTED
        offer.setMessage(request.getNote()); // seller note

        Listing listing = offer.getListing();
        User buyer = offer.getBuyer();

        // 7) Auto-reject all other pending offers
        autoRejectOtherOffers(listing.getPublicId(), offer.getPublicId());

        // 8) Send notification to buyer
        notificationService.notifyOfferStatusChange(
                buyer.getUsername(),              // buyerUsername
                listing.getPublicId(),            // listingPublicId
                "ACCEPTED"                        // status
        );

        // 9) Create conversation for this accepted offer
        Conversation conversation = conversationService.createConversationForOffer(listing, buyer);

        // 10) Create a pending transaction for this accepted offer
        //    - status: PENDING (default in Transaction entity)
        //    - buyerConfirmed / sellerConfirmed: false (default)
        transactionService.createTransaction(
                listing.getPublicId(),
                buyer.getUsername(),
                offer.getAmount()
        );

        // 11) Create first message in conversation (seller -> buyer)
        String initialMessageBody = buildAcceptedOfferMessage(offer, request);

        SendMessageRequest messageRequest = new SendMessageRequest();
        messageRequest.setBody(initialMessageBody);

        messageService.sendMessage(
                conversation.getPublicId(),
                seller.getUsername(),
                messageRequest
        );

        // 12) Return response
        return PriceOfferResponse.from(offer);

    }

    // ==================== reject offer (seller) ====================

    @Override
    @Transactional
    public PriceOfferResponse rejectOffer(String offerPublicId, String sellerUsername, UpdateOfferStatusRequest request) {

        // 1) Validate offer
        PriceOffer offer = priceOfferValidator.validatePriceOfferByPublicId(offerPublicId);

        // 2) Validate seller
        User seller = authValidator.validateUserByUsername(sellerUsername);

        // 3) Ensure seller owns the listing
        priceOfferValidator.validateOfferRightSeller(offer.getListing().getSeller(), seller);

        // 4) Validate requested status == REJECTED
        if (request.getStatus() != OfferStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid status. Use acceptOffer() for acceptance.");
        }

        // 5) Only pending offers can be rejected
        if (!offer.isPending()) {
            throw new IllegalStateException("Only pending offers can be rejected.");
        }

        // 6) Reject offer
        offer.reject();  // status = REJECTED
        offer.setMessage(request.getNote());
        Listing listing = offer.getListing();

        // 7) Send notification to buyer
        notificationService.notifyOfferStatusChange(
                offer.getBuyer().getUsername(),   // buyerUsername
                listing.getPublicId(),            // listingPublicId
                "REJECTED"                        // status
        );

        return PriceOfferResponse.from(offer);
    }

    // ==================== cancel offer (buyer) ====================

    @Override
    @Transactional
    public void cancelOffer(String offerPublicId, String buyerUsername) {

        // 1) Validate offer
        PriceOffer offer = priceOfferValidator.validatePriceOfferByPublicId(offerPublicId);

        // 2) Validate buyer
        User buyer = authValidator.validateUserByUsername(buyerUsername);

        // 3) Only the buyer who created the offer can cancel it
        if (!offer.getBuyer().getUserId().equals(buyer.getUserId())) {
            throw new SecurityException("Not your offer.");
        }

        // 4) Only pending offers can be canceled
        if (!offer.isPending()) {
            throw new IllegalStateException("Only pending offers can be canceled.");
        }

        // 5) Mark as rejected / canceled
        offer.reject(); // still using REJECTED for canceled offers

        String originalMsg = offer.getMessage() == null ? "" : offer.getMessage();
        offer.setMessage("(canceled by buyer) " + originalMsg);

        // 6) Notification
        notificationService.notifyOfferCanceled(
                offer.getListing().getSeller().getUsername(),  // seller
                buyer.getUsername(),                           // buyer
                offer.getListing().getPublicId()               // listingPublicId
        );
    }

    // ==================== listing offers for seller ====================

    @Override
    @Transactional(readOnly = true)
    public List<PriceOfferResponse> getOffersForListing(String listingPublicId, String sellerUsername) {

        // 1) Validate listing
        Listing listing = listingValidator.validateListingByPublicId(listingPublicId);

        // 2) Validate seller
        User seller = authValidator.validateUserByUsername(sellerUsername);

        // 3) Ensure seller owns the listing
        priceOfferValidator.validateOfferRightSeller(listing.getSeller(), seller);

        // 4) Fetch offers
        return priceOfferRepository.findByListing_PublicId(listingPublicId).stream()
                .map(PriceOfferResponse::from)
                .toList();
    }

    // ==================== buyer's sent offers ====================

    @Override
    @Transactional(readOnly = true)
    public List<PriceOfferResponse> getUserSentOffers(String buyerUsername) {

        authValidator.validateUserByUsername(buyerUsername);

        return priceOfferRepository.findByBuyer_Username(buyerUsername).stream()
                .map(PriceOfferResponse::from)
                .toList();
    }

    // ==================== seller's received offers ====================

    @Override
    @Transactional(readOnly = true)
    public List<PriceOfferResponse> getUserReceivedOffers(String sellerUsername) {

        authValidator.validateUserByUsername(sellerUsername);

        return priceOfferRepository.findByListing_Seller_Username(sellerUsername).stream()
                .map(PriceOfferResponse::from)
                .toList();
    }

    // ==================== single offer detail ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceOfferResponse> getOffer(String offerPublicId, String username) {

        User user = authValidator.validateUserByUsername(username);

        Optional<PriceOffer> opt = priceOfferRepository.findByPublicId(offerPublicId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        PriceOffer offer = opt.get();

        boolean isBuyer = offer.getBuyer().getUserId().equals(user.getUserId());
        boolean isSeller = offer.getListing().getSeller().getUserId().equals(user.getUserId());

        if (!isBuyer && !isSeller) {
            throw new SecurityException("You are not allowed to view this offer.");
        }

        return Optional.of(PriceOfferResponse.from(offer));
    }

    // ==================== helper: pending check ====================

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingOffer(String buyerUsername, String listingPublicId) {
        return priceOfferRepository.existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                buyerUsername,
                listingPublicId,
                OfferStatus.PENDING
        );
    }

    // ==================== latest accepted offer for listing ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceOfferResponse> getAcceptedOffer(String listingPublicId) {

//        List<PriceOffer> acceptedOffers =
//                priceOfferRepository.findByListing_PublicIdAndStatus(
//                        listingPublicId,
//                        OfferStatus.ACCEPTED
//                );
//
//        List<String> listingIds = acceptedOffers.stream()
//                .map(o -> o.getListing().getPublicId())
//                .distinct()
//                .toList();
//
//        List<Transaction> transactions = transactionRepository.findByListing_PublicIdIn(listingIds);
//        Map<String, String> listingToTxPublicId = transactions.stream()
//                .collect(Collectors.toMap(
//                        t -> t.getListing().getPublicId(),
//                        Transaction::getPublicId,
//                        (a, b) -> a
//                ));
//
//        return acceptedOffers.stream()
//                .map(offer -> {
//                    String txPublicId = listingToTxPublicId.get(offer.getListing().getPublicId());
//                    return PriceOfferResponse.from(offer, txPublicId);
//                })
//                .toList();

        return null;
    }

    // ==================== auto reject other pending offers ====================

    @Override
    @Transactional
    public void autoRejectOtherOffers(String listingPublicId, String acceptedOfferPublicId) {

        // 1) Find all pending offers for this listing
        List<PriceOffer> pendingOffers = priceOfferRepository
                .findByListing_PublicIdAndStatus(listingPublicId, OfferStatus.PENDING);

        // 2) Reject all pending offers except the one that was just accepted
        for (PriceOffer offer : pendingOffers) {
            if (!offer.getPublicId().equals(acceptedOfferPublicId)) {
                offer.reject(); // helper: status = REJECTED
            }
        }
        // @Transactional + dirty checking → auto flush
    }

    // ==================== helpers ====================

    private String buildAcceptedOfferMessage(PriceOffer offer, UpdateOfferStatusRequest request) {

        String note = request.getNote();
        if (note != null && !note.isBlank()) {
            return note;
        }

        String title = offer.getListing().getTitle();
        String amountStr = offer.getAmount() != null
                ? offer.getAmount().toPlainString()
                : "";

        return "Your offer of $" + amountStr + " for \"" + title + "\" has been accepted.";
    }
}
