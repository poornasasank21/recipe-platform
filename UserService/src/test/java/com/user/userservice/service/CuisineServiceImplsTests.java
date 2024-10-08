package com.user.userservice.service;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import com.user.userservice.dto.CuisineDTO;
import com.user.userservice.exception.CuisineIdNotFoundException;
import com.user.userservice.exception.DuplicateCuisineException;
import com.user.userservice.feignclient.RecipeServiceClient;
import com.user.userservice.service.implementation.CuisineServiceImpls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
class CuisineServiceImplsTests {

    @Mock
    private RecipeServiceClient recipeServiceClient;

    @InjectMocks
    private CuisineServiceImpls cuisineService;
    private final String path = "test/path";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        cuisineService = new CuisineServiceImpls(recipeServiceClient);
        setField(cuisineService, "path", path);
    }

    @Test
    void testGetAllCuisines() {
        List<CuisineDTO> cuisines = Arrays.asList(new CuisineDTO(1L, "Italian", true, "image.jpg"));
        when(recipeServiceClient.getAllCuisines()).thenReturn(cuisines);

        List<CuisineDTO> result = cuisineService.getAllCuisines();

        assertEquals(1, result.size());
        assertEquals("Italian", result.get(0).getName());
        verify(recipeServiceClient).getAllCuisines();
    }

    @Test
    void testAddCuisine_Success() {
        String name = "Italian";
        MultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[1]);
        when(recipeServiceClient.addCuisine(any(CuisineDTO.class))).thenReturn(new CuisineDTO(1L, name, true, "image.png"));
        when(recipeServiceClient.doesCuisineExistByName(name)).thenReturn(ResponseEntity.ok(false));

        CuisineDTO result = cuisineService.addCuisine(name, true, file);

        assertEquals(name, result.getName());
        assertEquals("image.png", result.getImageUrl());
        verify(recipeServiceClient).addCuisine(any(CuisineDTO.class));
    }

    @Test
    void testAddCuisine_DuplicateName() {
        String name = "Italian";
        when(recipeServiceClient.doesCuisineExistByName(name)).thenReturn(ResponseEntity.ok(true));

        MultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[1]);

        assertThrows(DuplicateCuisineException.class, () -> cuisineService.addCuisine(name, true, file));
    }

    @Test
    void testDeleteCuisine_Success() {
        Long id = 1L;
        when(recipeServiceClient.doesCuisineExistById(id)).thenReturn(ResponseEntity.ok(true));

        cuisineService.deleteCuisine(id);

        verify(recipeServiceClient).deleteCuisine(id);
    }

    @Test
    void testDeleteCuisine_NotFound() {
        Long id = 1L;
        when(recipeServiceClient.doesCuisineExistById(id)).thenReturn(ResponseEntity.ok(false));

        assertThrows(CuisineIdNotFoundException.class, () -> cuisineService.deleteCuisine(id));
    }

    @Test
    void testUpdateCuisine_Success() {
        Long id = 1L;
        String name = "Mexican";
        MultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[1]);

        when(recipeServiceClient.doesCuisineExistById(id)).thenReturn(ResponseEntity.ok(true));
        when(recipeServiceClient.updateCuisine(eq(id), any(CuisineDTO.class))).thenReturn(new CuisineDTO(id, name, true, "image.png"));

        CuisineDTO result = cuisineService.updateCuisine(id, name, true, file);

        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        verify(recipeServiceClient).updateCuisine(eq(id), any(CuisineDTO.class));
    }

    @Test
    void testToggleCuisineEnabled_DisableCuisine() {
        Long id = 1L;
        when(recipeServiceClient.doesCuisineExistById(id)).thenReturn(ResponseEntity.ok(true));
        when(recipeServiceClient.isCuisineEnabled(id)).thenReturn(true);

        String result = cuisineService.toggleCuisineEnabled(id);

        assertEquals("Cuisine disabled successfully", result);
        verify(recipeServiceClient).disableCuisine(id);
    }

    @Test
    void testToggleCuisineEnabled_EnableCuisine() {
        Long id = 1L;
        when(recipeServiceClient.doesCuisineExistById(id)).thenReturn(ResponseEntity.ok(true));
        when(recipeServiceClient.isCuisineEnabled(id)).thenReturn(false);

        String result = cuisineService.toggleCuisineEnabled(id);

        assertEquals("Cuisine enabled successfully", result);
        verify(recipeServiceClient).enableCuisine(id);
    }

    @Test
    void testSaveImage_Success() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new ByteArrayInputStream(new byte[10]));

        String result = cuisineService.saveImage(file);

        assertNotNull(result);
        assertTrue(result.endsWith(".png"));
        assertTrue(result.length() > 10);
    }

    @Test
    void testSaveImage_InvalidFileType() {
        MultipartFile file = new MockMultipartFile("file", "document.txt", "text/plain", new byte[1]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cuisineService.saveImage(file));
        assertEquals("Invalid file type. Only PNG, JPG, JPEG, and SVG are allowed.", exception.getMessage());
    }

}
