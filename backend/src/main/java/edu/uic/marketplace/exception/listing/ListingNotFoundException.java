package edu.uic.marketplace.exception.listing;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class ListingNotFoundException extends CustomException {

    public ListingNotFoundException(String message) {
        super(message, 401);
    }
}
