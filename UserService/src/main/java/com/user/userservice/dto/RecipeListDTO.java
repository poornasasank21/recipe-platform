package com.user.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RecipeListDTO {
    private String timestamp;
    private String status;
    private List<RecipeStatusChangeDTO> recipeList;
}
