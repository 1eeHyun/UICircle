package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.message.Message;
import edu.uic.marketplace.model.message.MessageType;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.message.ConversationRepository;
import edu.uic.marketplace.repository.message.MessageRepository;
import edu.uic.marketplace.service.notification.NotificationService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.message.ConversationValidator;
import edu.uic.marketplace.validator.message.MessageValidator;
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
class MessageServiceImplTest {

    @Mock
    private AuthValidator authValidator;

    @Mock
    private ConversationValidator conversationValidator;

    @Mock
    private MessageValidator messageValidator;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MessageServiceImpl messageService;

    // ---------------- Helper factory methods ----------------

    private User createUser(Long id, String username) {
        return User.builder()
                .userId(id)
                .username(username)
                .build();
    }

    private Conversation createConversation(String publicId, User buyer, User seller) {
        return Conversation.builder()
                .publicId(publicId)
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
                .body("test message")
                .messageType(MessageType.TEXT)
                .build();
    }

    // ============================================================
    // sendMessage()
    // ============================================================

    @Nested
    @DisplayName("sendMessage()")
    class SendMessageTests {

        @Test
        @DisplayName("should send message, update conversation, and create notification")
        void sendMessage_success() {
            // given
            String conversationPublicId = "conv-public-id";
            String senderUsername = "senderUser";

            User sender = createUser(1L, senderUsername);
            User receiver = createUser(2L, "receiverUser");

            Conversation conversation = createConversation(conversationPublicId, receiver, sender);

            SendMessageRequest request = SendMessageRequest.builder()
                    .body("Hello there")
                    .build();

            when(authValidator.validateUserByUsername(senderUsername)).thenReturn(sender);
            when(conversationValidator.validateConversation(conversationPublicId)).thenReturn(conversation);
            doNothing().when(conversationValidator).validateParticipant(conversation, sender);
            doNothing().when(messageValidator).validateMessageBody(request.getBody());
            when(conversationValidator.validateReceiverInConversation(conversation, sender)).thenReturn(receiver);

            // messageRepository.save(...) should return a message with sender set
            Message savedMessage = createMessage("msg-public-id", conversation, sender);
            when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

            // when
            MessageResponse response = messageService.sendMessage(conversationPublicId, senderUsername, request);

            // then
            assertThat(response).isNotNull();

            // verify message saved
            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
            verify(messageRepository).save(messageCaptor.capture());
            Message toSave = messageCaptor.getValue();
            assertThat(toSave.getConversation()).isEqualTo(conversation);
            assertThat(toSave.getSender()).isEqualTo(sender);
            assertThat(toSave.getBody()).isEqualTo("Hello there");
            assertThat(toSave.getMessageType()).isEqualTo(MessageType.TEXT);

            // verify conversation updated and saved
            verify(conversationRepository).save(conversation);
            assertThat(conversation.getLastMessageAt()).isNotNull();

            // verify notification
            verify(notificationService).notifyNewMessage(
                    receiver.getUsername(),
                    sender.getUsername(),
                    conversationPublicId
            );
        }
    }

    // ============================================================
    // findById()
    // ============================================================

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("should validate user and return message when authorized")
        void findById_success() {
            // given
            String messagePublicId = "msg-id";
            String username = "user1";

            User user = createUser(1L, username);
            Conversation conversation = createConversation("conv-id", user, createUser(2L, "other"));
            Message message = createMessage(messagePublicId, conversation, user);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(messageValidator.validateMessage(messagePublicId)).thenReturn(message);
            doNothing().when(conversationValidator).validateParticipant(conversation, user);

            // when
            Optional<Message> result = messageService.findById(messagePublicId, username);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(message);
        }
    }

    // ============================================================
    // getMessages()
    // ============================================================

    @Nested
    @DisplayName("getMessages()")
    class GetMessagesTests {

        @Test
        @DisplayName("should return paginated messages for conversation")
        void getMessages_success() {
            // given
            String conversationPublicId = "conv-id";
            String username = "user1";

            User user = createUser(1L, username);
            User other = createUser(2L, "other");
            Conversation conversation = createConversation(conversationPublicId, user, other);
            Message message = createMessage("msg-id", conversation, user);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(conversationValidator.validateConversation(conversationPublicId)).thenReturn(conversation);
            doNothing().when(conversationValidator).validateParticipant(conversation, user);

            Page<Message> messagePage = new PageImpl<>(
                    List.of(message),
                    PageRequest.of(0, 20, Sort.by("createdAt").descending()),
                    1
            );
            when(messageRepository.findByConversation_PublicId(eq(conversationPublicId), any(Pageable.class)))
                    .thenReturn(messagePage);

            // when
            PageResponse<MessageResponse> result =
                    messageService.getMessages(conversationPublicId, username, 0, 20);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(messageRepository).findByConversation_PublicId(eq(conversationPublicId), any(Pageable.class));
        }
    }

    // ============================================================
    // markAsRead()
    // ============================================================

    @Nested
    @DisplayName("markAsRead()")
    class MarkAsReadTests {

        @Test
        @DisplayName("should mark message as read when user is receiver and message is unread")
        void markAsRead_success() {
            // given
            String messagePublicId = "msg-id";
            String receiverUsername = "receiverUser";

            User sender = createUser(1L, "senderUser");
            User receiver = createUser(2L, receiverUsername);
            Conversation conversation = createConversation("conv-id", receiver, sender);
            Message message = spy(createMessage(messagePublicId, conversation, sender));

            when(authValidator.validateUserByUsername(receiverUsername)).thenReturn(receiver);
            when(messageValidator.validateMessage(messagePublicId)).thenReturn(message);
            doNothing().when(conversationValidator).validateParticipant(conversation, receiver);

            // message is unread initially
            when(message.isUnread()).thenReturn(true);

            // when
            messageService.markAsRead(messagePublicId, receiverUsername);

            // then
            verify(message).isUnread();
            verify(message).markAsRead();
            verify(messageRepository).save(message);
        }

        @Test
        @DisplayName("should not save when message is already read (idempotent)")
        void markAsRead_alreadyRead() {
            // given
            String messagePublicId = "msg-id";
            String receiverUsername = "receiverUser";

            User sender = createUser(1L, "senderUser");
            User receiver = createUser(2L, receiverUsername);
            Conversation conversation = createConversation("conv-id", receiver, sender);
            Message message = spy(createMessage(messagePublicId, conversation, sender));

            when(authValidator.validateUserByUsername(receiverUsername)).thenReturn(receiver);
            when(messageValidator.validateMessage(messagePublicId)).thenReturn(message);
            doNothing().when(conversationValidator).validateParticipant(conversation, receiver);

            when(message.isUnread()).thenReturn(false);

            // when
            messageService.markAsRead(messagePublicId, receiverUsername);

            // then
            verify(message).isUnread();
            verify(message, never()).markAsRead();
            verify(messageRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when sender tries to mark their own message as read")
        void markAsRead_senderForbidden() {
            // given
            String messagePublicId = "msg-id";
            String senderUsername = "senderUser";

            User sender = createUser(1L, senderUsername);
            User receiver = createUser(2L, "receiverUser");
            Conversation conversation = createConversation("conv-id", receiver, sender);
            Message message = createMessage(messagePublicId, conversation, sender);

            when(authValidator.validateUserByUsername(senderUsername)).thenReturn(sender);
            when(messageValidator.validateMessage(messagePublicId)).thenReturn(message);
            doNothing().when(conversationValidator).validateParticipant(conversation, sender);

            // when / then
            assertThatThrownBy(() -> messageService.markAsRead(messagePublicId, senderUsername))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Sender cannot mark their own message as read.");

            verify(messageRepository, never()).save(any());
        }
    }

    // ============================================================
    // markConversationAsRead()
    // ============================================================

    @Nested
    @DisplayName("markConversationAsRead()")
    class MarkConversationAsReadTests {

        @Test
        @DisplayName("should reset unread counter and mark all messages as read")
        void markConversationAsRead_success() {
            // given
            String conversationPublicId = "conv-id";
            String username = "user1";

            User user = createUser(1L, username);
            User other = createUser(2L, "other");
            Conversation conversation = spy(createConversation(conversationPublicId, user, other));

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(conversationValidator.validateConversation(conversationPublicId)).thenReturn(conversation);
            doNothing().when(conversationValidator).validateParticipant(conversation, user);

            // when
            messageService.markConversationAsRead(conversationPublicId, username);

            // then
            verify(conversation).resetUnreadCountForUser(user.getUserId());
            verify(conversationRepository).save(conversation);
            verify(messageRepository).markAllAsReadInConversation(conversationPublicId, username);
        }
    }

    // ============================================================
    // deleteMessage()
    // ============================================================

    @Nested
    @DisplayName("deleteMessage()")
    class DeleteMessageTests {

        @Test
        @DisplayName("should soft delete when user is sender and message not deleted")
        void deleteMessage_success() {
            // given
            String messagePublicId = "msg-id";
            String senderUsername = "senderUser";

            User sender = createUser(1L, senderUsername);
            User receiver = createUser(2L, "receiverUser");
            Conversation conversation = createConversation("conv-id", receiver, sender);
            Message message = spy(createMessage(messagePublicId, conversation, sender));

            when(authValidator.validateUserByUsername(senderUsername)).thenReturn(sender);
            when(messageValidator.validateMessage(messagePublicId)).thenReturn(message);
            when(message.isDeleted()).thenReturn(false);

            // when
            messageService.deleteMessage(messagePublicId, senderUsername);

            // then
            verify(message).isDeleted();
            verify(message).softDelete();
            verify(messageRepository).save(message);
        }

        @Test
        @DisplayName("should throw when non-sender tries to delete the message")
        void deleteMessage_nonSenderForbidden() {
            // given
            String messagePublicId = "msg-id";
            String username = "notSender";

            User sender = createUser(1L, "senderUser");
            User notSender = createUser(2L, username);
            Conversation conversation = createConversation("conv-id", notSender, sender);
            Message message = createMessage(messagePublicId, conversation, sender);

            when(authValidator.validateUserByUsername(username)).thenReturn(notSender);
            when(messageValidator.validateMessage(messagePublicId)).thenReturn(message);

            // when / then
            assertThatThrownBy(() -> messageService.deleteMessage(messagePublicId, username))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("You can only delete your own messages.");

            verify(messageRepository, never()).save(any());
        }

        @Test
        @DisplayName("should not save when message is already deleted")
        void deleteMessage_alreadyDeleted() {
            // given
            String messagePublicId = "msg-id";
            String senderUsername = "senderUser";

            User sender = createUser(1L, senderUsername);
            User receiver = createUser(2L, "receiverUser");
            Conversation conversation = createConversation("conv-id", receiver, sender);
            Message message = spy(createMessage(messagePublicId, conversation, sender));

            when(authValidator.validateUserByUsername(senderUsername)).thenReturn(sender);
            when(messageValidator.validateMessage(messagePublicId)).thenReturn(message);
            when(message.isDeleted()).thenReturn(true);

            // when
            messageService.deleteMessage(messagePublicId, senderUsername);

            // then
            verify(message).isDeleted();
            verify(message, never()).softDelete();
            verify(messageRepository, never()).save(any());
        }
    }

    // ============================================================
    // getUnreadMessageCount()
    // ============================================================

    @Nested
    @DisplayName("getUnreadMessageCount()")
    class GetUnreadMessageCountTests {

        @Test
        @DisplayName("should delegate to repository")
        void getUnreadMessageCount_success() {
            // given
            String username = "user1";
            when(messageRepository.countUnreadByUsername(username)).thenReturn(10L);

            // when
            Long count = messageService.getUnreadMessageCount(username);

            // then
            assertThat(count).isEqualTo(10L);
            verify(messageRepository).countUnreadByUsername(username);
        }
    }

    // ============================================================
    // getUnreadCountInConversation()
    // ============================================================

    @Nested
    @DisplayName("getUnreadCountInConversation()")
    class GetUnreadCountInConversationTests {

        @Test
        @DisplayName("should validate and return unread count")
        void getUnreadCountInConversation_success() {
            // given
            String conversationPublicId = "conv-id";
            String username = "user1";

            User user = createUser(1L, username);
            User other = createUser(2L, "other");
            Conversation conversation = createConversation(conversationPublicId, user, other);

            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(conversationValidator.validateConversation(conversationPublicId)).thenReturn(conversation);
            doNothing().when(conversationValidator).validateParticipant(conversation, user);
            when(messageRepository.countUnreadInConversation(conversationPublicId, username)).thenReturn(3L);

            // when
            Long count = messageService.getUnreadCountInConversation(conversationPublicId, username);

            // then
            assertThat(count).isEqualTo(3L);
            verify(messageRepository).countUnreadInConversation(conversationPublicId, username);
        }
    }
}
