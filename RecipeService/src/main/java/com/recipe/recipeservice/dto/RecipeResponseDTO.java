package com.recipe.recipeservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecipeResponseDTO {
    private Long id;
    private String name;
    private String ingredients;
    private String description;
    private String category;
    private String cuisine;
    private int cookingTime;
    private String imageUrl;
    private List<String> tags;
    private String difficultyLevel;
    private String status;
    private String dietaryRestrictions;
}

