package com.recipe.recipeservice.dto;
import lombok.Builder;
import lombok.Data;
import lombok.AccessLevel;

import java.util.List;

@Data
@Builder
public class FavouritesRecipeResponse {
    private List<RecipeResponseDTO> recipes;
}

