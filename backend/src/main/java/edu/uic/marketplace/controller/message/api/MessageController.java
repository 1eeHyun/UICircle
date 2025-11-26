package edu.uic.marketplace.controller.message.api;

import edu.uic.marketplace.controller.message.docs.MessageApiDocs;
import edu.uic.marketplace.dto.request.message.SendMessageRequest;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.MessageResponse;
import edu.uic.marketplace.service.message.MessageService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApiDocs {

    private final MessageService messageService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping("/{conversationId}")
    public ResponseEntity<CommonResponse<MessageResponse>> sendMessage(
            @RequestParam(name = "conversationId", required = true) String conversationId,
            @RequestBody SendMessageRequest request) {

        String username = authValidator.extractUsername();

        MessageResponse res = messageService.sendMessage(conversationId, username, request);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/{conversationId}")
    public ResponseEntity<CommonResponse<PageResponse<MessageResponse>>> getMessages(
            @RequestParam(name = "conversationId", required = true) String conversationId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10")int size) {

        String username = authValidator.extractUsername();

        PageResponse<MessageResponse> res = messageService.getMessages(conversationId, username, page, size);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/{conversationId}/unread-count")
    public ResponseEntity<CommonResponse<Long>> getUnreadCountInConversation(
            @RequestParam(name = "conversationId", required = true) String conversationId) {

        String username = authValidator.extractUsername();

        Long res = messageService.getUnreadCountInConversation(conversationId, username);
        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PostMapping("/{conversationId}/read")
    public ResponseEntity<CommonResponse<Void>> markConversationAsRead(
            String conversationId) {

        String username = authValidator.extractUsername();

        messageService.markConversationAsRead(conversationId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PostMapping("/{conversationId}/read-all")
    public ResponseEntity<CommonResponse<Void>> markMessageAsRead(
            @RequestParam(name = "conversationId", required = true) String conversationId,
            @RequestParam(name = "messageId", required = true) String messageId) {

        String username = authValidator.extractUsername();
        messageService.markAsRead(messageId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @DeleteMapping("/{conversationId}/{messageId}")
    public ResponseEntity<CommonResponse<Void>> deleteMessage(
            @RequestParam(name = "conversationId", required = true) String conversationId,
            @RequestParam(name = "messageId", required = true) String messageId) {

        String username = authValidator.extractUsername();
        messageService.deleteMessage(messageId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }
}
