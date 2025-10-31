package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.response.moderation.BlockResponse;
import edu.uic.marketplace.model.moderation.Block;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    @Override
    public BlockResponse blockUser(Long blockerId, Long blockedId) {
        return null;
    }

    @Override
    public void unblockUser(Long blockerId, Long blockedId) {

    }

    @Override
    public Optional<Block> findByBlockerAndBlocked(Long blockerId, Long blockedId) {
        return Optional.empty();
    }

    @Override
    public List<BlockResponse> getBlockedUsers(Long userId) {
        return null;
    }

    @Override
    public List<BlockResponse> getBlockers(Long userId) {
        return null;
    }

    @Override
    public boolean isBlocked(Long blockerId, Long blockedId) {
        return false;
    }

    @Override
    public boolean hasBlockRelationship(Long userId1, Long userId2) {
        return false;
    }

    @Override
    public List<Long> getBlockedUserIds(Long userId) {
        return null;
    }
}
