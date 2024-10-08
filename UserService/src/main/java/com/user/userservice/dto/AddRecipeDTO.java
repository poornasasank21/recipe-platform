package com.user.userservice.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddRecipeDTO {

    @NotBlank(message = "Please Enter Valid name of the recipe")
    private String name;
    @NotBlank(message = "ingredients must not be blank or empty")
    private String ingredients;
    @NotBlank(message = "description must not be empty or null")
    private String description;
    @NotBlank(message = "category Id must not be empty or null")
    private String category;
    @NotBlank(message = "cuisine Id must not be empty or null")
    private String cuisine;
    @NotBlank(message = "cookingTime must not be empty or null")
    private String cookingTime;
    @JsonIgnore
    private MultipartFile file;
    private List<String> tagIds;
    @NotBlank(message = "difficultyLevel must not be empty or null")
    private String difficultyLevel;
    @NotBlank(message = "dietaryRestrictions must not be empty or null")
    private String dietaryRestrictions;
    @NotBlank(message = "UserId must not be empty or null")
    private String user;

}
