package com.recipe.recipeservice.dto;

import com.recipe.recipeservice.entity.DifficultyLevel;
import com.recipe.recipeservice.entity.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecipeDTO {
    private Long id;
    private String name;
    private String ingredients;
    private String description;
    private String cookingTime;
    private String cuisine;
    private String category;
    private List<String> tags;
    private MultipartFile file;
    private String difficultyLevel;
    private String userId;
}
