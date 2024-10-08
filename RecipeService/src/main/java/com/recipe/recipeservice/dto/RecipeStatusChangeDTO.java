package com.recipe.recipeservice.dto;

import com.recipe.recipeservice.entity.Category;
import com.recipe.recipeservice.entity.Cuisine;
import com.recipe.recipeservice.entity.Status;
import lombok.Data;

@Data
public class RecipeStatusChangeDTO {
    private Long id;
    private Long user;
    private String email;
    private String name;
    private Category category;
    private Cuisine cuisine;
    private Status status;
}
