package edu.uic.marketplace.exception.listing;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class ListingNotFoundException extends CustomException {

    public ListingNotFoundException() {
        super("Listing not found", 401);
    }
}
