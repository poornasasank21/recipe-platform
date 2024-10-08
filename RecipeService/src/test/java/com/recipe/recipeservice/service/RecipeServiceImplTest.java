package com.recipe.recipeservice.service;

import com.recipe.recipeservice.constants.ErrorConstants;
import com.recipe.recipeservice.dto.ApiResponse;
import com.recipe.recipeservice.dto.FavouritesRecipeResponse;
import com.recipe.recipeservice.dto.RecipeDTO;
import com.recipe.recipeservice.dto.ViewRecipeDTO;
import com.recipe.recipeservice.entity.*;
import com.recipe.recipeservice.exception.DuplicateResourceException;
import com.recipe.recipeservice.exception.InvalidInputException;
import com.recipe.recipeservice.exception.ResourceNotFoundException;
import com.recipe.recipeservice.repository.FavoritesRepository;
import com.recipe.recipeservice.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private FavoritesRepository favoritesRepository;
    @InjectMocks
    private RecipeServiceImpl recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRecipe_Success() throws ResourceNotFoundException {
        Long recipeId = 1L;
        Recipe mockRecipe = new Recipe();
        mockRecipe.setName("Spaghetti Carbonara");
        mockRecipe.setIngredients("Spaghetti, Eggs, Pancetta");
        mockRecipe.setDescription("Classic Italian pasta dish");
        mockRecipe.setCookingTime(30);
        mockRecipe.setImageUrl("http://example.com/carbonara.jpg");
        Category mockCategory = new Category();
        mockCategory.setName("Non-Veg");
        mockRecipe.setCategory(mockCategory);
        Cuisine mockCuisine = new Cuisine();
        mockCuisine.setName("Italian");
        mockRecipe.setCuisine(mockCuisine);
        Tag tag1 = new Tag();
        tag1.setName("Dinner");
        Tag tag2 = new Tag();
        tag2.setName("Easy");
        mockRecipe.setTags(List.of(tag1, tag2));
        mockRecipe.setDifficultyLevel(DifficultyLevel.EASY);
        mockRecipe.setDietaryRestrictions("None");
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));
        ViewRecipeDTO result = recipeService.getRecipe(recipeId);
        assertNotNull(result);
        assertEquals("Spaghetti Carbonara", result.getName());
        assertEquals("Spaghetti, Eggs, Pancetta", result.getIngredients());
        assertEquals("Classic Italian pasta dish", result.getDescription());
        assertEquals(30, result.getCookingTime());
        assertEquals("Italian", result.getCuisine());
        assertEquals("Non-Veg", result.getCategory());
        assertEquals(List.of("Dinner", "Easy"), result.getTags());
        assertEquals("EASY", result.getDifficultyLevel());
        verify(recipeRepository, times(1)).findById(recipeId);
    }

    @Test
    void getRecipe_ThrowsResourceNotFoundException() {
        Long invalidRecipeId = 999L;
        when(recipeRepository.findById(invalidRecipeId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> recipeService.getRecipe(invalidRecipeId));
        assertEquals(ErrorConstants.RECIPE_ID_NOT_FOUND + " " + invalidRecipeId, exception.getMessage());
        verify(recipeRepository, times(1)).findById(invalidRecipeId);
    }

    @Test
    void testSearchRecipes_ReturnsRecipeDTOs() {
        String keyword = "chicken";
        List<Recipe> recipes = List.of(new Recipe());
        when(recipeRepository.findByKeyword(keyword)).thenReturn(recipes);
        when(modelMapper.map(any(Recipe.class), eq(RecipeDTO.class))).thenReturn(new RecipeDTO());
        List<RecipeDTO> result = recipeService.searchRecipes(keyword);
        assertFalse(result.isEmpty());
        verify(recipeRepository).findByKeyword(keyword);
        verify(modelMapper, times(recipes.size())).map(any(Recipe.class), eq(RecipeDTO.class));
    }

    @Test
    void testFetchRecipesByFilters_ValidInputs_ReturnsRecipes() throws InvalidInputException {
        Long cuisineId = 1L;
        Long categoryId = 2L;
        Integer cookingTime = 30;
        String difficulty = "EASY";
        List<Recipe> recipes = Collections.singletonList(new Recipe());
        when(recipeRepository.findRecipesByFilters(anyLong(), anyLong(), any(), any())).thenReturn(recipes);
        when(modelMapper.map(any(), any())).thenReturn(new RecipeDTO());
        List<RecipeDTO> result = recipeService.fetchRecipesByFilters(cuisineId, categoryId, cookingTime, difficulty);
        assertEquals(1, result.size());
        verify(recipeRepository).findRecipesByFilters(cuisineId, categoryId, cookingTime, DifficultyLevel.fromString(difficulty));
    }


    @Test
    void addFavoriteRecipe_Success() throws ResourceNotFoundException, DuplicateResourceException {
        String userId = "1";
        Long recipeId = 123L;
        Favourites favourites = new Favourites(null, userId, new ArrayList<>());
        Recipe recipe = new Recipe();
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.of(favourites));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        ApiResponse response = recipeService.addFavoriteRecipe(userId, recipeId);
        assertEquals("Recipe added to favorites successfully.", response.getResponse());
        verify(favoritesRepository, times(1)).save(favourites);
    }

    @Test
    void addFavoriteRecipe_RecipeAlreadyInFavorites() throws ResourceNotFoundException, DuplicateResourceException {
        String userId = "1";
        Long recipeId = 123L;
        Recipe recipe = new Recipe();
        Favourites favourites = new Favourites(null, userId, new ArrayList<>(Collections.singletonList(recipe)));
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.of(favourites));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            recipeService.addFavoriteRecipe(userId, recipeId);
        });
        assertEquals("Recipe is already in favorites", exception.getMessage());
        verify(favoritesRepository, never()).save(favourites);
    }

    @Test
    void addFavoriteRecipe_RecipeNotFound() {
        String userId = "1";
        Long recipeId = 123L;
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.of(new Favourites()));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.addFavoriteRecipe(userId, recipeId);
        });
        assertEquals("Recipe not found", exception.getMessage());
    }

    @Test
    void deleteFavoriteRecipe_Success() throws ResourceNotFoundException {
        String userId = "1";
        Long recipeId = 123L;
        Recipe recipe = new Recipe();
        Favourites favourites = new Favourites(null, userId, new ArrayList<>(Collections.singletonList(recipe)));
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.of(favourites));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        ApiResponse response = recipeService.deleteFavoriteRecipe(userId, recipeId);
        assertEquals("Recipe removed from favorites successfully.", response.getResponse());
        verify(favoritesRepository, times(1)).save(favourites);
    }

    @Test
    void deleteFavoriteRecipe_RecipeNotInFavorites() throws ResourceNotFoundException {
        String userId = "1";
        Long recipeId = 123L;
        Favourites favourites = new Favourites(null, userId, new ArrayList<>());
        Recipe recipe = new Recipe();
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.of(favourites));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.deleteFavoriteRecipe(userId, recipeId);
        });
        assertEquals("Recipe not found in user's favorites", exception.getMessage());
        verify(favoritesRepository, never()).save(favourites);
    }

    @Test
    void deleteFavoriteRecipe_RecipeNotFound() {
        String userId = "1";
        Long recipeId = 123L;
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.of(new Favourites()));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.deleteFavoriteRecipe(userId, recipeId);
        });
        assertEquals("Recipe not found", exception.getMessage());
    }

    @Test
    void getFavoriteRecipes_Success() throws ResourceNotFoundException {
        String userId = "1";
        Favourites favourites = new Favourites(null, userId, new ArrayList<>());
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.of(favourites));
        FavouritesRecipeResponse response = recipeService.getFavoriteRecipes(userId);
        assertEquals(0, response.getRecipes().size());
        verify(favoritesRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getFavoriteRecipes_FavoritesNotFound() {
        String userId = "1";
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.getFavoriteRecipes(userId);
        });
        assertEquals("Favorites not found for user", exception.getMessage());
    }

    @Test
    void getOrCreateFavourites_NewFavourites() {
        String userId = "1";
        when(favoritesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        Favourites favourites = recipeService.getOrCreateFavourites(userId);
        assertNotNull(favourites);
        assertEquals(userId, favourites.getUserId());
        assertTrue(favourites.getFavoriteRecipes().isEmpty());
    }
}
