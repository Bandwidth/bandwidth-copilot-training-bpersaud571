package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.model.RecipeRating;
import com.coveros.training.flavorhub.repository.RecipeRatingRepository;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test class for RecipeRatingService
 * Tests all public methods with happy path, error cases, and boundary conditions
 */
@ExtendWith(MockitoExtension.class)
class RecipeRatingServiceTest {

    @Mock
    private RecipeRatingRepository ratingRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeRatingService ratingService;

    private Recipe testRecipe;
    private RecipeRating testRating;
    private RecipeRating testRating2;

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

        testRating = new RecipeRating();
        testRating.setId(1L);
        testRating.setRecipe(testRecipe);
        testRating.setUserId(1L);
        testRating.setRating(5);
        testRating.setReview("Delicious recipe!");
        testRating.setCreatedAt(LocalDateTime.now());
        testRating.setUpdatedAt(LocalDateTime.now());

        testRating2 = new RecipeRating();
        testRating2.setId(2L);
        testRating2.setRecipe(testRecipe);
        testRating2.setUserId(2L);
        testRating2.setRating(4);
        testRating2.setReview("Good recipe, but could be better");
        testRating2.setCreatedAt(LocalDateTime.now());
        testRating2.setUpdatedAt(LocalDateTime.now());
    }

    // =====================================================
    // submitRating() Tests
    // =====================================================

    @Test
    void testSubmitRating_WhenValid_ThenCreatesRating() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(ratingRepository.findByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(RecipeRating.class))).thenReturn(testRating);

        // Act
        RecipeRating result = ratingService.submitRating(1L, 1L, 5, "Delicious recipe!");

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Delicious recipe!", result.getReview());
        assertEquals(1L, result.getUserId());
        verify(recipeRepository).findById(1L);
        verify(ratingRepository).findByRecipeIdAndUserId(1L, 1L);
        verify(ratingRepository).save(any(RecipeRating.class));
    }

    @Test
    void testSubmitRating_WhenRecipeNotFound_ThenThrowsException() {
        // Arrange
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ratingService.submitRating(999L, 1L, 5, "Test review")
        );
        assertTrue(exception.getMessage().contains("Recipe not found"));
        verify(recipeRepository).findById(999L);
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void testSubmitRating_WhenUserAlreadyRated_ThenThrowsException() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(ratingRepository.findByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.of(testRating));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ratingService.submitRating(1L, 1L, 5, "Another review")
        );
        assertTrue(exception.getMessage().contains("already rated"));
        verify(recipeRepository).findById(1L);
        verify(ratingRepository).findByRecipeIdAndUserId(1L, 1L);
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void testSubmitRating_WhenNoReview_ThenCreatesRatingWithoutReview() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(ratingRepository.findByRecipeIdAndUserId(1L, 3L)).thenReturn(Optional.empty());
        
        RecipeRating ratingWithoutReview = new RecipeRating();
        ratingWithoutReview.setId(3L);
        ratingWithoutReview.setRecipe(testRecipe);
        ratingWithoutReview.setUserId(3L);
        ratingWithoutReview.setRating(4);
        ratingWithoutReview.setReview(null);
        
        when(ratingRepository.save(any(RecipeRating.class))).thenReturn(ratingWithoutReview);

        // Act
        RecipeRating result = ratingService.submitRating(1L, 3L, 4, null);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertNull(result.getReview());
        verify(ratingRepository).save(any(RecipeRating.class));
    }

    // =====================================================
    // updateRating() Tests
    // =====================================================

    @Test
    void testUpdateRating_WhenExists_ThenUpdatesRating() {
        // Arrange
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));
        when(ratingRepository.save(any(RecipeRating.class))).thenReturn(testRating);

        // Act
        RecipeRating result = ratingService.updateRating(1L, 3, "Updated review");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getRating());
        assertEquals("Updated review", result.getReview());
        verify(ratingRepository).findById(1L);
        verify(ratingRepository).save(testRating);
    }

    @Test
    void testUpdateRating_WhenNotFound_ThenThrowsException() {
        // Arrange
        when(ratingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ratingService.updateRating(999L, 4, "Test")
        );
        assertTrue(exception.getMessage().contains("Rating not found"));
        verify(ratingRepository).findById(999L);
        verify(ratingRepository, never()).save(any());
    }

    // =====================================================
    // getRatingsForRecipe() Tests
    // =====================================================

    @Test
    void testGetRatingsForRecipe_WhenRatingsExist_ThenReturnsRatings() {
        // Arrange
        List<RecipeRating> ratings = Arrays.asList(testRating, testRating2);
        when(ratingRepository.findByRecipeId(1L)).thenReturn(ratings);

        // Act
        List<RecipeRating> result = ratingService.getRatingsForRecipe(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getRating());
        assertEquals(4, result.get(1).getRating());
        verify(ratingRepository).findByRecipeId(1L);
    }

    @Test
    void testGetRatingsForRecipe_WhenNoRatings_ThenReturnsEmptyList() {
        // Arrange
        when(ratingRepository.findByRecipeId(1L)).thenReturn(List.of());

        // Act
        List<RecipeRating> result = ratingService.getRatingsForRecipe(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ratingRepository).findByRecipeId(1L);
    }

    // =====================================================
    // getAverageRating() Tests
    // =====================================================

    @Test
    void testGetAverageRating_WhenMultipleRatings_ThenCalculatesAverage() {
        // Arrange
        when(ratingRepository.getAverageRatingForRecipe(1L)).thenReturn(4.5);

        // Act
        Double result = ratingService.getAverageRating(1L);

        // Assert
        assertNotNull(result);
        assertEquals(4.5, result);
        verify(ratingRepository).getAverageRatingForRecipe(1L);
    }

    @Test
    void testGetAverageRating_WhenNoRatings_ThenReturnsNull() {
        // Arrange
        when(ratingRepository.getAverageRatingForRecipe(1L)).thenReturn(null);

        // Act
        Double result = ratingService.getAverageRating(1L);

        // Assert
        assertNull(result);
        verify(ratingRepository).getAverageRatingForRecipe(1L);
    }

    // =====================================================
    // getRatingCount() Tests
    // =====================================================

    @Test
    void testGetRatingCount_WhenRatingsExist_ThenReturnsCount() {
        // Arrange
        when(ratingRepository.countByRecipeId(1L)).thenReturn(5L);

        // Act
        Long result = ratingService.getRatingCount(1L);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result);
        verify(ratingRepository).countByRecipeId(1L);
    }

    @Test
    void testGetRatingCount_WhenNoRatings_ThenReturnsZero() {
        // Arrange
        when(ratingRepository.countByRecipeId(1L)).thenReturn(0L);

        // Act
        Long result = ratingService.getRatingCount(1L);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result);
        verify(ratingRepository).countByRecipeId(1L);
    }

    // =====================================================
    // deleteRating() Tests
    // =====================================================

    @Test
    void testDeleteRating_WhenExists_ThenDeletesRating() {
        // Arrange
        doNothing().when(ratingRepository).deleteById(1L);

        // Act
        ratingService.deleteRating(1L);

        // Assert
        verify(ratingRepository).deleteById(1L);
    }

    // =====================================================
    // getUserRatingForRecipe() Tests
    // =====================================================

    @Test
    void testGetUserRatingForRecipe_WhenExists_ThenReturnsRating() {
        // Arrange
        when(ratingRepository.findByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.of(testRating));

        // Act
        Optional<RecipeRating> result = ratingService.getUserRatingForRecipe(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getRating());
        assertEquals("Delicious recipe!", result.get().getReview());
        verify(ratingRepository).findByRecipeIdAndUserId(1L, 1L);
    }

    @Test
    void testGetUserRatingForRecipe_WhenNotExists_ThenReturnsEmpty() {
        // Arrange
        when(ratingRepository.findByRecipeIdAndUserId(1L, 999L)).thenReturn(Optional.empty());

        // Act
        Optional<RecipeRating> result = ratingService.getUserRatingForRecipe(1L, 999L);

        // Assert
        assertFalse(result.isPresent());
        verify(ratingRepository).findByRecipeIdAndUserId(1L, 999L);
    }
}
