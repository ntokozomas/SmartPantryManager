package com.ntokozo.smartpantry.data;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe together with all of its ingredient rows.
 * Room fills this in automatically using the @Relation (one recipe -> many ingredients).
 */
public class RecipeWithIngredients {

    @Embedded
    public Recipe recipe;

    @Relation(parentColumn = "id", entityColumn = "recipeId")
    public List<RecipeIngredient> ingredients;

    /** Required by Room. */
    public RecipeWithIngredients() {
    }

    /** Convenience constructor (used by unit tests). */
    @Ignore
    public RecipeWithIngredients(Recipe recipe, List<RecipeIngredient> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    @NonNull
    public List<RecipeIngredient> getIngredients() {
        return ingredients != null ? ingredients : new ArrayList<>();
    }
}
