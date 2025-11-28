package edu.uic.marketplace.validator.message;

import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.message.ConversationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationValidator {

    private final ConversationRepository conversationRepository;

    public Conversation validateConversation(String conversationPublicId) {

        return conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
    }

    public void validateParticipant(Conversation conversation, User user) {

        Long userId = user.getUserId();
        boolean isParticipant = conversation.getBuyer().getUserId().equals(userId)
                || conversation.getSeller().getUserId().equals(userId);

        if (!isParticipant)
            throw new SecurityException("You are not a participant of this conversation.");
    }

    public User validateReceiverInConversation(Conversation conversation, User sender) {

        if (conversation.getBuyer().getUserId().equals(sender.getUserId())) {
            return conversation.getSeller();
        } else if (conversation.getSeller().getUserId().equals(sender.getUserId())) {
            return conversation.getBuyer();
        }

        throw new SecurityException("User is not a participant in the conversation.");
    }
}
