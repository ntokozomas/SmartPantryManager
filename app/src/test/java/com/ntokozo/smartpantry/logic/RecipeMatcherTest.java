package com.ntokozo.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ntokozo.smartpantry.data.PantryItem;
import com.ntokozo.smartpantry.data.Recipe;
import com.ntokozo.smartpantry.data.RecipeIngredient;
import com.ntokozo.smartpantry.data.RecipeWithIngredients;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests for the strict-matching rule (Section 2.3 of the brief).
 */
public class RecipeMatcherTest {

    private static PantryItem pantry(String name, double quantity, String unit) {
        return new PantryItem(name, quantity, unit, null);
    }

    private static RecipeIngredient need(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static RecipeWithIngredients recipe(String name, RecipeIngredient... ingredients) {
        return new RecipeWithIngredients(new Recipe(name, "🍽️", "", ""), Arrays.asList(ingredients));
    }

    private static final RecipeWithIngredients OMELETTE = recipe("Omelette",
            need("Eggs", 3, "pcs"), need("Cheese", 50, "g"), need("Milk", 30, "ml"));

    @Test
    public void allIngredientsPresent_isSuggested() {
        List<PantryItem> items = Arrays.asList(
                pantry("Eggs", 6, "pcs"), pantry("Cheese", 200, "g"), pantry("Milk", 1, "L"));
        assertEquals(1, RecipeMatcher.findStrictMatches(items, Collections.singletonList(OMELETTE)).size());
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        RecipeWithIngredients shakshuka = recipe("Shakshuka",
                need("Eggs", 4, "pcs"), need("Tomatoes", 4, "pcs"), need("Onion", 1, "pcs"),
                need("Bell pepper", 1, "pcs"), need("Garlic", 2, "pcs"));
        List<PantryItem> items = Arrays.asList(
                pantry("Eggs", 6, "pcs"), pantry("Tomatoes", 4, "pcs"), pantry("Onion", 2, "pcs"),
                pantry("Garlic", 5, "pcs")); // no bell pepper

        List<RecipeWithIngredients> recipes = Collections.singletonList(shakshuka);
        assertTrue(RecipeMatcher.findStrictMatches(items, recipes).isEmpty());

        // ...but it does show up in the separate "almost there" list
        List<MatchResult> almost = RecipeMatcher.findAlmostThere(items, recipes);
        assertEquals(1, almost.size());
        assertEquals("Bell pepper", almost.get(0).getMissingIngredients().get(0).getName());
    }

    @Test
    public void notEnoughQuantity_isNotSuggested() {
        List<PantryItem> items = Arrays.asList(
                pantry("Eggs", 2, "pcs"), pantry("Cheese", 200, "g"), pantry("Milk", 1, "L"));
        assertTrue(RecipeMatcher.findStrictMatches(items, Collections.singletonList(OMELETTE)).isEmpty());
    }

    @Test
    public void singularAndPluralNamesMatch() {
        List<PantryItem> items = Arrays.asList(
                pantry("egg", 3, "pcs"), pantry("cheddar cheese", 50, "g"), pantry("MILK ", 30, "ml"));
        assertEquals(1, RecipeMatcher.findStrictMatches(items, Collections.singletonList(OMELETTE)).size());
    }

    @Test
    public void differentButCompatibleUnitsAreConverted() {
        RecipeWithIngredients pancakes = recipe("Pancakes",
                need("Flour", 200, "g"), need("Milk", 300, "ml"));
        List<PantryItem> items = Arrays.asList(pantry("Flour", 1, "kg"), pantry("Milk", 0.5, "L"));
        assertEquals(1, RecipeMatcher.findStrictMatches(items, Collections.singletonList(pancakes)).size());
    }

    @Test
    public void incompatibleUnits_countAsMissing() {
        List<PantryItem> items = Arrays.asList(
                pantry("Eggs", 3, "pcs"), pantry("Cheese", 2, "pcs"), pantry("Milk", 1, "L"));
        assertTrue(RecipeMatcher.findStrictMatches(items, Collections.singletonList(OMELETTE)).isEmpty());
    }

    @Test
    public void separateEntriesOfTheSameIngredientAddUp() {
        List<PantryItem> items = Arrays.asList(
                pantry("Eggs", 1, "pcs"), pantry("eggs", 2, "pcs"),
                pantry("Cheese", 50, "g"), pantry("Milk", 30, "ml"));
        assertEquals(1, RecipeMatcher.findStrictMatches(items, Collections.singletonList(OMELETTE)).size());
    }

    @Test
    public void missingTwoIngredients_isInNeitherList() {
        List<PantryItem> items = Collections.singletonList(pantry("Eggs", 3, "pcs"));
        List<RecipeWithIngredients> recipes = Collections.singletonList(OMELETTE);
        assertTrue(RecipeMatcher.findStrictMatches(items, recipes).isEmpty());
        assertTrue(RecipeMatcher.findAlmostThere(items, recipes).isEmpty());
    }

    @Test
    public void emptyPantry_suggestsNothing() {
        assertTrue(RecipeMatcher.findStrictMatches(Collections.emptyList(),
                Collections.singletonList(OMELETTE)).isEmpty());
    }
}
