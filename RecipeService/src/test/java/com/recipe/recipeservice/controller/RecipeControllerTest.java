package com.recipe.recipeservice.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.recipe.recipeservice.dto.CuisineDTO;
import com.recipe.recipeservice.dto.RecipeDTO;
import com.recipe.recipeservice.service.CuisineService;
import com.recipe.recipeservice.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RecipeService recipeService;
    @MockBean
    private CuisineService cuisineService;

    @Test
    public void testSearchRecipes_ReturnsRecipesSuccessfully() throws Exception {
        String keyword = "salad";
        List<RecipeDTO> recipeDTOs = List.of(new RecipeDTO());
        when(recipeService.searchRecipes(keyword)).thenReturn(recipeDTOs);
        mockMvc.perform(get("/recipes/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeList").exists())
                .andExpect(jsonPath("$.status").value("200 OK"));
        verify(recipeService).searchRecipes(keyword);
    }


    @Test
    void getEnabledCuisines_ReturnsCuisinesList() throws Exception {
        when(cuisineService.getEnabledCuisines()).thenReturn(List.of(new CuisineDTO()));
        this.mockMvc.perform(get("/recipes/cuisines/enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuisines").exists());
    }

    @Test
    void fetchAllRecipesByFilters_ReturnsRecipesList() throws Exception {
        when(recipeService.fetchRecipesByFilters(anyLong(), anyLong(), any(), any())).thenReturn(List.of(new RecipeDTO()));

        this.mockMvc.perform(get("/recipes/filter?cuisineId=1&categoryId=2&cookingTime=30&difficulty=EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeList").isNotEmpty());
    }

    @Test
    void fetchAllRecipesByFilters_InvalidInput_ThrowsException() throws Exception {
        this.mockMvc.perform(get("/recipes/filter?cuisineId=-1"))
                .andExpect(status().isBadRequest());
    }
}
