package edu.uic.marketplace.exception.listing;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotSellerException extends CustomException {

    public UserNotSellerException() {
        super("User is not the seller of this listing", 403);
    }
}
