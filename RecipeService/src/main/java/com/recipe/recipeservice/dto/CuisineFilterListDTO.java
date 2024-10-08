package com.recipe.recipeservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.recipe.recipeservice.entity.Cuisine;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class CuisineFilterListDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String status;
    private List<Cuisine> cuisineList;
}
