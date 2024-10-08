package com.recipe.recipeservice.controller;

import com.recipe.recipeservice.dto.ViewRecipeDTO;
import com.recipe.recipeservice.exception.ResourceNotFoundException;
import com.recipe.recipeservice.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewRecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRecipe_Success() throws ResourceNotFoundException {
        Long recipeId = 1L;
        ViewRecipeDTO mockRecipe = new ViewRecipeDTO(
                "Spaghetti Carbonara",
                "Spaghetti, Eggs, Pancetta",
                "Classic Italian pasta dish",
                30,
                "Italian",
                "Non-Veg",
                List.of("Dinner", "Easy"),
                "EASY",
                "None",
                "1"
        );

        when(recipeService.getRecipe(recipeId)).thenReturn(mockRecipe);
        ResponseEntity<ViewRecipeDTO> response = recipeController.getRecipe(recipeId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecipe, response.getBody());
        verify(recipeService, times(1)).getRecipe(recipeId);
    }

    @Test
    void getRecipe_ThrowsResourceNotFoundException() throws ResourceNotFoundException {
        Long invalidRecipeId = 999L;
        when(recipeService.getRecipe(invalidRecipeId)).thenThrow(new ResourceNotFoundException("Recipe not found"));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            recipeController.getRecipe(invalidRecipeId);
        });
        assertEquals("Recipe not found", exception.getMessage());
        verify(recipeService, times(1)).getRecipe(invalidRecipeId);
    }
}
