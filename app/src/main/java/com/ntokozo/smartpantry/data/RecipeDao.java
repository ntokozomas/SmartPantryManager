package com.ntokozo.smartpantry.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

/**
 * Queries for the recipes and recipe_ingredients tables.
 */
@Dao
public interface RecipeDao {

    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    /**
     * Inserts a recipe and its ingredients in one transaction,
     * linking every ingredient to the new recipe's generated id.
     */
    @Transaction
    default void insertRecipeWithIngredients(Recipe recipe, List<RecipeIngredient> ingredients) {
        long recipeId = insertRecipe(recipe);
        for (RecipeIngredient ingredient : ingredients) {
            ingredient.setRecipeId(recipeId);
        }
        insertIngredients(ingredients);
    }

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name COLLATE NOCASE")
    List<RecipeWithIngredients> getAllWithIngredients();

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    RecipeWithIngredients getById(long id);

    @Query("SELECT COUNT(*) FROM recipes")
    int count();
}
