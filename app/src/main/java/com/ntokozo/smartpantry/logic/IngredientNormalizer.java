package com.ntokozo.smartpantry.logic;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Turns messy, user-typed ingredient names into one canonical form so they can be compared.
 * Examples: "Tomatoes", " tomato ", "Fresh tomatoes" and "Cherry Tomatoes" all become "tomato".
 * Pure Java (no Android classes) so it can be unit tested.
 */
public final class IngredientNormalizer {

    /** Describing words that do not change what the ingredient is. */
    private static final Set<String> DESCRIPTOR_WORDS = new HashSet<>(Arrays.asList(
            "fresh", "large", "small", "medium", "big", "chopped", "sliced", "diced", "ripe",
            "raw", "frozen", "dried", "grated", "whole", "organic", "free", "range", "can",
            "tin", "tinned", "canned", "of"));

    /** Words ending in "s" that are already singular. */
    private static final Set<String> SINGULAR_EXCEPTIONS = new HashSet<>(Arrays.asList(
            "hummus", "couscous", "asparagus", "molasses", "citrus", "swiss", "grass",
            "bass", "series", "species", "harissa"));

    /** Different names for the same ingredient -> one canonical name (keys are already singular). */
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        alias("tomato", "cherry tomato", "roma tomato", "plum tomato");
        alias("spring onion", "green onion", "scallion");
        alias("onion", "red onion", "white onion", "brown onion", "yellow onion");
        alias("bell pepper", "green pepper", "red pepper", "yellow pepper", "sweet pepper",
                "capsicum");
        alias("chicken", "chicken breast", "chicken thigh", "chicken fillet", "chicken drumstick",
                "chicken piece");
        alias("beef mince", "minced beef", "ground beef", "mince");
        alias("cheese", "cheddar", "cheddar cheese", "mozzarella", "gouda");
        alias("pasta", "spaghetti", "macaroni", "penne", "fusilli");
        alias("rice", "white rice", "brown rice", "basmati rice", "jasmine rice");
        alias("milk", "full cream milk", "low fat milk", "skim milk");
        alias("yoghurt", "yogurt", "plain yoghurt", "plain yogurt", "greek yoghurt",
                "greek yogurt");
        alias("flour", "cake flour", "bread flour", "plain flour", "all purpose flour",
                "self raising flour", "wheat flour");
        alias("sugar", "white sugar", "brown sugar", "caster sugar");
        alias("oil", "olive oil", "sunflower oil", "vegetable oil", "cooking oil", "canola oil");
        alias("oat", "rolled oat", "oatmeal");
        alias("maize meal", "mealie meal", "mielie meal", "maizemeal", "mealiemeal");
        alias("garlic", "garlic clove", "clove garlic");
        alias("bread", "white bread", "brown bread", "bread slice", "slice bread");
        alias("baked bean", "bean in tomato sauce");
        alias("mushroom", "button mushroom", "brown mushroom");
        alias("soy sauce", "soya sauce");
        alias("chili", "chilli", "chile");
    }

    private IngredientNormalizer() {
    }

    private static void alias(String canonical, String... others) {
        for (String other : others) {
            ALIASES.put(other, canonical);
        }
    }

    /** Returns the canonical form of an ingredient name, or "" if the input is blank. */
    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String cleaned = stripAccents(raw).toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z\\s]", " ")   // drop digits, punctuation, emoji
                .trim()
                .replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return "";
        }

        String withoutDescriptors = joinSingular(cleaned, true);
        // If every word was a descriptor (e.g. just "fresh"), keep the words instead.
        String result = withoutDescriptors.isEmpty() ? joinSingular(cleaned, false) : withoutDescriptors;

        String alias = ALIASES.get(result);
        return alias != null ? alias : result;
    }

    private static String joinSingular(String cleaned, boolean skipDescriptors) {
        StringBuilder builder = new StringBuilder();
        for (String word : cleaned.split(" ")) {
            if (skipDescriptors && DESCRIPTOR_WORDS.contains(word)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(singularize(word));
        }
        return builder.toString();
    }

    /** Very small English singularizer: tomatoes -> tomato, berries -> berry, eggs -> egg. */
    static String singularize(String word) {
        if (word.length() <= 3 || SINGULAR_EXCEPTIONS.contains(word)) {
            return word;
        }
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes") || word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("xes") || word.endsWith("sses")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("us")
                && !word.endsWith("is")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    private static String stripAccents(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
