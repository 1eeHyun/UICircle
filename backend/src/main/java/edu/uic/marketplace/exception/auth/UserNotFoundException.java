package edu.uic.marketplace.exception.auth;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super("User not found", 401);
    }
}
