package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.response.moderation.BlockResponse;
import edu.uic.marketplace.model.moderation.Block;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.moderation.BlockRepository;
import edu.uic.marketplace.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final AuthValidator authValidator;
    private final BlockRepository blockRepository;

    @Override
    @Transactional
    public BlockResponse blockUser(String blockerUsername, String blockedUsername) {

        User blocker = authValidator.validateUserByUsername(blockerUsername);
        User blocked = authValidator.validateUserByUsername(blockedUsername);

        Optional<Block> found = blockRepository.findByBlockerIdAndBlockedId(blocker.getUserId(), blocked.getUserId());
        if (found.isPresent()) return BlockResponse.from(found.get());

        Block block = Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .blockedAt(Instant.now())
                .build();

        blockRepository.save(block);
        return BlockResponse.from(block);
    }

    @Override
    @Transactional
    public void unblockUser(String blockerUsername, String blockedUsername) {

        User blocker = authValidator.validateUserByUsername(blockerUsername);
        User blocked = authValidator.validateUserByUsername(blockedUsername);

        Optional<Block> found = blockRepository.findByBlockerIdAndBlockedId(blocker.getUserId(), blocked.getUserId());
        if (found.isEmpty()) return;

        blockRepository.deleteByBlockedId(found.get().getId().getBlockedId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Block> findByBlockerAndBlocked(String blockerUsername, String blockedUsername) {

        User blocker = authValidator.validateUserByUsername(blockerUsername);
        User blocked = authValidator.validateUserByUsername(blockedUsername);

        return blockRepository.findByBlockerIdAndBlockedId(blocker.getUserId(), blocked.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockResponse> getBlockedUsers(String username) {

        User user = authValidator.validateUserByUsername(username);
        List<Block> blocks = blockRepository.findByBlockerId(user.getUserId());

        return blocks.stream()
                .map(BlockResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlocked(String blockerUsername, String blockedUsername) {

        User blocker = authValidator.validateUserByUsername(blockerUsername);
        User blocked = authValidator.validateUserByUsername(blockedUsername);

        return blockRepository.existsByBlockerIdAndBlockedId(blocker.getUserId(), blocked.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllBlockRelatedUsernames(String username) {
        User user = authValidator.validateUserByUsername(username);

        // Get users I blocked
        List<String> blocked = blockRepository.findBlockedUsernamesByBlockerUsername(username);

        // Get users who blocked me
        List<String> blockers = blockRepository.findBlockerUsernamesByBlockedUsername(username);

        // Combine both lists and remove duplicates
        Set<String> allBlocked = new HashSet<>(blocked);
        allBlocked.addAll(blockers);

        return new ArrayList<>(allBlocked);
    }
}
