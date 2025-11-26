package edu.uic.marketplace.validator.message;

import edu.uic.marketplace.model.message.Message;
import edu.uic.marketplace.repository.message.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageValidator {

    private final MessageRepository messageRepository;

    public void validateMessageBody(String messagePublicId) {
        if (messagePublicId == null || messagePublicId.trim().isEmpty()) {
            throw new IllegalArgumentException("Message body must not be empty.");
        }
    }

    public Message validateMessage(String messagePublicId) {
        return messageRepository.findByPublicId(messagePublicId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
    }
}
