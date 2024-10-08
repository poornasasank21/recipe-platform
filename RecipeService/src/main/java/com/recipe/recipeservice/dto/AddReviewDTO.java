package com.recipe.recipeservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddReviewDTO {

    private Long recipeId;
    private Integer rating;
    private String description;
    private Long userId;

}
