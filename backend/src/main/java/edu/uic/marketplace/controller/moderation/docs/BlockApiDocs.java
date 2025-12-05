package edu.uic.marketplace.controller.moderation.docs;

import edu.uic.marketplace.dto.response.common.CommonResponse;
import edu.uic.marketplace.dto.response.moderation.BlockResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
        name = "Block API",
        description = "APIs for blocking/unblocking users and checking block status."
)
public interface BlockApiDocs {

    @Operation(
            summary = "Block a user",
            description = """
                    Blocks a specific user. 
                    Once blocked, the target user cannot interact with the blocker (posts, comments, messages, etc.).
                    """
    )
    ResponseEntity<CommonResponse<BlockResponse>> blockUser(
            @Parameter(description = "The username of the user to be blocked")
            String blockedUsername
    );

    @Operation(
            summary = "Unblock a user",
            description = "Cancels the block relationship with the specified user."
    )
    ResponseEntity<CommonResponse<Void>> unblockUser(
            @Parameter(description = "The username of the user to be unblocked")
            String blockedUsername
    );

    @Operation(
            summary = "Get users I blocked",
            description = "Returns a list of users that the given user has blocked."
    )
    ResponseEntity<CommonResponse<List<BlockResponse>>> getBlockedUsers();

    @Operation(
            summary = "Check block status",
            description = "Checks if the blocker has blocked the target user."
    )
    ResponseEntity<CommonResponse<Boolean>> isBlocked(
            @Parameter(description = "The username of the potentially blocked user")
            String blockedUsername
    );

//    @Operation(
//            summary = "Get all block-related usernames",
//            description = """
//                    Returns a combined list of usernames that are block-related:
//                    - Users I blocked
//                    - Users who blocked me
//
//                    Duplicate usernames will be removed.
//                    """
//    )
//    ResponseEntity<CommonResponse<List<String>>> getAllBlockRelatedUsernames(
//            @Parameter(description = "The username for which to gather block-related users")
//            String username
//    );
}
