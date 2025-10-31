package edu.uic.marketplace.service.user;

import edu.uic.marketplace.dto.response.user.BadgeResponse;
import edu.uic.marketplace.model.user.Badge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    @Override
    public List<Badge> findAll() {
        return null;
    }

    @Override
    public Optional<Badge> findByCode(String code) {
        return Optional.empty();
    }

    @Override
    public List<BadgeResponse> getUserBadges(Long userId) {
        return null;
    }

    @Override
    public BadgeResponse awardBadge(Long userId, String badgeCode) {
        return null;
    }

    @Override
    public boolean userHasBadge(Long userId, String badgeCode) {
        return false;
    }

    @Override
    public void checkAndAwardAutoBadges(Long userId) {

    }
}
