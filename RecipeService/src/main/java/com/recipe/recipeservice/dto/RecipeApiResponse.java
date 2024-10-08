package com.recipe.recipeservice.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class RecipeApiResponse {
    private String response;
    private  String UserId;
}

