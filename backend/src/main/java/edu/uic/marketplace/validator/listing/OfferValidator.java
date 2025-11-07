package edu.uic.marketplace.validator.listing;

import edu.uic.marketplace.exception.auth.UserNotAuthorizedException;
import edu.uic.marketplace.exception.listing.OfferNotFoundException;
import edu.uic.marketplace.model.transaction.PriceOffer;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.PriceOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OfferValidator {

    private final PriceOfferRepository priceOfferRepository;

    public PriceOffer validatePriceOfferByPublicId(String publicId) {

        Optional<PriceOffer> found = priceOfferRepository.findByPublicId(publicId);

        if (found.isEmpty())
            throw new OfferNotFoundException("Offer does not exist.");

        return found.get();
    }

    public void validateOfferRightSeller(User user, User target) {

        if (user != target)
            throw new UserNotAuthorizedException("User does not have permission to access this resource");
    }
}
