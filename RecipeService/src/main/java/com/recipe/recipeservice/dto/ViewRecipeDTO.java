package com.recipe.recipeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewRecipeDTO {
    private String name;
    private String ingredients;
    private String description;
    private int cookingTime;
    private String cuisine;
    private String category;
    private List<String> tags;
    private String difficultyLevel;
    private String dietaryRestrictions;
    private String userId;
}
