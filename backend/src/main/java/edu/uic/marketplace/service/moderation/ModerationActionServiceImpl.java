package edu.uic.marketplace.service.moderation;

import edu.uic.marketplace.dto.request.moderation.ModerationActionRequest;
import edu.uic.marketplace.dto.response.common.PageResponse;
import edu.uic.marketplace.dto.response.moderation.ModerationActionResponse;
import edu.uic.marketplace.model.moderation.ModerationAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModerationActionServiceImpl implements ModerationActionService {

    @Override
    public ModerationActionResponse createAction(Long adminId, ModerationActionRequest request) {
        return null;
    }

    @Override
    public Optional<ModerationAction> findById(Long actionId) {
        return Optional.empty();
    }

    @Override
    public PageResponse<ModerationActionResponse> getAllActions(Integer page, Integer size) {
        return null;
    }

    @Override
    public PageResponse<ModerationActionResponse> getActionsByAdmin(Long adminId, Integer page, Integer size) {
        return null;
    }

    @Override
    public List<ModerationActionResponse> getActionsForTarget(String targetType, Long targetId) {
        return null;
    }

    @Override
    public ModerationActionResponse suspendUser(Long adminId, Long userId, String reason) {
        return null;
    }

    @Override
    public ModerationActionResponse deleteListing(Long adminId, Long listingId, String reason) {
        return null;
    }

    @Override
    public ModerationActionResponse warnUser(Long adminId, Long userId, String reason) {
        return null;
    }

    @Override
    public Map<String, Long> getModerationStatistics() {
        return null;
    }
}
