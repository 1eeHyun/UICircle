package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.CreateConversationRequest;
import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import edu.uic.marketplace.model.listing.Category;
import edu.uic.marketplace.model.listing.ItemCondition;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.model.user.UserRole;
import edu.uic.marketplace.model.user.UserStatus;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.repository.message.ConversationRepository;
import edu.uic.marketplace.repository.message.MessageRepository;
import edu.uic.marketplace.repository.user.UserRepository;
import edu.uic.marketplace.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Message Domain Integration Test")
class MessageIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Conversation and message full flow")
    void conversationAndMessage_FullFlow() {

        // Given
        User buyer = createUser("buyer@uic.edu");
        User seller = createUser("seller@uic.edu");
        Listing listing = createListing(seller);

        // When - Create a conversation
        CreateConversationRequest conversationRequest = CreateConversationRequest.builder()
                .listingId(listing.getListingId())
                .initialMessage("Is this still available?")
                .build();

        ConversationResponse conversation = conversationService.createConversation(
                buyer.getUserId(), conversationRequest);

        // Then - Check the conversation
        assertThat(conversation).isNotNull();
        assertThat(conversation.getListing().getListingId()).isEqualTo(listing.getListingId());
        assertThat(conversation.getOtherUser().getUserId()).isEqualTo(seller.getUserId());

        // When - Send a message
        SendMessageRequest messageRequest = SendMessageRequest.builder()
                .body("Yes, it's available!")
                .build();

        MessageResponse message = messageService.sendMessage(
                conversation.getConversationId(), seller.getUserId(), messageRequest);

        // Then - Check the message
        assertThat(message).isNotNull();
        assertThat(message.getBody()).isEqualTo("Yes, it's available!");
        assertThat(message.getSenderId()).isEqualTo(seller.getUserId());

        // DB verification
        Long messageCount = messageRepository.countByConversation_ConversationId(
                conversation.getConversationId());
        assertThat(messageCount).isEqualTo(2); // initial + 1
    }

    @Test
    @DisplayName("Mark a message as read")
    void markMessageAsRead() {

        // Given
        User buyer = createUser("buyer@uic.edu");
        User seller = createUser("seller@uic.edu");
        Listing listing = createListing(seller);

        CreateConversationRequest conversationRequest = CreateConversationRequest.builder()
                .listingId(listing.getListingId())
                .initialMessage("Hello")
                .build();

        ConversationResponse conversation = conversationService.createConversation(
                buyer.getUserId(), conversationRequest);

        PageResponse<MessageResponse> messages = messageService.getMessages(
                conversation.getConversationId(), seller.getUserId(), 0, 10);
        Long messageId = messages.getContent().get(0).getMessageId();

        // When
        messageService.markAsRead(messageId, seller.getUserId());

        // Then
        var message = messageRepository.findById(messageId).orElseThrow();
        assertThat(message.getReadAt()).isNotNull();
    }

    // Helper methods
    private User createUser(String email) {

        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .passwordHash("hashed_password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .build();
        
        return userRepository.save(user);
    }

    private Listing createListing(User seller) {

        Category category = categoryRepository.save(
                Category.builder().name("Books").parent(null).build());
        
        Listing listing = Listing.builder()
                .seller(seller)
                .category(category)
                .title("Test Listing")
                .description("Test description")
                .price(new BigDecimal("50.00"))
                .condition(ItemCondition.LIKE_NEW)
                .status(ListingStatus.ACTIVE)
                .latitude(41.8781)
                .longitude(-87.6298)
                .viewCount(0)
                .favoriteCount(0)
                .createdAt(Instant.now())
                .build();
        
        return listingRepository.save(listing);
    }
}
