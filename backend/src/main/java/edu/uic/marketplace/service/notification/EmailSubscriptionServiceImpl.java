package edu.uic.marketplace.service.notification;

import edu.uic.marketplace.dto.request.notification.UpdateEmailSubscriptionRequest;
import edu.uic.marketplace.dto.response.notification.EmailSubscriptionResponse;
import edu.uic.marketplace.model.notification.EmailSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailSubscriptionServiceImpl implements EmailSubscriptionService {

    @Override
    public Optional<EmailSubscription> findByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public EmailSubscriptionResponse getEmailSubscription(Long userId) {
        return null;
    }

    @Override
    public EmailSubscriptionResponse updateEmailSubscription(Long userId, UpdateEmailSubscriptionRequest request) {
        return null;
    }

    @Override
    public EmailSubscription createDefaultSubscription(Long userId) {
        return null;
    }

    @Override
    public boolean isSubscribedToNewMessages(Long userId) {
        return false;
    }

    @Override
    public boolean isSubscribedToPriceChanges(Long userId) {
        return false;
    }

    @Override
    public boolean isSubscribedToOffers(Long userId) {
        return false;
    }

    @Override
    public boolean isSubscribedToListingStatus(Long userId) {
        return false;
    }
}
