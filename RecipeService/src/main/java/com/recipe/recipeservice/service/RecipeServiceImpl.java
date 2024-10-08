package com.recipe.recipeservice.service;

import com.recipe.recipeservice.constants.ErrorConstants;
import com.recipe.recipeservice.dto.*;
import com.recipe.recipeservice.entity.Recipe;
import com.recipe.recipeservice.entity.Tag;
import com.recipe.recipeservice.entity.Category;
import com.recipe.recipeservice.entity.DifficultyLevel;
import com.recipe.recipeservice.entity.Cuisine;
import com.recipe.recipeservice.entity.Status;
import com.recipe.recipeservice.entity.Favourites;
import com.recipe.recipeservice.exception.IdNotFoundException;
import com.recipe.recipeservice.exception.InvalidInputException;
import com.recipe.recipeservice.exception.ResourceNotFoundException;
import com.recipe.recipeservice.repository.RecipeRepository;
import com.recipe.recipeservice.repository.CategoryRepository;
import com.recipe.recipeservice.repository.CuisineRepository;
import com.recipe.recipeservice.repository.TagRepository;
import com.recipe.recipeservice.repository.FavoritesRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import com.recipe.recipeservice.exception.DuplicateResourceException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    @Value("${project.image}")
    String path;
    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final CuisineRepository cuisineRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;
    private final FavoritesRepository favoritesRepository;
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    public Cuisine createCuisine(Cuisine cuisine) {
        return cuisineRepository.save(cuisine);
    }
    public Recipe createRecipe(AddRecipeDTO addRecipeDTO) throws InvalidInputException, IOException, MethodArgumentNotValidException, NotFoundException {
        return recipeRepository.save(mapRecipeDTOtoRecipe(addRecipeDTO));
    }
    public Recipe mapRecipeDTOtoRecipe(AddRecipeDTO addRecipeDTO) throws IOException, InvalidInputException, NotFoundException,IllegalArgumentException {
        Recipe recipe = modelMapper.map(addRecipeDTO, Recipe.class);
        System.out.println(recipe);
        System.out.println(addRecipeDTO);
        recipe.setStatus(Status.PENDING);
        recipe.setCategory(categoryRepository.findById(Long.parseLong(addRecipeDTO.getCategory())).orElseThrow(()->new IllegalArgumentException("Category not found with given id "+addRecipeDTO.getCategory())));
        recipe.setCuisine(cuisineRepository.findById(Long.parseLong(addRecipeDTO.getCuisine())).orElseThrow(()->new IllegalArgumentException("cuisine not found with id "+addRecipeDTO.getCuisine())));
        recipe.setDifficultyLevel(DifficultyLevel.valueOf(Integer.parseInt(addRecipeDTO.getDifficultyLevel())));
        List<Tag> tags = tagRepository.findAllById(addRecipeDTO.getTagIds().stream().map(Long::parseLong).collect(Collectors.toList()));
        recipe.setTags(tags);
        recipe.setImageUrl(uploadImage(path, addRecipeDTO.getFile()));
        return recipe;
    }
    public String uploadImage(String path, MultipartFile file) throws IOException, NullPointerException, InvalidInputException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException(ErrorConstants.INVALID_FILE_NAME);
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(ErrorConstants.INVALID_FILE_TYPE);
        }
        String uniqueIdentifier = UUID.randomUUID().toString();
        String newFileName = uniqueIdentifier + fileExtension;
        String filePath=path+File.separator+newFileName;
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return newFileName;
    }
    public byte[] getRecipeProfileImage(Long id) throws IOException, ResourceNotFoundException {
        Recipe user = recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.RECIPE_ID_NOT_FOUND));
        String imageUrl = path + File.separator + user.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new FileNotFoundException("Image url doesn't exist");
        }
        Path imagePath = Paths.get(imageUrl);
        if (!Files.exists(imagePath)) {
            throw new FileNotFoundException("File not found");
        }
        return Files.readAllBytes(imagePath);
    }
    @Override
    public ViewRecipeDTO getRecipe(Long id) throws ResourceNotFoundException {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.RECIPE_ID_NOT_FOUND+" "+id));
        return new ViewRecipeDTO(
                recipe.getName(),
                recipe.getIngredients(),
                recipe.getDescription(),
                recipe.getCookingTime(),
                recipe.getCuisine().getName(),
                recipe.getCategory().getName(),
                recipe.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()),
                recipe.getDifficultyLevel().name(),
                recipe.getDietaryRestrictions(),
                recipe.getUser()
        );
    }
    @Override
    public void updateRecipe(UpdateRecipeDTO recipeDTO, Long id) throws IdNotFoundException, IOException {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(()->new IdNotFoundException("Recipe not found with id: " + id));
        if (!recipe.getUser().equals(recipeDTO.getUserId())) {
            throw new IdNotFoundException("User with id: " + recipeDTO.getUserId() + " is not authorized to update this recipe.");
        }
        recipe.setName(recipeDTO.getName());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setCategory(categoryRepository.findById(Long.parseLong(recipeDTO.getCategory())).orElseThrow(()->new IdNotFoundException("Category not found")));
        recipe.setCuisine(cuisineRepository.findById(Long.parseLong(recipeDTO.getCuisine())).orElseThrow(()->new IdNotFoundException("Cuisine not  found")));
        List<Tag> tags = tagRepository.findAllById(recipeDTO.getTags().stream().map(Long::parseLong).collect(Collectors.toList()));
        recipe.setCookingTime(Integer.parseInt(recipeDTO.getCookingTime()));
        recipe.setTags(tags);
        if (recipeDTO.getFile() != null && !recipeDTO.getFile().isEmpty()) {
            recipe.setImageUrl(updateRecipeImage(path, recipeDTO.getFile()));
        }
        recipe.setDifficultyLevel(DifficultyLevel.valueOf(Integer.parseInt(recipeDTO.getDifficultyLevel())));
        recipeRepository.save(recipe);
    }
    @Override
    public String updateRecipeImage(String path, MultipartFile file) throws  IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(ErrorConstants.FILE_MUST_NOT_BE_EMPTY);
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException(ErrorConstants.FILE_NAME_MUST_NOT_BE_NULL);
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!fileExtension.equals(".png") && !fileExtension.equals(".jpg") &&            !fileExtension.equals(".jpeg") && !fileExtension.equals(".svg")) {
            throw new IllegalArgumentException(ErrorConstants.INVALID_FILE_TYPE);
        }
        String randomID = UUID.randomUUID().toString();
        String newFilename = randomID.concat(fileExtension);
        String filePath = path + File.separator + newFilename;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return newFilename;
    }

    public List<RecipeDTO> searchRecipes(String keyword) {
        List<Recipe> recipes = recipeRepository.findByKeyword(keyword);
        return recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeDTO.class))
                .collect(Collectors.toList());
    }

    public List<RecipeDTO> fetchRecipesByFilters(Long cuisineId, Long categoryId, Integer cookingTime, String difficulty) throws InvalidInputException {
        List<Recipe> recipes = recipeRepository.findRecipesByFilters(cuisineId, categoryId, cookingTime, DifficultyLevel.fromString(difficulty));
        return recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeDTO.class))
                .toList();

    }

    public ResponseEntity<SuccessResponse> editRecipeStatus(String id, String status) throws InvalidInputException {
        try {
            Long recipeId = Long.parseLong(id);
            Status recipeStatus = Status.valueOf(status.toUpperCase());

            Recipe recipe = recipeRepository.findById(Long.parseLong(id))
                    .orElseThrow(() -> new InvalidInputException(ErrorConstants.RECIPE_ID_NOT_FOUND + id));
            recipe.setStatus(recipeStatus);
            recipeRepository.save(recipe);

            SuccessResponse successResponse = SuccessResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.OK.name())
                    .message(ErrorConstants.RECIPE_STATUS_UPDATED)
                    .build();

            return ResponseEntity.ok(successResponse);
        } catch (NumberFormatException ex) {
            throw new InvalidInputException(ErrorConstants.INVALID_RECIPE_ID_FORMAT + id);
        } catch (IllegalArgumentException ex) {
            throw new InvalidInputException(ErrorConstants.INVALID_RECIPE_STATUS + status);
        }
    }

    @Override
    public RecipeFilterListDTO fetchAllRecipesByTwoFilters(Long cuisineId, Long categoryId) throws InvalidInputException {
        List<Recipe> recipes = recipeRepository.findRecipesByTwoFilters(cuisineId, categoryId);
        List<RecipeStatusChangeDTO> list = createAdminRecipeListDTO(recipes);

        RecipeFilterListDTO recipeFilterListDTO = RecipeFilterListDTO.builder()
                .recipeList(list)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.name())
                .build();

        return recipeFilterListDTO;
    }

    public List<RecipeStatusChangeDTO> createAdminRecipeListDTO(List<Recipe> recipeList) throws InvalidInputException {
        List<RecipeStatusChangeDTO> recipeList1 = recipeList.stream()
                .map(r -> modelMapper.map(r, RecipeStatusChangeDTO.class))
                .toList();

        return recipeList1;
    }

    @Override
    public CuisineFilterListDTO fetchAllCuisines() {
        List<Cuisine> cuisineList = cuisineRepository.findAll();
        CuisineFilterListDTO cuisineFilterListDTO = CuisineFilterListDTO.builder()
                .cuisineList(cuisineList)
                .status(HttpStatus.OK.name())
                .timestamp(LocalDateTime.now())
                .build();

        return cuisineFilterListDTO;
    }

    @Override
    public CategoryFilterListDTO fetchAllCategory() {
        List<Category> categoryList = categoryRepository.findAll();
        CategoryFilterListDTO categoryFilterListDTO = CategoryFilterListDTO.builder()
                .categoryList(categoryList)
                .status(HttpStatus.OK.name())
                .timestamp(LocalDateTime.now())
                .build();

        return categoryFilterListDTO;
    }
    public ApiResponse deleteRecipe(Long id) throws InvalidInputException {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Recipe not found with ID: " + id));
        recipe.setStatus(Status.REJECTED);
        recipeRepository.save(recipe);
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now()).response("Recipe status updated to REJECTED").build();
    }
    public String getRecipeOwnerId(Long recipeId) throws InvalidInputException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new InvalidInputException("Recipe not found with ID: " + recipeId));

        return recipe.getUser();
    }
    @Override
    public ApiResponse addFavoriteRecipe(String userId, Long recipeId) throws ResourceNotFoundException, DuplicateResourceException {
        Favourites favourites = getOrCreateFavourites(userId);
        Recipe recipe = getRecipeById(recipeId);
        if (favourites.getFavoriteRecipes().contains(recipe)) {
            throw new DuplicateResourceException("Recipe is already in favorites");
        }
        favourites.getFavoriteRecipes().add(recipe);
        favoritesRepository.save(favourites);
        return buildApiResponse("Recipe added to favorites successfully.");
    }

    @Override
    public ApiResponse deleteFavoriteRecipe(String userId, Long recipeId) throws ResourceNotFoundException {
        Favourites favourites = getFavoritesByUserId(userId);
        Recipe recipe = getRecipeById(recipeId);
        if (!favourites.getFavoriteRecipes().remove(recipe)) {
            throw new ResourceNotFoundException("Recipe not found in user's favorites");
        }
        favoritesRepository.save(favourites);
        return buildApiResponse("Recipe removed from favorites successfully.");
    }

    @Override
    public FavouritesRecipeResponse getFavoriteRecipes(String userId) throws ResourceNotFoundException {
        Favourites favourites = getFavoritesByUserId(userId);
        List<RecipeResponseDTO> recipeResponsDTOS = favourites.getFavoriteRecipes().stream()
                .map(this::mapToRecipeResponse)
                .collect(Collectors.toList());
        return FavouritesRecipeResponse.builder()
                .recipes(recipeResponsDTOS)
                .build();
    }

    public Favourites getOrCreateFavourites(String userId) {
        return favoritesRepository.findByUserId(userId)
                .orElse(new Favourites(null, userId, new ArrayList<>()));
    }

    public Favourites getFavoritesByUserId(String userId) throws ResourceNotFoundException {
        return favoritesRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorites not found for user"));
    }

    public Recipe getRecipeById(Long recipeId) throws ResourceNotFoundException {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
    }

    private ApiResponse buildApiResponse(String message) {
        return ApiResponse.builder()
                .response(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public  RecipeResponseDTO mapToRecipeResponse(Recipe recipe) {
        return RecipeResponseDTO.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .ingredients(recipe.getIngredients())
                .description(recipe.getDescription())
                .category(recipe.getCategory().getName())
                .cuisine(recipe.getCuisine().getName())
                .cookingTime(recipe.getCookingTime())
                .imageUrl(recipe.getImageUrl())
                .tags(recipe.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .difficultyLevel(recipe.getDifficultyLevel().name())
                .status(recipe.getStatus().name())
                .dietaryRestrictions(recipe.getDietaryRestrictions())
                .build();
    }
}

