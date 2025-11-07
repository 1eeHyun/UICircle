package edu.uic.marketplace.exception.listing;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class CategoryNotFoundException extends CustomException {

    public CategoryNotFoundException(String message) {
        super(message, 401);
    }
}
