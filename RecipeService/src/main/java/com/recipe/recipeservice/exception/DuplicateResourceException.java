package com.recipe.recipeservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends Exception {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
