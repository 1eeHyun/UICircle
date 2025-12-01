package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.CreateConversationRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.message.Message;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.message.ConversationRepository;
import edu.uic.marketplace.repository.message.MessageRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import edu.uic.marketplace.validator.message.ConversationValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceImplTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private ConversationValidator conversationValidator;

    @Mock
    private ListingValidator listingValidator;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    // ---------------- Helper factory methods ----------------

    private User createUser(Long id, String username) {
        return User.builder()
                .userId(id)
                .username(username)
                .build();
    }

    private Listing createListing(String publicId, User seller) {
        return Listing.builder()
                .publicId(publicId)
                .seller(seller)
                .listingId(1L)
                .build();
    }

    private Conversation createConversation(String publicId, Listing listing, User buyer, User seller) {
        return Conversation.builder()
                .publicId(publicId)
                .listing(listing)
                .buyer(buyer)
                .seller(seller)
                .sellerUnreadCount(0)
                .buyerUnreadCount(0)
                .lastMessageAt(Instant.now())
                .build();
    }

    private Message createMessage(String publicId, Conversation conversation, User sender) {
        return Message.builder()
                .publicId(publicId)
                .conversation(conversation)
                .sender(sender)
                .body("Test message")
                .createdAt(Instant.now())
                .build();
    }

    // ============================================================
    // createConversation()
    // ============================================================

    @Nested
    @DisplayName("createConversation()")
    class CreateConversationTests {

        @Test
        @DisplayName("should create new conversation when none exists")
        void createConversation_new() {
            // given
            String username = "buyerUser";
            String listingPublicId = "listing-public-id";

            User buyer = createUser(1L, username);
            User seller = createUser(2L, "sellerUser");
            Listing listing = createListing(listingPublicId, seller);

            CreateConversationRequest request = CreateConversationRequest.builder()
                    .listingPublicId(listingPublicId)
                    .build();

            when(authValidator.validateUserByUsername(username)).thenReturn(buyer);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);
            when(conversationRepository.findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                    listingPublicId, buyer.getUsername(), seller.getUsername()
            )).thenReturn(Optional.empty());

            Conversation saved = createConversation("conv-public-id", listing, buyer, seller);
            when(conversationRepository.save(any(Conversation.class))).thenReturn(saved);

            // when
            ConversationResponse response = conversationService.createConversation(username, request);

            // then
            assertThat(response).isNotNull();

            ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationRepository).save(captor.capture());
            Conversation toSave = captor.getValue();

            assertThat(toSave.getListing()).isEqualTo(listing);
            assertThat(toSave.getBuyer()).isEqualTo(buyer);
            assertThat(toSave.getSeller()).isEqualTo(seller);
            assertThat(toSave.getSellerUnreadCount()).isZero();
            assertThat(toSave.getBuyerUnreadCount()).isZero();
            assertThat(toSave.getLastMessageAt()).isNotNull();
        }

        @Test
        @DisplayName("should return existing conversation if already exists")
        void createConversation_existing() {
            // given
            String username = "buyerUser";
            String listingPublicId = "listing-public-id";

            User buyer = createUser(1L, username);
            User seller = createUser(2L, "sellerUser");
            Listing listing = createListing(listingPublicId, seller);
            Conversation existing = createConversation("conv-public-id", listing, buyer, seller);

            CreateConversationRequest request = CreateConversationRequest.builder()
                    .listingPublicId(listingPublicId)
                    .build();

            when(authValidator.validateUserByUsername(username)).thenReturn(buyer);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);
            when(conversationRepository.findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                    listingPublicId, buyer.getUsername(), seller.getUsername()
            )).thenReturn(Optional.of(existing));

            // when
            ConversationResponse response = conversationService.createConversation(username, request);

            // then
            assertThat(response).isNotNull();
            verify(conversationRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when user is seller of the listing")
        void createConversation_selfListing() {
            // given
            String username = "sameUser";
            String listingPublicId = "listing-public-id";

            User user = createUser(1L, username);
            Listing listing = createListing(listingPublicId, user);

            CreateConversationRequest request = CreateConversationRequest.builder()
                    .listingPublicId(listingPublicId)
                    .build();

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(listingValidator.validateListingByPublicId(listingPublicId)).thenReturn(listing);

            // when / then
            assertThatThrownBy(() -> conversationService.createConversation(username, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("You cannot start a conversation on your own listing.");

            verify(conversationRepository, never()).save(any());
        }
    }

    // ============================================================
    // findById()
    // ============================================================

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("should validate user and participant and return conversation")
        void findById_success() {
            // given
            String username = "user1";
            String convPublicId = "conv-id";

            User user = createUser(1L, username);
            User other = createUser(2L, "other");
            Listing listing = createListing("listing-id", other);
            Conversation conversation = createConversation(convPublicId, listing, user, other);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(conversationValidator.validateConversation(convPublicId)).thenReturn(conversation);
            doNothing().when(conversationValidator).validateParticipant(conversation, user);

            // when
            Optional<Conversation> result = conversationService.findById(convPublicId, username);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(conversation);
        }
    }

    // ============================================================
    // getConversation()
    // ============================================================

    @Nested
    @DisplayName("getConversation()")
    class GetConversationTests {

        @Test
        @DisplayName("should return ConversationResponse for participant")
        void getConversation_success() {
            // given
            String username = "user1";
            String convPublicId = "conv-id";

            User user = createUser(1L, username);
            User other = createUser(2L, "other");
            Listing listing = createListing("listing-id", other);
            Conversation conversation = createConversation(convPublicId, listing, user, other);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(conversationValidator.validateConversation(convPublicId)).thenReturn(conversation);
            doNothing().when(conversationValidator).validateParticipant(conversation, user);

            // when
            ConversationResponse response = conversationService.getConversation(convPublicId, username);

            // then
            assertThat(response).isNotNull();
        }
    }

    // ============================================================
    // getConversations()
    // ============================================================

    @Nested
    @DisplayName("getConversations()")
    class GetConversationsTests {

        @Test
        @DisplayName("should return paginated conversation responses for current user")
        void getConversations_success() {

            // given
            String username = "user1";
            User currentUser = createUser(1L, username);
            User otherUser = createUser(2L, "user2");

            Listing listing = createListing("listing-id", otherUser);
            Conversation conv = createConversation("conv-id", listing, currentUser, otherUser);

            Page<Conversation> convPage = new PageImpl<>(
                    List.of(conv),
                    PageRequest.of(0, 20),
                    1
            );

            when(authValidator.validateUserByUsername(username)).thenReturn(currentUser);
            when(conversationRepository.findVisibleConversationsOptimized(
                    eq(currentUser.getUserId()),
                    any(Pageable.class)
            )).thenReturn(convPage);

            Message lastMessage = createMessage("msg-id", conv, otherUser);
            when(messageRepository.findLatestMessagesByConversationIds(
                    eq(List.of(conv.getPublicId())),
                    any(Pageable.class)
            )).thenReturn(List.of(lastMessage));

            // when
            PageResponse<ConversationResponse> result =
                    conversationService.getConversations(username, 0, 20);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(conversationRepository).findVisibleConversationsOptimized(
                    eq(currentUser.getUserId()),
                    any(Pageable.class)
            );
            verify(messageRepository).findLatestMessagesByConversationIds(
                    eq(List.of(conv.getPublicId())),
                    any(Pageable.class)
            );
        }

        @Test
        @DisplayName("should handle empty conversations list")
        void getConversations_empty() {
            // given
            String username = "user1";
            User currentUser = createUser(1L, username);

            Page<Conversation> emptyPage = new PageImpl<>(
                    List.of(),
                    PageRequest.of(0, 20),
                    0
            );

            when(authValidator.validateUserByUsername(username)).thenReturn(currentUser);
            when(conversationRepository.findVisibleConversationsOptimized(
                    eq(currentUser.getUserId()),
                    any(Pageable.class)
            )).thenReturn(emptyPage);

            // when
            PageResponse<ConversationResponse> result =
                    conversationService.getConversations(username, 0, 20);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();

            // Should not call findLatestMessagesByConversationIds when no conversations
            verify(messageRepository, never()).findLatestMessagesByConversationIds(anyList(), any());
        }
    }

    // ============================================================
    // findByListingAndUsers()
    // ============================================================

    @Nested
    @DisplayName("findByListingAndUsers()")
    class FindByListingAndUsersTests {

        @Test
        @DisplayName("should return conversation when first lookup (username1 as buyer, username2 as seller) succeeds")
        void findByListingAndUsers_direct() {
            // given
            String listingPublicId = "listing-id";
            String username1 = "buyerUser";
            String username2 = "sellerUser";

            User buyer = createUser(1L, username1);
            User seller = createUser(2L, username2);
            Listing listing = createListing(listingPublicId, seller);
            Conversation conv = createConversation("conv-id", listing, buyer, seller);

            when(conversationRepository.findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                    listingPublicId, username1, username2
            )).thenReturn(Optional.of(conv));

            // when
            Optional<Conversation> result =
                    conversationService.findByListingAndUsers(listingPublicId, username1, username2);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(conv);

            verify(conversationRepository, times(1))
                    .findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(listingPublicId, username1, username2);
            verify(conversationRepository, never())
                    .findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(listingPublicId, username2, username1);
        }

        @Test
        @DisplayName("should try reversed order when direct lookup is empty")
        void findByListingAndUsers_reverse() {
            // given
            String listingPublicId = "listing-id";
            String username1 = "buyerUser";
            String username2 = "sellerUser";

            User buyer = createUser(1L, username2);
            User seller = createUser(2L, username1);
            Listing listing = createListing(listingPublicId, seller);
            Conversation conv = createConversation("conv-id", listing, buyer, seller);

            when(conversationRepository.findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                    listingPublicId, username1, username2
            )).thenReturn(Optional.empty());

            when(conversationRepository.findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                    listingPublicId, username2, username1
            )).thenReturn(Optional.of(conv));

            // when
            Optional<Conversation> result =
                    conversationService.findByListingAndUsers(listingPublicId, username1, username2);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(conv);

            verify(conversationRepository, times(1))
                    .findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(listingPublicId, username1, username2);
            verify(conversationRepository, times(1))
                    .findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(listingPublicId, username2, username1);
        }
    }

    // ============================================================
    // getUnreadConversationCount()
    // ============================================================

    @Nested
    @DisplayName("getUnreadConversationCount()")
    class GetUnreadConversationCountTests {

        @Test
        @DisplayName("should validate user and delegate to repository")
        void getUnreadConversationCount_success() {
            // given
            String username = "user1";
            User user = createUser(1L, username);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(conversationRepository.countUnreadByUsername(username)).thenReturn(5L);

            // when
            Long count = conversationService.getUnreadConversationCount(username);

            // then
            assertThat(count).isEqualTo(5L);
            verify(conversationRepository).countUnreadByUsername(username);
        }
    }

    // ============================================================
    // leaveConversation()
    // ============================================================

    @Nested
    @DisplayName("leaveConversation()")
    class LeaveConversationTests {

        @Test
        @DisplayName("should soft delete for seller and reset seller unread count")
        void leaveConversation_seller() {
            // given
            String username = "sellerUser";
            String convPublicId = "conv-id";

            User seller = createUser(1L, username);
            User buyer = createUser(2L, "buyerUser");
            Listing listing = createListing("listing-id", seller);

            Conversation conv = createConversation(convPublicId, listing, buyer, seller);
            conv.setSellerUnreadCount(3);

            when(authValidator.validateUserByUsername(username)).thenReturn(seller);
            when(conversationValidator.validateConversation(convPublicId)).thenReturn(conv);
            doNothing().when(conversationValidator).validateParticipant(conv, seller);

            // when
            conversationService.leaveConversation(convPublicId, username);

            // then
            assertThat(conv.getSellerDeletedAt()).isNotNull();
            assertThat(conv.getSellerUnreadCount()).isZero();
            verify(conversationRepository).save(conv);
        }

        @Test
        @DisplayName("should soft delete for buyer and reset buyer unread count")
        void leaveConversation_buyer() {
            // given
            String username = "buyerUser";
            String convPublicId = "conv-id";

            User seller = createUser(1L, "sellerUser");
            User buyer = createUser(2L, username);
            Listing listing = createListing("listing-id", seller);

            Conversation conv = createConversation(convPublicId, listing, buyer, seller);
            conv.setBuyerUnreadCount(4);

            when(authValidator.validateUserByUsername(username)).thenReturn(buyer);
            when(conversationValidator.validateConversation(convPublicId)).thenReturn(conv);
            doNothing().when(conversationValidator).validateParticipant(conv, buyer);

            // when
            conversationService.leaveConversation(convPublicId, username);

            // then
            assertThat(conv.getBuyerDeletedAt()).isNotNull();
            assertThat(conv.getBuyerUnreadCount()).isZero();
            verify(conversationRepository).save(conv);
        }
    }
}
