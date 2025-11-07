package edu.uic.marketplace.exception.auth;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotLoggedInException extends CustomException {

    public UserNotLoggedInException(String message) {
        super(message, 401);
    }
}
