package com.coveros.training.flavorhub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.coveros.training.flavorhub.model.Recipe;

/**
 * Unit tests for UserMealPlanService.
 * Contract-first: positive and negative tests, even if not implemented yet.
 */
class UserMealPlanServiceTest {

	private UserMealPlanService userMealPlanService;

	@BeforeEach
	void setUp() {
		userMealPlanService = new UserMealPlanService();
	}

	@Test
	void testCreateMealPlan_ThrowsUnsupportedOperationException() {
		Long userId = 1L;
		List<Recipe> mealPlanData = new ArrayList<>();
		assertThrows(UnsupportedOperationException.class, () ->
				userMealPlanService.createMealPlan(userId, mealPlanData));
	}

	@Test
	void testGetMealPlanForUser_ThrowsUnsupportedOperationException() {
		Long userId = 1L;
		assertThrows(UnsupportedOperationException.class, () ->
				userMealPlanService.getMealPlanForUser(userId));
	}

	// Positive contract tests (will fail until implemented)
	@Test
	void testCreateAndGetMealPlan_Positive() {
		Long userId = 42L;
		Recipe recipe1 = new Recipe();
		Recipe recipe2 = new Recipe();
		List<Recipe> mealPlan = Arrays.asList(recipe1, recipe2);
		userMealPlanService.createMealPlan(userId, mealPlan);
		List<Recipe> result = userMealPlanService.getMealPlanForUser(userId);
		assertEquals(mealPlan, result);
	}

	@Test
	void testGetMealPlanForUser_EmptyIfNone() {
		Long userId = 99L;
		List<Recipe> result = userMealPlanService.getMealPlanForUser(userId);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
