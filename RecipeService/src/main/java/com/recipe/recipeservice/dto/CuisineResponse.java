package com.recipe.recipeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
@AllArgsConstructor

public class CuisineResponse {
    List<CuisineDTO> cuisines;
}
