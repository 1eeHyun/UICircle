package edu.uic.marketplace.exception.auth;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotAuthorizedException extends CustomException {

    public UserNotAuthorizedException() {
        super("User not authorized", 401);
    }
}
