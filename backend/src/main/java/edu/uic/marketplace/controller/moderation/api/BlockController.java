package edu.uic.marketplace.controller.moderation.api;

import edu.uic.marketplace.controller.moderation.docs.BlockApiDocs;
import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.moderation.BlockResponse;
import edu.uic.marketplace.service.moderation.BlockService;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/block")
public class BlockController implements BlockApiDocs {

    private final AuthValidator authValidator;
    private final BlockService blockService;

    @Override
    @PostMapping("/{blockedUsername}")
    public ResponseEntity<CommonResponse<BlockResponse>> blockUser(
            @PathVariable() String blockedUsername) {

        String blockerUsername = authValidator.extractUsername();
        BlockResponse res = blockService.blockUser(blockerUsername, blockedUsername);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @DeleteMapping("/{blockedUsername}")
    public ResponseEntity<CommonResponse<Void>> unblockUser(
            @PathVariable String blockedUsername) {

        String blockerUsername = authValidator.extractUsername();
        blockService.unblockUser(blockerUsername, blockedUsername);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<List<BlockResponse>>> getBlockedUsers() {

        String username = authValidator.extractUsername();
        List<BlockResponse> res = blockService.getBlockedUsers(username);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

    @Override
    @GetMapping("/{blockedUsername}/is-blocked")
    public ResponseEntity<CommonResponse<Boolean>> isBlocked(
            @PathVariable String blockedUsername) {

        String username = authValidator.extractUsername();
        boolean res = blockService.isBlocked(username, blockedUsername);

        return ResponseEntity.ok(CommonResponse.success(res));
    }

//    @Override
//    public ResponseEntity<CommonResponse<List<String>>> getAllBlockRelatedUsernames(String username) {
//
//        return ResponseEntity.ok(CommonResponse.success(res));
//    }
}
