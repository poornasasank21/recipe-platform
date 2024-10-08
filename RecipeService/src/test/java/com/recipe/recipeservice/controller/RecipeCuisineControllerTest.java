package com.recipe.recipeservice.controller;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import com.recipe.recipeservice.dto.CuisineDTO;
import com.recipe.recipeservice.service.CuisineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class RecipeCuisineControllerTest {
    @Mock
    private CuisineService cuisineService;
    @InjectMocks
    private RecipeCuisineController recipeCuisineController;
    private CuisineDTO cuisineDTO;
    private CuisineDTO updatedCuisineDTO;

    @BeforeEach
    public void setUp() {
        cuisineDTO = new CuisineDTO(1L, "Italian", true, "url");
        updatedCuisineDTO = new CuisineDTO(1L, "French", false, "new-url");
    }

    @Test
    void testAddCuisine_CreatesCuisine() {
        when(cuisineService.doesCuisineExistByName(anyString())).thenReturn(false);
        when(cuisineService.addCuisine(any(CuisineDTO.class))).thenReturn(cuisineDTO);
        ResponseEntity<CuisineDTO> response = recipeCuisineController.addCuisine(cuisineDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(cuisineDTO.getName(), response.getBody().getName());
    }

    @Test
    void testAddCuisine_ConflictIfExists() {
        when(cuisineService.doesCuisineExistByName(anyString())).thenReturn(true);
        ResponseEntity<CuisineDTO> response = recipeCuisineController.addCuisine(cuisineDTO);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }



    @Test
    void testDisableCuisine_DisablesCuisine() {
        when(cuisineService.disableCuisineById(anyLong())).thenReturn(true);
        ResponseEntity<Void> response = recipeCuisineController.disableCuisine(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDisableCuisine_NotFound() {
        when(cuisineService.disableCuisineById(anyLong())).thenReturn(false);
        ResponseEntity<Void> response = recipeCuisineController.disableCuisine(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteCuisine_DeletesCuisine() {
        when(cuisineService.deleteCuisineById(anyLong())).thenReturn(true);
        ResponseEntity<Void> response = recipeCuisineController.deleteCuisine(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteCuisine_NotFound() {
        when(cuisineService.deleteCuisineById(anyLong())).thenReturn(false);
        ResponseEntity<Void> response = recipeCuisineController.deleteCuisine(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testEnableCuisine_EnablesCuisine() {
        when(cuisineService.enableCuisineById(anyLong())).thenReturn(true);
        ResponseEntity<Void> response = recipeCuisineController.enableCuisine(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testEnableCuisine_NotFound() {
        when(cuisineService.enableCuisineById(anyLong())).thenReturn(false);
        ResponseEntity<Void> response = recipeCuisineController.enableCuisine(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateCuisine_UpdatesCuisine() {
        when(cuisineService.updateCuisineById(anyLong(), any(CuisineDTO.class))).thenReturn(updatedCuisineDTO);
        ResponseEntity<CuisineDTO> response = recipeCuisineController.updateCuisine(1L, updatedCuisineDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedCuisineDTO.getName(), response.getBody().getName());
    }

    @Test
    void testUpdateCuisine_NotFound() {
        when(cuisineService.updateCuisineById(anyLong(), any(CuisineDTO.class))).thenReturn(null);
        ResponseEntity<CuisineDTO> response = recipeCuisineController.updateCuisine(1L, updatedCuisineDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllCuisines_ReturnsList() {
        List<CuisineDTO> cuisineDTOs = List.of(cuisineDTO);
        when(cuisineService.getAllCuisines()).thenReturn(cuisineDTOs);
        ResponseEntity<List<CuisineDTO>> response = recipeCuisineController.getAllCuisines();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(cuisineDTO.getName(), response.getBody().get(0).getName());
    }

    @Test
    void testDoesCuisineExistByName_ReturnsTrue() {
        when(cuisineService.doesCuisineExistByName(anyString())).thenReturn(true);
        ResponseEntity<Boolean> response = recipeCuisineController.doesCuisineExistByName("Italian");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testDoesCuisineExistByName_ReturnsFalse() {
        when(cuisineService.doesCuisineExistByName(anyString())).thenReturn(false);
        ResponseEntity<Boolean> response = recipeCuisineController.doesCuisineExistByName("Non-Existent");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    void testDoesCuisineExistById_ReturnsTrue() {
        when(cuisineService.doesCuisineExistById(anyLong())).thenReturn(true);
        ResponseEntity<Boolean> response = recipeCuisineController.doesCuisineExistById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testDoesCuisineExistById_ReturnsFalse() {
        when(cuisineService.doesCuisineExistById(anyLong())).thenReturn(false);
        ResponseEntity<Boolean> response = recipeCuisineController.doesCuisineExistById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    void testIsCuisineEnabled_ReturnsTrue() {
        when(cuisineService.isCuisineEnabled(anyLong())).thenReturn(true);
        ResponseEntity<Boolean> response = recipeCuisineController.isCuisineEnabled(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testIsCuisineEnabled_ReturnsFalse() {
        when(cuisineService.isCuisineEnabled(anyLong())).thenReturn(false);
        ResponseEntity<Boolean> response = recipeCuisineController.isCuisineEnabled(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }
}