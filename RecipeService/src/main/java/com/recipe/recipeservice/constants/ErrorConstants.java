package com.recipe.recipeservice.constants;

public class ErrorConstants {

    public static final String INVALID_FILE_TYPE = "Invalid file type. Only PNG, JPG, JPEG, and SVG are allowed.";
    public static final String INVALID_FILE_NAME="File must have a valid name.";
    public static final String RECIPE_ID_NOT_FOUND="No Recipe found with ID";
    public static final String FILE_MUST_NOT_BE_EMPTY = "File must not be empty";
    public static final String FILE_NAME_MUST_NOT_BE_NULL = "File name must not be null";
    public static final String INVALID_INPUTS = "Provided inputs are not valid";
    public static final String INVALID_RECIPE_STATUS = "Provided Recipe Status is invalid";
    public static final String INVALID_RECIPE_ID_FORMAT = "Provided recipe id is not in correct format";
    public static final String RECIPE_STATUS_UPDATED = "Successfully updated recipe status.";

    private ErrorConstants() {
        // Prevent instantiation
    }
}

