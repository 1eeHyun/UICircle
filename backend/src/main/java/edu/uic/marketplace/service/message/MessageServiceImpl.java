package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import edu.uic.marketplace.model.message.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    @Override
    public MessageResponse sendMessage(Long conversationId, Long senderId, SendMessageRequest request) {
        return null;
    }

    @Override
    public Optional<Message> findById(Long messageId) {
        return Optional.empty();
    }

    @Override
    public PageResponse<MessageResponse> getMessages(Long conversationId, Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public void markAsRead(Long messageId, Long userId) {

    }

    @Override
    public void markConversationAsRead(Long conversationId, Long userId) {

    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {

    }

    @Override
    public Long getUnreadMessageCount(Long userId) {
        return null;
    }

    @Override
    public Long getUnreadCountInConversation(Long conversationId, Long userId) {
        return null;
    }
}
