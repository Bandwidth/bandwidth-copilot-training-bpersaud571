package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test class for RecipeService
 * Tests all public methods with happy path, error cases, and boundary conditions
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe testRecipe;
    private Recipe testRecipe2;
    private Recipe testRecipe3;

    @BeforeEach
    void setUp() {
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setName("Pasta Carbonara");
        testRecipe.setDescription("Classic Italian pasta dish");
        testRecipe.setPrepTime(10);
        testRecipe.setCookTime(15);
        testRecipe.setServings(4);
        testRecipe.setDifficultyLevel("Easy");
        testRecipe.setCuisineType("Italian");

        testRecipe2 = new Recipe();
        testRecipe2.setId(2L);
        testRecipe2.setName("Pad Thai");
        testRecipe2.setDescription("Thai stir-fried noodles");
        testRecipe2.setPrepTime(15);
        testRecipe2.setCookTime(10);
        testRecipe2.setServings(2);
        testRecipe2.setDifficultyLevel("Medium");
        testRecipe2.setCuisineType("Thai");

        testRecipe3 = new Recipe();
        testRecipe3.setId(3L);
        testRecipe3.setName("Spaghetti Bolognese");
        testRecipe3.setDescription("Italian meat sauce pasta");
        testRecipe3.setPrepTime(20);
        testRecipe3.setCookTime(30);
        testRecipe3.setServings(6);
        testRecipe3.setDifficultyLevel("Easy");
        testRecipe3.setCuisineType("Italian");
    }

    // =====================================================
    // getAllRecipes() Tests
    // =====================================================

    @Test
    void testGetAllRecipes_WhenRecipesExist_ThenReturnsAllRecipes() {
        // Arrange
        List<Recipe> recipes = Arrays.asList(testRecipe, testRecipe2, testRecipe3);
        when(recipeRepository.findAll()).thenReturn(recipes);

        // Act
        List<Recipe> result = recipeService.getAllRecipes();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Pasta Carbonara", result.get(0).getName());
        assertEquals("Pad Thai", result.get(1).getName());
        assertEquals("Spaghetti Bolognese", result.get(2).getName());
        verify(recipeRepository).findAll();
    }

    @Test
    void testGetAllRecipes_WhenNoRecipesExist_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Recipe> result = recipeService.getAllRecipes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findAll();
    }

    // =====================================================
    // getRecipeById() Tests
    // =====================================================

    @Test
    void testGetRecipeById_WhenRecipeExists_ThenReturnsRecipe() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));

        // Act
        Optional<Recipe> result = recipeService.getRecipeById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Pasta Carbonara", result.get().getName());
        assertEquals(1L, result.get().getId());
        verify(recipeRepository).findById(1L);
    }

    @Test
    void testGetRecipeById_WhenRecipeDoesNotExist_ThenReturnsEmpty() {
        // Arrange
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Recipe> result = recipeService.getRecipeById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(recipeRepository).findById(999L);
    }

    // =====================================================
    // getRecipesByDifficulty() Tests
    // =====================================================

    @Test
    void testGetRecipesByDifficulty_WhenRecipesExist_ThenReturnsFilteredRecipes() {
        // Arrange
        List<Recipe> easyRecipes = Arrays.asList(testRecipe, testRecipe3);
        when(recipeRepository.findByDifficultyLevel("Easy")).thenReturn(easyRecipes);

        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("Easy");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> "Easy".equals(r.getDifficultyLevel())));
        verify(recipeRepository).findByDifficultyLevel("Easy");
    }

    @Test
    void testGetRecipesByDifficulty_WhenNoRecipesMatch_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByDifficultyLevel("Hard")).thenReturn(new ArrayList<>());

        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("Hard");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findByDifficultyLevel("Hard");
    }

    // =====================================================
    // getRecipesByCuisine() Tests
    // =====================================================

    @Test
    void testGetRecipesByCuisine_WhenRecipesExist_ThenReturnsFilteredRecipes() {
        // Arrange
        List<Recipe> italianRecipes = Arrays.asList(testRecipe, testRecipe3);
        when(recipeRepository.findByCuisineType("Italian")).thenReturn(italianRecipes);

        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("Italian");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> "Italian".equals(r.getCuisineType())));
        verify(recipeRepository).findByCuisineType("Italian");
    }

    @Test
    void testGetRecipesByCuisine_WhenNoRecipesMatch_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByCuisineType("Mexican")).thenReturn(new ArrayList<>());

        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("Mexican");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findByCuisineType("Mexican");
    }

    // =====================================================
    // searchRecipes() Tests
    // =====================================================

    @Test
    void testSearchRecipes_WhenRecipesMatch_ThenReturnsMatchingRecipes() {
        // Arrange
        List<Recipe> matchingRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.findByNameContainingIgnoreCase("pasta")).thenReturn(matchingRecipes);

        // Act
        List<Recipe> result = recipeService.searchRecipes("pasta");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(r -> r.getName().toLowerCase().contains("pasta")));
        assertEquals("Pasta Carbonara", result.get(0).getName());
        verify(recipeRepository).findByNameContainingIgnoreCase("pasta");
    }

    @Test
    void testSearchRecipes_WhenNoRecipesMatch_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByNameContainingIgnoreCase("burger")).thenReturn(new ArrayList<>());

        // Act
        List<Recipe> result = recipeService.searchRecipes("burger");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findByNameContainingIgnoreCase("burger");
    }

    @Test
    void testSearchRecipes_WhenSearchTermIsEmpty_ThenStillCallsRepository() {
        // Arrange
        when(recipeRepository.findByNameContainingIgnoreCase("")).thenReturn(Arrays.asList(testRecipe, testRecipe2, testRecipe3));

        // Act
        List<Recipe> result = recipeService.searchRecipes("");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(recipeRepository).findByNameContainingIgnoreCase("");
    }

    // =====================================================
    // saveRecipe() Tests
    // =====================================================

    @Test
    @SuppressWarnings("null")
    void testSaveRecipe_WhenNewRecipe_ThenSavesAndReturnsRecipe() {
        // Arrange
        Recipe newRecipe = new Recipe();
        newRecipe.setName("New Recipe");
        newRecipe.setDescription("A new recipe");
        newRecipe.setDifficultyLevel("Easy");

        when(recipeRepository.save(any(Recipe.class))).thenReturn(testRecipe);

        // Act
        Recipe result = recipeService.saveRecipe(newRecipe);

        // Assert
        assertNotNull(result);
        assertEquals("Pasta Carbonara", result.getName());
        verify(recipeRepository).save(eq(newRecipe));
    }

    @Test
    @SuppressWarnings("null")
    void testSaveRecipe_WhenUpdatingExistingRecipe_ThenSavesAndReturnsUpdatedRecipe() {
        // Arrange
        testRecipe.setDescription("Updated description");
        when(recipeRepository.save(eq(testRecipe))).thenReturn(testRecipe);

        // Act
        Recipe result = recipeService.saveRecipe(testRecipe);

        // Assert
        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        assertEquals(1L, result.getId());
        verify(recipeRepository).save(eq(testRecipe));
    }

    @Test
    @SuppressWarnings("null")
    void testSaveRecipe_WhenRecipeHasAllFields_ThenSavesSuccessfully() {
        // Arrange
        when(recipeRepository.save(eq(testRecipe))).thenReturn(testRecipe);

        // Act
        Recipe result = recipeService.saveRecipe(testRecipe);

        // Assert
        assertNotNull(result);
        assertEquals("Pasta Carbonara", result.getName());
        assertEquals("Classic Italian pasta dish", result.getDescription());
        assertEquals(10, result.getPrepTime());
        assertEquals(15, result.getCookTime());
        assertEquals(4, result.getServings());
        assertEquals("Easy", result.getDifficultyLevel());
        assertEquals("Italian", result.getCuisineType());
        verify(recipeRepository).save(eq(testRecipe));
    }

    // =====================================================
    // deleteRecipe() Tests
    // =====================================================

    @Test
    void testDeleteRecipe_WhenRecipeExists_ThenDeletesSuccessfully() {
        // Arrange
        doNothing().when(recipeRepository).deleteById(1L);

        // Act
        recipeService.deleteRecipe(1L);

        // Assert
        verify(recipeRepository).deleteById(1L);
    }

    @Test
    void testDeleteRecipe_WhenRecipeDoesNotExist_ThenStillCallsDelete() {
        // Arrange
        doNothing().when(recipeRepository).deleteById(999L);

        // Act
        recipeService.deleteRecipe(999L);

        // Assert
        verify(recipeRepository).deleteById(999L);
    }

    // =====================================================
    // getRecipeOfTheDay() Tests
    // =====================================================

    @Test
    void testGetRecipeOfTheDay_WhenRecipesExist_ThenReturnsDeterministicRecipe() {
        // Arrange
        List<Recipe> recipes = Arrays.asList(testRecipe, testRecipe2, testRecipe3);
        when(recipeRepository.findAll()).thenReturn(recipes);

        // Act
        Recipe result = recipeService.getRecipeOfTheDay();

        // Assert
        assertNotNull(result);
        assertTrue(recipes.contains(result));
        verify(recipeRepository).findAll();
    }

    @Test
    void testGetRecipeOfTheDay_WhenNoRecipesExist_ThenReturnsNull() {
        // Arrange
        when(recipeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Recipe result = recipeService.getRecipeOfTheDay();

        // Assert
        assertNull(result);
        verify(recipeRepository).findAll();
    }

    @Test
    void testGetRecipeOfTheDay_WhenOnlyOneRecipeExists_ThenReturnsTheRecipe() {
        // Arrange
        List<Recipe> recipes = Arrays.asList(testRecipe);
        when(recipeRepository.findAll()).thenReturn(recipes);

        // Act
        Recipe result = recipeService.getRecipeOfTheDay();

        // Assert
        assertNotNull(result);
        assertEquals(testRecipe.getId(), result.getId());
        assertEquals("Pasta Carbonara", result.getName());
        verify(recipeRepository).findAll();
    }

    @Test
    void testGetRecipeOfTheDay_WhenCalledMultipleTimes_ThenReturnsSameRecipeForSameDay() {
        // Arrange
        List<Recipe> recipes = Arrays.asList(testRecipe, testRecipe2, testRecipe3);
        when(recipeRepository.findAll()).thenReturn(recipes);

        // Act
        Recipe result1 = recipeService.getRecipeOfTheDay();
        Recipe result2 = recipeService.getRecipeOfTheDay();

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getId(), result2.getId());
        verify(recipeRepository, times(2)).findAll();
    }
}
