package edu.uic.marketplace.validator.listing;

import edu.uic.marketplace.exception.listing.ListingNotFoundException;
import edu.uic.marketplace.exception.listing.UserNotSellerException;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.CategoryRepository;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.repository.listing.PriceOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ListingValidator {

    private final ListingRepository listingRepository;
    private final CategoryRepository categoryRepository;
    private final PriceOfferRepository priceOfferRepository;

    public Listing validateListing(Long listingId) {

        Optional<Listing> listing = listingRepository.findById(listingId);

        if (!listing.isPresent())
            throw new ListingNotFoundException();

        return listing.get();
    }

    public void validateSellerOwnership(User user, User target) {

        if (user != target)
            throw new UserNotSellerException();
    }
}
