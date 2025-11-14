package edu.uic.marketplace.exception.search;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class SavedSearchNotFound extends CustomException {

    public SavedSearchNotFound(String message) {
        super(message, 404);
    }
}
