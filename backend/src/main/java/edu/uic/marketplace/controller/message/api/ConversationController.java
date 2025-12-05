package edu.uic.marketplace.controller.message.api;

import edu.uic.marketplace.controller.message.docs.ConversationApiDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.message.ConversationResponse;
import edu.uic.marketplace.service.message.ConversationService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationController implements ConversationApiDocs {

    private final ConversationService conversationService;
    private final AuthValidator authValidator;

//    @Override
//    @PostMapping
//    public ResponseEntity<CommonResponse<ConversationResponse>> createConversation(
//            @RequestBody CreateConversationRequest request) {
//
//        String username = authValidator.extractUsername();
//
//        ConversationResponse res = conversationService.createConversation(username, request);
//
//        return ResponseEntity.ok(CommonResponse.success(res));
//    }

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<ConversationResponse>>> getConversations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        String username = authValidator.extractUsername();

        PageResponse<ConversationResponse> res = conversationService.getConversations(username, page, size);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/{conversationId}")
    public ResponseEntity<CommonResponse<ConversationResponse>> getConversation(
            @PathVariable("conversationId") String conversationId) {

        String username = authValidator.extractUsername();

        ConversationResponse res = conversationService.getConversation(conversationId, username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/unread-count")
    public ResponseEntity<CommonResponse<Long>> getUnreadConversationCount() {

        String username = authValidator.extractUsername();

        Long res = conversationService.getUnreadConversationCount(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @PostMapping("/{conversationId}/leave")
    public ResponseEntity<CommonResponse<Void>> leaveConversation(
            @PathVariable("conversationId") String conversationId) {

        String username = authValidator.extractUsername();

        conversationService.leaveConversation(conversationId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }
}
