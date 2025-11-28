package edu.uic.marketplace.service.message;

import edu.uic.marketplace.dto.request.message.CreateConversationRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import edu.uic.marketplace.dto.response.user.UserResponse;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.message.Conversation;
import edu.uic.marketplace.model.message.Message;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.message.ConversationRepository;
import edu.uic.marketplace.repository.message.MessageRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import edu.uic.marketplace.validator.message.ConversationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    private final AuthValidator authValidator;
    private final ConversationValidator conversationValidator;
    private final ListingValidator listingValidator;

    @Override
    @Transactional
    public ConversationResponse createConversation(String username, CreateConversationRequest request) {

        // 1) Validate current user (buyer candidate)
        User user = authValidator.validateUserByUsername(username);

        // 2) Validate listing
        Listing listing = listingValidator.validateListingByPublicId(request.getListingPublicId());

        // 3) Prevent seller from starting conversation on their own listing
        if (listing.getSeller().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("You cannot start a conversation on your own listing.");
        }

        User buyer = user;
        User seller = listing.getSeller();

        // 4) Check if a conversation already exists between this buyer and seller for this listing
        Optional<Conversation> existing = conversationRepository
                .findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                        listing.getPublicId(),
                        buyer.getUsername(),
                        seller.getUsername()
                );

        if (existing.isPresent()) {
            // If conversation already exists, just return it for the current user
            return ConversationResponse.fromForUser(existing.get(), user);
        }

        // 5) Create a new conversation
        Conversation conversation = Conversation.builder()
                .listing(listing)
                .buyer(buyer)
                .seller(seller)
                .sellerUnreadCount(0)
                .buyerUnreadCount(0)
                .lastMessageAt(Instant.now()) // show newly created conversation at top
                .build();

        Conversation saved = conversationRepository.save(conversation);

        // (Optional) If you want to send an initial message here,
        // call MessageService.sendMessage(...) instead of leaving it to frontend.

        return ConversationResponse.fromForUser(saved, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findById(String conversationPublicId, String username) {

        User user = authValidator.validateUserByUsername(username);
        Conversation conversation = conversationValidator.validateConversation(conversationPublicId);
        conversationValidator.validateParticipant(conversation, user);

        return Optional.of(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversation(String conversationPublicId, String username) {

        User user = authValidator.validateUserByUsername(username);
        Conversation conversation = conversationValidator.validateConversation(conversationPublicId);
        conversationValidator.validateParticipant(conversation, user);

        return ConversationResponse.fromForUser(conversation, user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConversationResponse> getConversations(String username, Integer page, Integer size) {

        // 1) Validate current user
        User user = authValidator.validateUserByUsername(username);
        Long currentUserId = user.getUserId();

        // 2) Normalize page and size
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 20 : size;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // 3) Load only conversations visible to this user (not soft-deleted)
        Page<Conversation> conversations =
                conversationRepository.findVisibleConversations(currentUserId, pageable);

        // 4) Map to DTO
        Page<ConversationResponse> dtoPage = conversations.map(conv -> {

            // Determine the other participant
            User other = conv.getSeller().getUserId().equals(currentUserId)
                    ? conv.getBuyer()
                    : conv.getSeller();

            // Determine unread count for current user
            int unread = conv.getSeller().getUserId().equals(currentUserId)
                    ? conv.getSellerUnreadCount()
                    : conv.getBuyerUnreadCount();

            // Fetch the latest (last) message, may be empty if no messages
            Page<Message> lastMessagePage = messageRepository.findLatestMessages(
                    conv.getPublicId(),
                    PageRequest.of(0, 1)
            );

            MessageResponse lastMessageDto = lastMessagePage
                    .stream()
                    .findFirst()
                    .map(MessageResponse::from)
                    .orElse(null);

            return ConversationResponse.from(
                    conv,
                    UserResponse.from(other),
                    lastMessageDto,
                    unread
            );
        });

        return PageResponse.fromPage(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findByListingAndUsers(
            String listingPublicId,
            String username1,
            String username2
    ) {
        Optional<Conversation> conv = conversationRepository
                .findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                        listingPublicId, username1, username2
                );

        if (conv.isPresent()) {
            return conv;
        }

        return conversationRepository
                .findByListing_PublicIdAndBuyer_UsernameAndSeller_Username(
                        listingPublicId, username2, username1
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadConversationCount(String username) {

        authValidator.validateUserByUsername(username);
        return conversationRepository.countUnreadByUsername(username);
    }

    @Override
    @Transactional
    public void leaveConversation(String conversationPublicId, String username) {

        User user = authValidator.validateUserByUsername(username);
        Conversation conversation = conversationValidator.validateConversation(conversationPublicId);
        conversationValidator.validateParticipant(conversation, user);

        if (conversation.getSeller().getUserId().equals(user.getUserId())) {
            conversation.setSellerDeletedAt(Instant.now());
            conversation.resetUnreadCountForSeller();
        } else if (conversation.getBuyer().getUserId().equals(user.getUserId())) {
            conversation.setBuyerDeletedAt(Instant.now());
            conversation.resetUnreadCountForBuyer();
        }

        conversationRepository.save(conversation);

        // Optionally hard delete when both sides left
        // if (conversation.getSellerDeletedAt() != null && conversation.getBuyerDeletedAt() != null) {
        //     conversationRepository.delete(conversation);
        // }
    }
}
