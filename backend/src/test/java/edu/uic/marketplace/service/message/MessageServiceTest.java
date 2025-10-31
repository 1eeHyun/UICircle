package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.message.Message;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.message.MessageRepository;
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
@DisplayName("MessageService Unit Test")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationService conversationService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User sender;
    private Conversation conversation;
    private Message message;

    @BeforeEach
    void setUp() {

        sender = User.builder().userId(1L).email("sender@uic.edu").build();
        conversation = Conversation.builder().conversationId(1L).build();
        message = Message.builder()
                .messageId(1L)
                .conversation(conversation)
                .sender(sender)
                .body("Hello")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Send message - Success")
    void sendMessage_Success() {

        // Given
        SendMessageRequest request = SendMessageRequest.builder()
                .body("Hello, is this available?")
                .build();

        when(conversationService.findById(1L)).thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // When
        MessageResponse response = messageService.sendMessage(1L, 1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isEqualTo("Hello");
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(conversationService, times(1)).updateLastMessageAt(1L);
    }

    @Test
    @DisplayName("Mark as read")
    void markAsRead() {

        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // When
        messageService.markAsRead(1L, 1L);

        // Then
        assertThat(message.getReadAt()).isNotNull();
        verify(messageRepository, times(1)).save(message);
    }

    @Test
    @DisplayName("Delete message")
    void deleteMessage() {

        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // When
        messageService.deleteMessage(1L, 1L);

        // Then
        assertThat(message.getDeletedAt()).isNotNull();
        verify(messageRepository, times(1)).save(message);
    }
}
