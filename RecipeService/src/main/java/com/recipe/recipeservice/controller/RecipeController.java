package com.recipe.recipeservice.controller;

import com.recipe.recipeservice.constants.ControllerConstants;
import com.recipe.recipeservice.constants.ErrorConstants;
import com.recipe.recipeservice.dto.CuisineDTO;
import com.recipe.recipeservice.dto.CuisineResponse;
import com.recipe.recipeservice.dto.RecipeListDTO;
import com.recipe.recipeservice.dto.RecipeDTO;
import com.recipe.recipeservice.dto.ApiResponse;
import com.recipe.recipeservice.dto.FavouritesRecipeResponse;
import com.recipe.recipeservice.entity.Category;
import com.recipe.recipeservice.entity.Tag;
import com.recipe.recipeservice.exception.DuplicateResourceException;
import com.recipe.recipeservice.exception.InvalidInputException;
import com.recipe.recipeservice.exception.ResourceNotFoundException;
import com.recipe.recipeservice.service.CuisineService;
import com.recipe.recipeservice.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.recipe.recipeservice.dto.ViewRecipeDTO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final CuisineService cuisineService;
    @GetMapping("/cuisines/enabled")
    public ResponseEntity<CuisineResponse> getEnabledCuisines() {
        List<CuisineDTO> cuisineDTOs = cuisineService.getEnabledCuisines();
        CuisineResponse response = CuisineResponse.builder()
                .cuisines(cuisineDTOs)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(recipeService.getAllCategories());
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(recipeService.getAllTags());
    }

    @GetMapping("/search")
    public ResponseEntity<RecipeListDTO> searchRecipes(@RequestParam String keyword) {
        keyword = keyword.trim().replaceAll("\\s+", " ");
        List<RecipeDTO> recipes = recipeService.searchRecipes(keyword);
        RecipeListDTO response = RecipeListDTO.builder()
                .recipeList(recipes)
                .status(HttpStatus.OK.toString())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<RecipeListDTO> fetchAllRecipesByFilters(
            @RequestParam(required = false) Long cuisineId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer cookingTime,
            @RequestParam(required = false) String difficulty) throws InvalidInputException {
        if((cuisineId!=null && cuisineId<0) || (categoryId!=null && categoryId<0) || (cookingTime!=null && cookingTime<0))
            throw new InvalidInputException(ErrorConstants.INVALID_INPUTS);
        RecipeListDTO recipesFiltered = RecipeListDTO.builder()
                .recipeList(recipeService.fetchRecipesByFilters(cuisineId, categoryId, cookingTime, difficulty))
                .status(HttpStatus.OK.toString())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(recipesFiltered);
    }
    @GetMapping("{id}"+ ControllerConstants.VIEW_RECIPE_PATH)
    public ResponseEntity<ViewRecipeDTO> getRecipe(@PathVariable Long id) throws ResourceNotFoundException {
        ViewRecipeDTO recipe= recipeService.getRecipe(id);
        return ResponseEntity.ok(recipe);
    }
    @GetMapping("image/{id}")
    public ResponseEntity<?> getProfileImage(@PathVariable Long id) throws IOException, ResourceNotFoundException {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(recipeService.getRecipeProfileImage(id));
    }
    @PostMapping("/favourites/{userId}/add")
    public ResponseEntity<ApiResponse> addFavoriteRecipe(@PathVariable String userId, @RequestBody Long recipeId) throws ResourceNotFoundException, DuplicateResourceException {
        return ResponseEntity.ok(recipeService.addFavoriteRecipe(userId, recipeId));
    }

    @DeleteMapping("/favourites/{userId}/delete")
    public ResponseEntity<ApiResponse> deleteFavoriteRecipe(@PathVariable String userId, @RequestBody Long recipeId) throws ResourceNotFoundException {
        return ResponseEntity.ok(recipeService.deleteFavoriteRecipe(userId, recipeId));
    }

    @GetMapping("/favourites/{userId}")
    public ResponseEntity<FavouritesRecipeResponse> getFavoriteRecipes(@PathVariable String userId) throws ResourceNotFoundException {
        FavouritesRecipeResponse favouritesResponse = recipeService.getFavoriteRecipes(userId);
        return ResponseEntity.ok(favouritesResponse);
    }
}
