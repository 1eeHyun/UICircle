package edu.uic.marketplace.service.transaction;

import edu.uic.marketplace.dto.request.transaction.CreateOfferRequest;
import edu.uic.marketplace.dto.request.transaction.UpdateOfferStatusRequest;
import edu.uic.marketplace.dto.response.transaction.PriceOfferResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.OfferStatus;
import edu.uic.marketplace.model.transaction.PriceOffer;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.transaction.PriceOfferRepository;
import edu.uic.marketplace.service.notification.NotificationService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import edu.uic.marketplace.validator.transaction.PriceOfferValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceOfferServiceImplTest {

    @Mock
    private AuthValidator authValidator;

    @Mock
    private ListingValidator listingValidator;

    @Mock
    private PriceOfferValidator priceOfferValidator;

    @Mock
    private PriceOfferRepository priceOfferRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PriceOfferServiceImpl priceOfferService;

    // ============ helper builders ============

    private User createUser(Long id, String username) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        return user;
    }

    private Listing createListing(Long id, String publicId, User seller) {
        Listing listing = new Listing();
        listing.setListingId(id);
        listing.setPublicId(publicId);
        listing.setSeller(seller);
        return listing;
    }

    private PriceOffer createOffer(Long id, String publicId, Listing listing, User buyer, OfferStatus status, BigDecimal amount) {
        PriceOffer offer = new PriceOffer();
        offer.setOfferId(id);
        offer.setPublicId(publicId);
        offer.setListing(listing);
        offer.setBuyer(buyer);
        offer.setStatus(status);
        offer.setAmount(amount);
        offer.setCreatedAt(Instant.now());
        return offer;
    }

    // ============ createOffer ============

    @Nested
    @DisplayName("createOffer")
    class CreateOfferTests {

        @Test
        @DisplayName("creates a new pending offer when buyer and listing are valid and no pending offer exists")
        void createOffer_success() {
            // given
            String listingPublicId = "listing-123";
            String buyerUsername = "buyer";
            User buyer = createUser(2L, buyerUsername);
            User seller = createUser(1L, "seller");
            Listing listing = createListing(10L, listingPublicId, seller);

            CreateOfferRequest request = CreateOfferRequest.builder()
                    .amount(new BigDecimal("50.00"))
                    .message("Can you do 50?")
                    .build();

            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);
            when(priceOfferRepository.existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                    buyerUsername, listingPublicId, OfferStatus.PENDING
            )).thenReturn(false);

            PriceOffer saved = createOffer(100L, "offer-100", listing, buyer, OfferStatus.PENDING, request.getAmount());
            when(priceOfferRepository.save(any(PriceOffer.class))).thenReturn(saved);

            // when
            PriceOfferResponse response = priceOfferService.createOffer(listingPublicId, buyerUsername, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getPublicId()).isEqualTo("offer-100");
            assertThat(response.getAmount()).isEqualByComparingTo("50.00");
            assertThat(response.getStatus()).isEqualTo(OfferStatus.PENDING);

            verify(authValidator).validateUserByUsername(buyerUsername);
            verify(listingValidator).validateListingByPublicId(listingPublicId);
            verify(priceOfferRepository).existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                    buyerUsername, listingPublicId, OfferStatus.PENDING
            );
            verify(priceOfferRepository).save(any(PriceOffer.class));
        }

        @Test
        @DisplayName("throws when buyer tries to make an offer on their own listing")
        void createOffer_buyerIsSeller() {
            // given
            String listingPublicId = "listing-123";
            String buyerUsername = "sameUser";
            User user = createUser(1L, buyerUsername);
            Listing listing = createListing(10L, listingPublicId, user); // seller == buyer

            CreateOfferRequest request = CreateOfferRequest.builder()
                    .amount(new BigDecimal("50.00"))
                    .build();

            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);

            // when / then
            assertThatThrownBy(() ->
                    priceOfferService.createOffer(listingPublicId, buyerUsername, request)
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("You cannot make an offer on your own listing.");

            verify(priceOfferRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws when buyer already has a pending offer for the listing")
        void createOffer_duplicatePending() {
            // given
            String listingPublicId = "listing-123";
            String buyerUsername = "buyer";
            User buyer = createUser(2L, buyerUsername);
            User seller = createUser(1L, "seller");
            Listing listing = createListing(10L, listingPublicId, seller);

            CreateOfferRequest request = CreateOfferRequest.builder()
                    .amount(new BigDecimal("50.00"))
                    .build();

            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);
            when(priceOfferRepository.existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                    buyerUsername, listingPublicId, OfferStatus.PENDING
            )).thenReturn(true);

            // when / then
            assertThatThrownBy(() ->
                    priceOfferService.createOffer(listingPublicId, buyerUsername, request)
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("You already have a pending offer for this listing.");

            verify(priceOfferRepository, never()).save(any());
        }
    }

    // ============ acceptOffer ============

    @Nested
    @DisplayName("acceptOffer")
    class AcceptOfferTests {

        @Test
        @DisplayName("accepts a pending offer, auto-rejects other pending offers")
        void acceptOffer_success() {
            // given
            String offerPublicId = "offer-1";
            String sellerUsername = "seller";
            User seller = createUser(1L, sellerUsername);
            User buyer = createUser(2L, "buyer");
            Listing listing = createListing(10L, "listing-123", seller);

            PriceOffer offer = createOffer(1L, offerPublicId, listing, buyer, OfferStatus.PENDING, new BigDecimal("70.00"));
            UpdateOfferStatusRequest request = UpdateOfferStatusRequest.builder()
                    .status(OfferStatus.ACCEPTED)
                    .note("Deal accepted")
                    .build();

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);

            PriceOffer otherPending1 = createOffer(2L, "offer-2", listing, buyer, OfferStatus.PENDING, new BigDecimal("60.00"));
            PriceOffer otherPending2 = createOffer(3L, "offer-3", listing, buyer, OfferStatus.PENDING, new BigDecimal("55.00"));

            when(priceOfferRepository.findByListing_PublicIdAndStatus("listing-123", OfferStatus.PENDING))
                    .thenReturn(List.of(offer, otherPending1, otherPending2));

            // when
            PriceOfferResponse response = priceOfferService.acceptOffer(offerPublicId, sellerUsername, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo(OfferStatus.ACCEPTED);
            assertThat(offer.getStatus()).isEqualTo(OfferStatus.ACCEPTED);
            assertThat(offer.getMessage()).isEqualTo("Deal accepted");

            assertThat(otherPending1.getStatus()).isEqualTo(OfferStatus.REJECTED);
            assertThat(otherPending2.getStatus()).isEqualTo(OfferStatus.REJECTED);

            // Notification
            verify(notificationService).notifyOfferStatusChange(
                    eq(buyer.getUsername()),
                    eq(listing.getPublicId()),
                    eq("ACCEPTED")
            );
        }

        @Test
        @DisplayName("throws when request status is not ACCEPTED")
        void acceptOffer_invalidStatus() {

            String offerPublicId = "offer-1";
            String sellerUsername = "seller";

            User seller = createUser(1L, sellerUsername);
            Listing listing = createListing(10L, "listing-123", seller);
            PriceOffer offer = createOffer(1L, offerPublicId, listing, createUser(2L, "buyer"),
                    OfferStatus.PENDING, new BigDecimal("70.00"));

            UpdateOfferStatusRequest request = UpdateOfferStatusRequest.builder()
                    .status(OfferStatus.REJECTED) // wrong
                    .note("Wrong")
                    .build();

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);

            assertThatThrownBy(() ->
                    priceOfferService.acceptOffer(offerPublicId, sellerUsername, request)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid status. Use rejectOffer() for rejection.");
        }

        @Test
        @DisplayName("throws when offer is not pending")
        void acceptOffer_notPending() {

            String offerPublicId = "offer-1";
            String sellerUsername = "seller";

            User seller = createUser(1L, sellerUsername);
            Listing listing = createListing(10L, "listing-123", seller);
            PriceOffer offer = createOffer(1L, offerPublicId, listing, createUser(2L, "buyer"),
                    OfferStatus.REJECTED, new BigDecimal("70.00")); // not pending

            UpdateOfferStatusRequest request = UpdateOfferStatusRequest.builder()
                    .status(OfferStatus.ACCEPTED)
                    .build();

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);

            assertThatThrownBy(() ->
                    priceOfferService.acceptOffer(offerPublicId, sellerUsername, request)
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only pending offers can be accepted.");
        }
    }

    // ============ rejectOffer ============

    @Nested
    @DisplayName("rejectOffer")
    class RejectOfferTests {

        @Test
        @DisplayName("rejects a pending offer by seller")
        void rejectOffer_success() {

            String offerPublicId = "offer-1";
            String sellerUsername = "seller";
            User seller = createUser(1L, sellerUsername);
            User buyer = createUser(2L, "buyer");
            Listing listing = createListing(10L, "listing-123", seller);

            PriceOffer offer = createOffer(1L, offerPublicId, listing, buyer,
                    OfferStatus.PENDING, new BigDecimal("70.00"));

            UpdateOfferStatusRequest request = UpdateOfferStatusRequest.builder()
                    .status(OfferStatus.REJECTED)
                    .note("Too low")
                    .build();

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);

            PriceOfferResponse response = priceOfferService.rejectOffer(offerPublicId, sellerUsername, request);

            assertThat(response.getStatus()).isEqualTo(OfferStatus.REJECTED);
            assertThat(offer.getStatus()).isEqualTo(OfferStatus.REJECTED);
            assertThat(offer.getMessage()).isEqualTo("Too low");

            // Notification
            verify(notificationService).notifyOfferStatusChange(
                    eq(buyer.getUsername()),
                    eq(listing.getPublicId()),
                    eq("REJECTED")
            );
        }

        @Test
        @DisplayName("throws when request status is not REJECTED")
        void rejectOffer_invalidStatus() {

            String offerPublicId = "offer-1";
            String sellerUsername = "seller";
            User seller = createUser(1L, sellerUsername);
            Listing listing = createListing(10L, "listing-123", seller);
            PriceOffer offer = createOffer(1L, offerPublicId, listing, createUser(2L, "buyer"),
                    OfferStatus.PENDING, new BigDecimal("70.00"));

            UpdateOfferStatusRequest request = UpdateOfferStatusRequest.builder()
                    .status(OfferStatus.ACCEPTED) // wrong
                    .build();

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);

            assertThatThrownBy(() ->
                    priceOfferService.rejectOffer(offerPublicId, sellerUsername, request)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid status. Use acceptOffer() for acceptance.");
        }

        @Test
        @DisplayName("throws when offer is not pending")
        void rejectOffer_notPending() {

            String offerPublicId = "offer-1";
            String sellerUsername = "seller";
            User seller = createUser(1L, sellerUsername);
            Listing listing = createListing(10L, "listing-123", seller);
            PriceOffer offer = createOffer(1L, offerPublicId, listing, createUser(2L, "buyer"),
                    OfferStatus.ACCEPTED, new BigDecimal("70.00"));

            UpdateOfferStatusRequest request = UpdateOfferStatusRequest.builder()
                    .status(OfferStatus.REJECTED)
                    .build();

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);

            assertThatThrownBy(() ->
                    priceOfferService.rejectOffer(offerPublicId, sellerUsername, request)
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only pending offers can be rejected.");
        }
    }

    // ============ cancelOffer ============

    @Nested
    @DisplayName("cancelOffer")
    class CancelOfferTests {

        @Test
        @DisplayName("cancels pending offer by buyer and notifies seller")
        void cancelOffer_success() {

            String offerPublicId = "offer-1";
            String buyerUsername = "buyer";
            User buyer = createUser(2L, buyerUsername);
            User seller = createUser(1L, "seller");
            Listing listing = createListing(10L, "listing-123", seller);

            PriceOffer offer = createOffer(1L, offerPublicId, listing, buyer,
                    OfferStatus.PENDING, new BigDecimal("70.00"));
            offer.setMessage("original");

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);

            // when
            priceOfferService.cancelOffer(offerPublicId, buyerUsername);

            // then
            assertThat(offer.getStatus()).isEqualTo(OfferStatus.REJECTED);
            assertThat(offer.getMessage()).contains("(canceled by buyer)");

            // Notification
            verify(notificationService).notifyOfferCanceled(
                    eq(seller.getUsername()),       // sellerUsername
                    eq(buyer.getUsername()),        // buyerUsername
                    eq(listing.getPublicId())       // listingPublicId
            );
        }

        @Test
        @DisplayName("throws when someone else tries to cancel the offer")
        void cancelOffer_notOwner() {

            String offerPublicId = "offer-1";
            User buyer = createUser(2L, "buyer");
            User attacker = createUser(3L, "attacker");
            User seller = createUser(1L, "seller");
            Listing listing = createListing(10L, "listing-123", seller);

            PriceOffer offer = createOffer(1L, offerPublicId, listing, buyer,
                    OfferStatus.PENDING, new BigDecimal("70.00"));

            when(priceOfferValidator.validatePriceOfferByPublicId(offerPublicId)).thenReturn(offer);
            when(authValidator.validateUserByUsername("attacker")).thenReturn(attacker);

            assertThatThrownBy(() ->
                    priceOfferService.cancelOffer(offerPublicId, "attacker")
            ).isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Not your offer.");
        }
    }

    // ============ getOffersForListing / sent / received / getOffer / flags ============

    @Nested
    @DisplayName("getOffersForListing")
    class GetOffersForListingTests {

        @Test
        @DisplayName("returns offers for listing when seller is owner")
        void getOffersForListing_success() {
            String listingId = "listing-123";
            String sellerUsername = "seller";
            User seller = createUser(1L, sellerUsername);
            Listing listing = createListing(10L, listingId, seller);

            User buyer = createUser(2L, "buyer");
            PriceOffer offer1 = createOffer(1L, "offer-1", listing, buyer, OfferStatus.PENDING, new BigDecimal("70"));
            PriceOffer offer2 = createOffer(2L, "offer-2", listing, buyer, OfferStatus.REJECTED, new BigDecimal("60"));

            when(listingValidator.validateListingByPublicId(listingId)).thenReturn(listing);
            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);
            when(priceOfferRepository.findByListing_PublicId(listingId)).thenReturn(List.of(offer1, offer2));

            List<PriceOfferResponse> responses = priceOfferService.getOffersForListing(listingId, sellerUsername);

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getPublicId()).isEqualTo("offer-1");
            assertThat(responses.get(1).getPublicId()).isEqualTo("offer-2");
        }
    }

    @Nested
    @DisplayName("getUserSentOffers")
    class GetUserSentOffersTests {

        @Test
        @DisplayName("returns offers sent by user")
        void getUserSentOffers_success() {
            String buyerUsername = "buyer";
            User buyer = createUser(2L, buyerUsername);
            User seller = createUser(1L, "seller");
            Listing listing = createListing(10L, "listing-123", seller);

            PriceOffer offer1 = createOffer(1L, "offer-1", listing, buyer, OfferStatus.PENDING, new BigDecimal("70"));

            when(authValidator.validateUserByUsername(buyerUsername)).thenReturn(buyer);
            when(priceOfferRepository.findByBuyer_Username(buyerUsername)).thenReturn(List.of(offer1));

            List<PriceOfferResponse> responses = priceOfferService.getUserSentOffers(buyerUsername);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getPublicId()).isEqualTo("offer-1");
        }
    }

    @Nested
    @DisplayName("getUserReceivedOffers")
    class GetUserReceivedOffersTests {

        @Test
        @DisplayName("returns offers received by seller")
        void getUserReceivedOffers_success() {
            String sellerUsername = "seller";
            User seller = createUser(1L, sellerUsername);
            User buyer = createUser(2L, "buyer");
            Listing listing = createListing(10L, "listing-123", seller);

            PriceOffer offer1 = createOffer(1L, "offer-1", listing, buyer, OfferStatus.PENDING, new BigDecimal("70"));

            when(authValidator.validateUserByUsername(sellerUsername)).thenReturn(seller);
            when(priceOfferRepository.findByListing_Seller_Username(sellerUsername)).thenReturn(List.of(offer1));

            List<PriceOfferResponse> responses = priceOfferService.getUserReceivedOffers(sellerUsername);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getPublicId()).isEqualTo("offer-1");
        }
    }

    @Nested
    @DisplayName("getOffer")
    class GetOfferTests {

        @Test
        @DisplayName("returns offer when user is buyer or seller")
        void getOffer_asBuyer() {
            String offerId = "offer-1";
            String username = "buyer";

            User buyer = createUser(2L, username);
            User seller = createUser(1L, "seller");
            Listing listing = createListing(10L, "listing-123", seller);
            PriceOffer offer = createOffer(1L, offerId, listing, buyer, OfferStatus.PENDING, new BigDecimal("70"));

            when(authValidator.validateUserByUsername(username)).thenReturn(buyer);
            when(priceOfferRepository.findByPublicId(offerId)).thenReturn(Optional.of(offer));

            Optional<PriceOfferResponse> result = priceOfferService.getOffer(offerId, username);

            assertThat(result).isPresent();
            assertThat(result.get().getPublicId()).isEqualTo(offerId);
        }

        @Test
        @DisplayName("returns empty Optional when offer does not exist")
        void getOffer_notFound() {
            String offerId = "offer-404";
            String username = "buyer";

            when(authValidator.validateUserByUsername(username)).thenReturn(createUser(2L, username));
            when(priceOfferRepository.findByPublicId(offerId)).thenReturn(Optional.empty());

            Optional<PriceOfferResponse> result = priceOfferService.getOffer(offerId, username);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("throws when user is neither buyer nor seller")
        void getOffer_unauthorizedUser() {
            String offerId = "offer-1";
            String username = "stranger";

            User stranger = createUser(3L, username);
            User buyer = createUser(2L, "buyer");
            User seller = createUser(1L, "seller");

            Listing listing = createListing(10L, "listing-123", seller);
            PriceOffer offer = createOffer(1L, offerId, listing, buyer, OfferStatus.PENDING, new BigDecimal("70"));

            when(authValidator.validateUserByUsername(username)).thenReturn(stranger);
            when(priceOfferRepository.findByPublicId(offerId)).thenReturn(Optional.of(offer));

            assertThatThrownBy(() ->
                    priceOfferService.getOffer(offerId, username)
            ).isInstanceOf(SecurityException.class)
                    .hasMessageContaining("You are not allowed to view this offer.");
        }
    }

    @Nested
    @DisplayName("hasPendingOffer")
    class HasPendingOfferTests {

        @Test
        @DisplayName("delegates to repository existsByBuyer_UsernameAndListing_PublicIdAndStatus")
        void hasPendingOffer_success() {
            when(priceOfferRepository.existsByBuyer_UsernameAndListing_PublicIdAndStatus(
                    "buyer", "listing-123", OfferStatus.PENDING
            )).thenReturn(true);

            boolean result = priceOfferService.hasPendingOffer("buyer", "listing-123");

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("getAcceptedOffer")
    class GetAcceptedOfferTests {

        @Test
        @DisplayName("returns latest accepted offer for listing if exists")
        void getAcceptedOffer_success() {
            String listingId = "listing-123";
            User seller = createUser(1L, "seller");
            User buyer = createUser(2L, "buyer");
            Listing listing = createListing(10L, listingId, seller);

            PriceOffer older = createOffer(1L, "offer-1", listing, buyer, OfferStatus.ACCEPTED, new BigDecimal("60"));
            older.setCreatedAt(Instant.now().minusSeconds(3600));

            PriceOffer newer = createOffer(2L, "offer-2", listing, buyer, OfferStatus.ACCEPTED, new BigDecimal("70"));
            newer.setCreatedAt(Instant.now());

            when(priceOfferRepository.findByListing_PublicIdAndStatus(listingId, OfferStatus.ACCEPTED))
                    .thenReturn(List.of(older, newer));

            Optional<PriceOfferResponse> result = priceOfferService.getAcceptedOffer(listingId);

            assertThat(result).isPresent();
            assertThat(result.get().getPublicId()).isEqualTo("offer-2");
        }

        @Test
        @DisplayName("returns empty Optional when no accepted offers exist")
        void getAcceptedOffer_empty() {
            when(priceOfferRepository.findByListing_PublicIdAndStatus("listing-123", OfferStatus.ACCEPTED))
                    .thenReturn(List.of());

            Optional<PriceOfferResponse> result = priceOfferService.getAcceptedOffer("listing-123");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("autoRejectOtherOffers")
    class AutoRejectOtherOffersTests {

        @Test
        @DisplayName("rejects all other pending offers except the accepted one")
        void autoRejectOtherOffers_success() {
            String listingId = "listing-123";
            String acceptedOfferId = "offer-accepted";

            User seller = createUser(1L, "seller");
            User buyer = createUser(2L, "buyer");
            Listing listing = createListing(10L, listingId, seller);

            PriceOffer accepted = createOffer(1L, acceptedOfferId, listing, buyer, OfferStatus.PENDING, new BigDecimal("70"));
            PriceOffer pending1 = createOffer(2L, "offer-2", listing, buyer, OfferStatus.PENDING, new BigDecimal("60"));
            PriceOffer pending2 = createOffer(3L, "offer-3", listing, buyer, OfferStatus.PENDING, new BigDecimal("55"));

            when(priceOfferRepository.findByListing_PublicIdAndStatus(listingId, OfferStatus.PENDING))
                    .thenReturn(List.of(accepted, pending1, pending2));

            // when
            priceOfferService.autoRejectOtherOffers(listingId, acceptedOfferId);

            // then
            assertThat(accepted.getStatus())
                    .as("Accepted offer should keep its status (service only rejects others)")
                    .isEqualTo(OfferStatus.PENDING);

            assertThat(pending1.getStatus()).isEqualTo(OfferStatus.REJECTED);
            assertThat(pending2.getStatus()).isEqualTo(OfferStatus.REJECTED);
        }
    }
}
