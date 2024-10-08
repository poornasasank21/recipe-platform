package com.recipe.recipeservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewDTO {

    private Long recipeId;
    private Integer rating; // Optional
    private String description; // Optional
    private Long userId; // Optional if fetched from JWT
    private Long reviewId; // Optional since userId and recipeId are a composite key

}
