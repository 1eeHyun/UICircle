package edu.uic.marketplace.exception.listing;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class OfferNotFoundException extends CustomException {

    public OfferNotFoundException(String message) {
        super(message, 401);
    }
}
