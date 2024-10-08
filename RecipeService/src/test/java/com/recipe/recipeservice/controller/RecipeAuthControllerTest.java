package com.recipe.recipeservice.controller;

import com.recipe.recipeservice.dto.UpdateRecipeDTO;
import com.recipe.recipeservice.exception.IdNotFoundException;
import com.recipe.recipeservice.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;


class RecipeAuthControllerTest {

    @InjectMocks
    private RecipeAuthController recipeAuthController;

    @Mock
    private RecipeService recipeService;

    private UpdateRecipeDTO recipeDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDTO = new UpdateRecipeDTO();
        recipeDTO.setId(1L);
    }

    @Test
    void updateRecipe_Success() throws IdNotFoundException, IOException {
        doNothing().when(recipeService).updateRecipe(recipeDTO, recipeDTO.getId());
        String response = recipeAuthController.updateRecipe(recipeDTO);
        assertEquals("Recipe updated successfully", response);
        verify(recipeService, times(1)).updateRecipe(recipeDTO, recipeDTO.getId());
    }

    @Test
    void updateRecipe_IdNotFoundException() throws IdNotFoundException, IOException {
        doThrow(new IdNotFoundException("ID not found")).when(recipeService).updateRecipe(recipeDTO, recipeDTO.getId());
        try {
            recipeAuthController.updateRecipe(recipeDTO);
        } catch (IdNotFoundException e) {
            assertEquals("ID not found", e.getMessage());
            verify(recipeService, times(1)).updateRecipe(recipeDTO, recipeDTO.getId());
        }
    }

    @Test
    void updateRecipe_IOException() throws IdNotFoundException, IOException {
        doThrow(new IOException("IO error occurred")).when(recipeService).updateRecipe(recipeDTO, recipeDTO.getId());
        try {
            recipeAuthController.updateRecipe(recipeDTO);
        } catch (IOException e) {
            assertEquals("IO error occurred", e.getMessage());
            verify(recipeService, times(1)).updateRecipe(recipeDTO, recipeDTO.getId());
        }
    }
}
