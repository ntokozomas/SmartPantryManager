package com.ntokozo.smartpantry.logic;

import com.ntokozo.smartpantry.data.Recipe;
import com.ntokozo.smartpantry.data.RecipeIngredient;
import com.ntokozo.smartpantry.data.RecipeWithIngredients;

import java.util.Collections;
import java.util.List;

/**
 * The outcome of checking one recipe against the pantry:
 * which ingredients (if any) are missing or not available in a large enough quantity.
 */
public final class MatchResult {

    private final RecipeWithIngredients recipeWithIngredients;
    private final List<RecipeIngredient> missingIngredients;

    public MatchResult(RecipeWithIngredients recipeWithIngredients,
                       List<RecipeIngredient> missingIngredients) {
        this.recipeWithIngredients = recipeWithIngredients;
        this.missingIngredients = Collections.unmodifiableList(missingIngredients);
    }

    public RecipeWithIngredients getRecipeWithIngredients() {
        return recipeWithIngredients;
    }

    public Recipe getRecipe() {
        return recipeWithIngredients.getRecipe();
    }

    public List<RecipeIngredient> getMissingIngredients() {
        return missingIngredients;
    }

    public int getMissingCount() {
        return missingIngredients.size();
    }

    /** True only when every required ingredient is in the pantry in at least the required quantity. */
    public boolean isStrictMatch() {
        return missingIngredients.isEmpty() && !recipeWithIngredients.getIngredients().isEmpty();
    }
}
