package com.recipe.recipeservice.dto;

import com.recipe.recipeservice.entity.DifficultyLevel;
import com.recipe.recipeservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String name;
    private String ingredients;
    private String description;
    private CategoryDTO category;
    private CuisineDTO cuisine;
    private Long userId;
    private int cookingTime;
    private String imageUrl;
    private List<TagDTO> tags;
    private DifficultyLevel difficultyLevel;
    private Status status;
}