package com.ntokozo.smartpantry.logic;

import com.ntokozo.smartpantry.data.PantryItem;
import com.ntokozo.smartpantry.data.RecipeIngredient;
import com.ntokozo.smartpantry.data.RecipeWithIngredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The STRICT-MATCHING RULE (core business logic).
 *
 * A recipe is suggested only if EVERY ingredient it needs is in the pantry
 * in AT LEAST the required quantity. Names are normalised (tomatoes == tomato)
 * and units are converted (1 kg == 1000 g) before comparing.
 * If quantities can't be compared (e.g. recipe needs 50 g cheese, pantry says 2 pcs),
 * the ingredient counts as missing - we never guess in the user's favour.
 */
public final class RecipeMatcher {

    /** Small allowance for floating point rounding (e.g. 0.1 + 0.2). */
    private static final double TOLERANCE = 1e-6;

    private RecipeMatcher() {
    }

    /**
     * Groups pantry items by their normalised name, e.g.
     * {"egg" -> [3 pcs, 2 pcs], "flour" -> [1000 g]}.
     */
    public static Map<String, List<UnitConverter.Amount>> buildPantryIndex(List<PantryItem> pantry) {
        Map<String, List<UnitConverter.Amount>> index = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = IngredientNormalizer.normalize(item.getName());
            if (key.isEmpty() || item.getQuantity() <= 0) {
                continue;
            }
            List<UnitConverter.Amount> amounts = index.get(key);
            if (amounts == null) {
                amounts = new ArrayList<>();
                index.put(key, amounts);
            }
            amounts.add(UnitConverter.toBase(item.getQuantity(), item.getUnit()));
        }
        return index;
    }

    /** Does the pantry hold enough of this single recipe ingredient? */
    public static boolean hasEnough(Map<String, List<UnitConverter.Amount>> pantryIndex,
                                    RecipeIngredient ingredient) {
        List<UnitConverter.Amount> available =
                pantryIndex.get(IngredientNormalizer.normalize(ingredient.getName()));
        if (available == null) {
            return false; // not in the pantry at all
        }
        UnitConverter.Amount required =
                UnitConverter.toBase(ingredient.getQuantity(), ingredient.getUnit());

        double total = 0;
        for (UnitConverter.Amount amount : available) {
            if (amount.isComparableWith(required)) {
                total += amount.value; // add up e.g. two separate egg entries
            }
        }
        return total + TOLERANCE >= required.value;
    }

    /** Checks one recipe and records which ingredients are missing. */
    public static MatchResult evaluate(RecipeWithIngredients recipe,
                                       Map<String, List<UnitConverter.Amount>> pantryIndex) {
        List<RecipeIngredient> missing = new ArrayList<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (!hasEnough(pantryIndex, ingredient)) {
                missing.add(ingredient);
            }
        }
        return new MatchResult(recipe, missing);
    }

    /** Recipes the user can cook RIGHT NOW (zero missing ingredients). */
    public static List<MatchResult> findStrictMatches(List<PantryItem> pantry,
                                                      List<RecipeWithIngredients> recipes) {
        Map<String, List<UnitConverter.Amount>> index = buildPantryIndex(pantry);
        List<MatchResult> matches = new ArrayList<>();
        for (RecipeWithIngredients recipe : recipes) {
            MatchResult result = evaluate(recipe, index);
            if (result.isStrictMatch()) {
                matches.add(result);
            }
        }
        return matches;
    }

    /** Bonus list: recipes missing exactly ONE ingredient. Kept separate from strict matches. */
    public static List<MatchResult> findAlmostThere(List<PantryItem> pantry,
                                                    List<RecipeWithIngredients> recipes) {
        Map<String, List<UnitConverter.Amount>> index = buildPantryIndex(pantry);
        List<MatchResult> almost = new ArrayList<>();
        for (RecipeWithIngredients recipe : recipes) {
            MatchResult result = evaluate(recipe, index);
            if (result.getMissingCount() == 1) {
                almost.add(result);
            }
        }
        return almost;
    }
}
