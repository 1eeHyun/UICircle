package edu.uic.marketplace.exception.listing;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotSellerException extends CustomException {

    public UserNotSellerException(String message) {
        super(message, 403);
    }
}
