package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.CreateConversationRequest;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.message.ConversationRepository;
import edu.uic.marketplace.service.listing.ListingService;
import edu.uic.marketplace.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConversationService Unit Test")
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserService userService;

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private User buyer;
    private User seller;
    private Listing listing;
    private Conversation conversation;

    @BeforeEach
    void setUp() {

        buyer = User.builder().userId(1L).email("buyer@uic.edu").build();
        seller = User.builder().userId(2L).email("seller@uic.edu").build();
        listing = Listing.builder().listingId(1L).seller(seller).build();
        conversation = Conversation.builder()
                .conversationId(1L)
                .listing(listing)
                .buyer(buyer)
                .seller(seller)
                .lastMessageAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Create conversation - Success")
    void createConversation_Success() {

        // Given
        CreateConversationRequest request = CreateConversationRequest.builder()
                .listingId(1L)
                .initialMessage("Is this available?")
                .build();

        when(listingService.findById(1L)).thenReturn(Optional.of(listing));
        when(userService.findById(1L)).thenReturn(Optional.of(buyer));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        // When
        ConversationResponse response = conversationService.createConversation(1L, request);

        // Then
        assertThat(response).isNotNull();
        verify(conversationRepository, times(1)).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Mark as read")
    void markAsRead() {

        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        // When
        conversationService.markAsRead(1L, 1L);

        // Then
        verify(conversationRepository, times(1)).save(conversation);
    }

    @Test
    @DisplayName("Update last message at")
    void updateLastMessageAt() {

        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        Instant oldTime = conversation.getLastMessageAt();

        // When
        conversationService.updateLastMessageAt(1L);

        // Then
        assertThat(conversation.getLastMessageAt()).isAfter(oldTime);
        verify(conversationRepository, times(1)).save(conversation);
    }
}
