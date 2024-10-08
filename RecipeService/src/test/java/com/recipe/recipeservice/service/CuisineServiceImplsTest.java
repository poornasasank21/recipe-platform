package com.recipe.recipeservice.service;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import com.recipe.recipeservice.dto.CuisineDTO;
import com.recipe.recipeservice.entity.Cuisine;
import com.recipe.recipeservice.repository.CuisineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CuisineServiceImplsTest {

	@Mock
	private CuisineRepository cuisineRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private CuisineServiceImpl cuisineService;

	private Cuisine cuisine;
	private CuisineDTO cuisineDTO;

	@BeforeEach
	void setUp() {
		cuisine = new Cuisine();
		cuisine.setId(1L);
		cuisine.setName("Indian");
		cuisine.setEnabled(true);
		cuisine.setImageUrl("image_url");

		cuisineDTO = new CuisineDTO(1L, "Indian", true, "image_url");
	}

	@Test
	void addCuisine() {
		when(modelMapper.map(cuisineDTO, Cuisine.class)).thenReturn(cuisine);
		when(cuisineRepository.save(any(Cuisine.class))).thenReturn(cuisine);
		when(modelMapper.map(cuisine, CuisineDTO.class)).thenReturn(cuisineDTO);

		CuisineDTO savedCuisine = cuisineService.addCuisine(cuisineDTO);

		assertNotNull(savedCuisine);
		assertEquals(cuisineDTO.getName(), savedCuisine.getName());
		verify(cuisineRepository).save(cuisine);
	}

	@Test
	void disableCuisineById() {
		when(cuisineRepository.findById(1L)).thenReturn(Optional.of(cuisine));
		boolean result = cuisineService.disableCuisineById(1L);

		assertTrue(result);
		assertFalse(cuisine.isEnabled());
		verify(cuisineRepository).save(cuisine);
	}

	@Test
	void disableCuisineById_NotFound() {
		when(cuisineRepository.findById(1L)).thenReturn(Optional.empty());
		boolean result = cuisineService.disableCuisineById(1L);

		assertFalse(result);
		verify(cuisineRepository, never()).save(any());
	}

	@Test
	void enableCuisineById() {
		cuisine.setEnabled(false);
		when(cuisineRepository.findById(1L)).thenReturn(Optional.of(cuisine));
		boolean result = cuisineService.enableCuisineById(1L);

		assertTrue(result);
		assertTrue(cuisine.isEnabled());
		verify(cuisineRepository).save(cuisine);
	}

	@Test
	void enableCuisineById_NotFound() {
		when(cuisineRepository.findById(1L)).thenReturn(Optional.empty());
		boolean result = cuisineService.enableCuisineById(1L);

		assertFalse(result);
		verify(cuisineRepository, never()).save(any());
	}

	@Test
	void deleteCuisineById() {
		when(cuisineRepository.existsById(1L)).thenReturn(true);
		boolean result = cuisineService.deleteCuisineById(1L);

		assertTrue(result);
		verify(cuisineRepository).deleteById(1L);
	}

	@Test
	void deleteCuisineById_NotFound() {
		when(cuisineRepository.existsById(1L)).thenReturn(false);
		boolean result = cuisineService.deleteCuisineById(1L);

		assertFalse(result);
		verify(cuisineRepository, never()).deleteById(anyLong());
	}

	@Test
	void updateCuisineById() {
		when(cuisineRepository.findById(1L)).thenReturn(Optional.of(cuisine));
		CuisineDTO updatedCuisineDTO = new CuisineDTO(1L, "Chinese", true, "new_image_url");

		CuisineDTO result = cuisineService.updateCuisineById(1L, updatedCuisineDTO);

		assertNotNull(result);
		assertEquals("Chinese", cuisine.getName());
		assertEquals("new_image_url", cuisine.getImageUrl());
		verify(cuisineRepository).save(cuisine);
	}

	@Test
	void updateCuisineById_NotFound() {
		when(cuisineRepository.findById(1L)).thenReturn(Optional.empty());
		CuisineDTO result = cuisineService.updateCuisineById(1L, cuisineDTO);

		assertNull(result);
		verify(cuisineRepository, never()).save(any());
	}

	@Test
	void getEnabledCuisines() {
		when(cuisineRepository.findByIsEnabled(true)).thenReturn(Arrays.asList(cuisine));
		List<CuisineDTO> result = cuisineService.getEnabledCuisines();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		verify(cuisineRepository).findByIsEnabled(true);
	}

	@Test
	void getAllCuisines() {
		when(cuisineRepository.getAllCuisines()).thenReturn(Arrays.asList(cuisine));
		List<CuisineDTO> result = cuisineService.getAllCuisines();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		verify(cuisineRepository).getAllCuisines();
	}

	@Test
	void doesCuisineExistByName() {
		when(cuisineRepository.doesCuisineExistByName("Indian")).thenReturn(true);
		boolean result = cuisineService.doesCuisineExistByName("Indian");

		assertTrue(result);
		verify(cuisineRepository).doesCuisineExistByName("Indian");
	}

	@Test
	void doesCuisineExistById() {
		when(cuisineRepository.existsById(1L)).thenReturn(true);
		boolean result = cuisineService.doesCuisineExistById(1L);

		assertTrue(result);
		verify(cuisineRepository).existsById(1L);
	}

	@Test
	void isCuisineEnabled() {
		when(cuisineRepository.findById(1L)).thenReturn(Optional.of(cuisine));
		boolean result = cuisineService.isCuisineEnabled(1L);

		assertTrue(result);
		verify(cuisineRepository).findById(1L);
	}

	@Test
	void isCuisineEnabled_NotFound() {
		when(cuisineRepository.findById(1L)).thenReturn(Optional.empty());
		boolean result = cuisineService.isCuisineEnabled(1L);

		assertFalse(result);
		verify(cuisineRepository).findById(1L);
	}
}
