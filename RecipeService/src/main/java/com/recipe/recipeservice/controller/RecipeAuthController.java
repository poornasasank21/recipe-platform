package com.recipe.recipeservice.controller;

import com.recipe.recipeservice.dto.RecipeApiResponse;
import com.recipe.recipeservice.dto.AddRecipeDTO;
import com.recipe.recipeservice.dto.ApiResponse;
import com.recipe.recipeservice.dto.SuccessResponse;
import com.recipe.recipeservice.dto.CategoryFilterListDTO;
import com.recipe.recipeservice.dto.RecipeFilterListDTO;
import com.recipe.recipeservice.dto.CuisineFilterListDTO;
import com.recipe.recipeservice.dto.UpdateRecipeDTO;
import com.recipe.recipeservice.entity.Tag;
import com.recipe.recipeservice.entity.Category;
import com.recipe.recipeservice.exception.IdNotFoundException;
import com.recipe.recipeservice.exception.InvalidInputException;
import com.recipe.recipeservice.repository.CuisineRepository;
import com.recipe.recipeservice.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/recipes")
@RequiredArgsConstructor
public class RecipeAuthController {
    private final RecipeService recipeService;
    private final CuisineRepository cuisineRepository;
    private final ModelMapper modelMapper;
    @Value("${project.image}")
    String path;
    @PostMapping(value ="/save", consumes = "multipart/form-data")
    public ResponseEntity<RecipeApiResponse> addRecipe(@ModelAttribute @Valid AddRecipeDTO addRecipeDto) throws InvalidInputException, IOException, MethodArgumentNotValidException {
        recipeService.createRecipe(addRecipeDto);
        RecipeApiResponse response = RecipeApiResponse.builder()
                .response("Successfully Added the Recipe")
                .UserId(addRecipeDto.getUser())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/tags")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(recipeService.createTag(tag));
    }
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(recipeService.getAllCategories());
    }
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(recipeService.createCategory(category));
    }

    @PutMapping("{id}/status/{status}")
    public ResponseEntity<SuccessResponse> editRecipeStatus(@PathVariable("id") String id, @PathVariable("status") String status) throws InvalidInputException {
        return recipeService.editRecipeStatus(id, status);
    }

    @GetMapping("recipes/cuisines")
    public ResponseEntity<CuisineFilterListDTO> fetchAllCuisines() {
        CuisineFilterListDTO cuisineFilterListDTO = recipeService.fetchAllCuisines();
        return ResponseEntity.ok(cuisineFilterListDTO);
    }

    @GetMapping("recipes/categories")
    public ResponseEntity<CategoryFilterListDTO> fetchAllCategory() {
        CategoryFilterListDTO categoryFilterListDTO = recipeService.fetchAllCategory();
        return ResponseEntity.ok(categoryFilterListDTO);
    }

    @GetMapping("admins/filter")
    public ResponseEntity<RecipeFilterListDTO> fetchAllRecipesByFilters(
            @RequestParam(required = false) Long cuisineId,
            @RequestParam(required = false) Long categoryId
    ) throws InvalidInputException {
        RecipeFilterListDTO recipeFilterListDTO = recipeService.fetchAllRecipesByTwoFilters(cuisineId, categoryId);

        return ResponseEntity.ok(recipeFilterListDTO);
    }
    @PutMapping(value="/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateRecipe(@ModelAttribute UpdateRecipeDTO recipeDTO) throws IdNotFoundException, IOException{
        Long id= recipeDTO.getId();
        recipeService.updateRecipe(recipeDTO,id);
        return "Recipe updated successfully";
    }
    @PutMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteRecipe(@PathVariable("id") Long id) throws InvalidInputException {
        ApiResponse response = recipeService.deleteRecipe(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("getUserIdByRecipeId/{id}")
    public ResponseEntity<String> getRecipeOwnerId(@PathVariable("id") Long recipeId) throws InvalidInputException {
        String ownerId = recipeService.getRecipeOwnerId(recipeId);
        return new ResponseEntity<>(ownerId, HttpStatus.OK);
    }

}
