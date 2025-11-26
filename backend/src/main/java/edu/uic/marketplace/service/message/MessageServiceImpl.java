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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final AuthValidator authValidator;
    private final ConversationValidator conversationValidator;
    private final MessageValidator messageValidator;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    private final NotificationService notificationService;

    @Override
    @Transactional
    public MessageResponse sendMessage(String conversationPublicId, String senderUsername, SendMessageRequest request) {

        // 1) Validate sender
        User sender = authValidator.validateUserByUsername(senderUsername);

        // 2) Validate conversation
        Conversation conversation = conversationValidator.validateConversation(conversationPublicId);

        // 3) Validate authorization
        conversationValidator.validateParticipant(conversation, sender);

        // 4) Validate body
        messageValidator.validateMessageBody(request.getBody());

        // 5) Determine receiver (the other side)
        User receiver = conversationValidator.validateReceiverInConversation(conversation, sender);

        // 6) Create message
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .body(request.getBody())
                .messageType(MessageType.TEXT)
                .build();

        Message saved = messageRepository.save(message);

        // 7) Update conversation unread counts & last message timestamp
        conversation.updateLastMessageAt();
        conversation.incrementUnreadCountForUser(receiver.getUserId());
        conversationRepository.save(conversation);

        // 8) Notification
        notificationService.notifyNewMessage(
                receiver.getUsername(),
                sender.getUsername(),
                conversationPublicId
        );

        // 9) Return DTO
        return MessageResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> findById(String messagePublicId, String username) {

        // 1) Validate user
        User user = authValidator.validateUserByUsername(username);

        // 2) Validate message
        Message message = messageValidator.validateMessage(messagePublicId);

        // 3) Validate if the user is in the conversation
        Conversation conversation = message.getConversation();
        conversationValidator.validateParticipant(conversation, user);

        return Optional.of(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> getMessages(String conversationPublicId, String username, Integer page, Integer size) {

        // 1) Validate user
        User user = authValidator.validateUserByUsername(username);

        // 2) Validate conversation
        Conversation conversation = conversationValidator.validateConversation(conversationPublicId);

        // 3) Validate authorization
        conversationValidator.validateParticipant(conversation, user);

        // 4) Paging
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<MessageResponse> pageResult = messageRepository
                .findByConversation_PublicId(conversationPublicId, pageable)
                .map(MessageResponse::from);

        return PageResponse.fromPage(pageResult);
    }

    @Override
    @Transactional
    public void markAsRead(String messagePublicId, String username) {

        // 1) Validate user
        User user = authValidator.validateUserByUsername(username);

        // 2) Find message
        Message message = messageValidator.validateMessage(messagePublicId);

        Conversation conversation = message.getConversation();

        // 3) Authorization
        conversationValidator.validateParticipant(conversation, user);

        // 4) Only receiver can mark as read
        if (message.getSender().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Sender cannot mark their own message as read.");
        }

        // 5) Idempotent: already read → skip
        if (message.isUnread()) {
            message.markAsRead();
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional
    public void markConversationAsRead(String conversationPublicId, String username) {

        // 1) Validate user
        User user = authValidator.validateUserByUsername(username);

        // 2) Validate conversation
        Conversation conversation = conversationValidator.validateConversation(conversationPublicId);

        // 3) Authorization
        conversationValidator.validateParticipant(conversation, user);

        // 4) Reset unread counters in conversation
        conversation.resetUnreadCountForUser(user.getUserId());
        conversationRepository.save(conversation);

        // 5) Mark all messages as read in DB
        messageRepository.markAllAsReadInConversation(conversationPublicId, username);
    }

    @Override
    @Transactional
    public void deleteMessage(String messagePublicId, String username) {

        // 1) Validate user
        User user = authValidator.validateUserByUsername(username);

        // 2) Find message
        Message message = messageValidator.validateMessage(messagePublicId);

        // 3) Only sender can delete
        if (!message.getSender().getUserId().equals(user.getUserId())) {
            throw new SecurityException("You can only delete your own messages.");
        }

        // 4) Soft delete
        if (!message.isDeleted()) {
            message.softDelete();
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(String username) {
        return messageRepository.countUnreadByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCountInConversation(String conversationPublicId, String username) {

        // Validate
        User user = authValidator.validateUserByUsername(username);
        Conversation conversation = conversationValidator.validateConversation(conversationPublicId);
        conversationValidator.validateParticipant(conversation, user);

        return messageRepository.countUnreadInConversation(conversationPublicId, username);
    }

    // Helper methods
}
