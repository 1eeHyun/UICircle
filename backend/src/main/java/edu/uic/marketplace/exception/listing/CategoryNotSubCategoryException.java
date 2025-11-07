package edu.uic.marketplace.exception.listing;

import edu.uic.marketplace.exception.CustomException;
import lombok.Getter;

@Getter
public class CategoryNotSubCategoryException extends CustomException {

    public CategoryNotSubCategoryException(String message) {
        super(message , 400);
    }
}
