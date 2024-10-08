package com.recipe.recipeservice.service;


import com.recipe.recipeservice.dto.*;
import com.recipe.recipeservice.exception.DuplicateResourceException;
import com.recipe.recipeservice.entity.Category;
import com.recipe.recipeservice.entity.Cuisine;
import com.recipe.recipeservice.entity.Recipe;
import com.recipe.recipeservice.entity.Tag;
import com.recipe.recipeservice.exception.IdNotFoundException;
import com.recipe.recipeservice.exception.InvalidInputException;
import com.recipe.recipeservice.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface RecipeService {

    public List<Tag> getAllTags();
    public Tag createTag(Tag tag);
    public List<Category> getAllCategories();
    public Category createCategory(Category category);
    public Cuisine createCuisine(Cuisine cuisine);
    public Recipe createRecipe(AddRecipeDTO addRecipeDTO) throws InvalidInputException, IOException, MethodArgumentNotValidException;
    public Recipe mapRecipeDTOtoRecipe(AddRecipeDTO addRecipeDTO) throws IOException, InvalidInputException;
    public String uploadImage(String path, MultipartFile file) throws IOException, NullPointerException, InvalidInputException;
    ViewRecipeDTO getRecipe(Long id) throws ResourceNotFoundException;
    public byte[] getRecipeProfileImage(Long userId) throws IOException, ResourceNotFoundException;
    public List<RecipeDTO> fetchRecipesByFilters(Long cuisineId, Long categoryId, Integer cookingTime, String difficulty) throws InvalidInputException;
    void updateRecipe(UpdateRecipeDTO updateRecipeDTO, Long id) throws IdNotFoundException,IOException;
    String updateRecipeImage(String path, MultipartFile file) throws IdNotFoundException, IOException;
    public List<RecipeDTO> searchRecipes(String keyword);
    ResponseEntity<SuccessResponse> editRecipeStatus(String id, String status) throws InvalidInputException;
    public RecipeFilterListDTO fetchAllRecipesByTwoFilters(Long cuisineId, Long categoryId) throws InvalidInputException;
    public CuisineFilterListDTO fetchAllCuisines();
    public CategoryFilterListDTO fetchAllCategory();
    public ApiResponse deleteRecipe(Long id) throws InvalidInputException;
    public String getRecipeOwnerId(Long recipeId) throws InvalidInputException;
    ApiResponse addFavoriteRecipe(String userId, Long recipeId) throws ResourceNotFoundException, DuplicateResourceException;
    ApiResponse deleteFavoriteRecipe(String userId, Long recipeId) throws ResourceNotFoundException;
    FavouritesRecipeResponse getFavoriteRecipes(String userId) throws ResourceNotFoundException;
}

