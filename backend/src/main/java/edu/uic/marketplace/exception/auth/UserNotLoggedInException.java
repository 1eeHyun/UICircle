package edu.uic.marketplace.exception.auth;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotLoggedInException extends CustomException {

    public UserNotLoggedInException() {
        super("User not logged-in", 401);
    }
}
