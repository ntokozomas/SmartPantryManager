package com.ntokozo.smartpantry.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IngredientNormalizerTest {

    @Test
    public void pluralsBecomeSingular() {
        assertEquals("tomato", IngredientNormalizer.normalize("tomatoes"));
        assertEquals("egg", IngredientNormalizer.normalize("Eggs"));
        assertEquals("strawberry", IngredientNormalizer.normalize("Strawberries"));
        assertEquals("potato", IngredientNormalizer.normalize("POTATOES"));
    }

    @Test
    public void extraSpacesCaseAndDescriptorsAreIgnored() {
        assertEquals("tomato", IngredientNormalizer.normalize("  Fresh   Tomatoes "));
        assertEquals("egg", IngredientNormalizer.normalize("free-range eggs"));
        assertEquals("milk", IngredientNormalizer.normalize("Whole milk"));
    }

    @Test
    public void aliasesMapToOneName() {
        assertEquals("spring onion", IngredientNormalizer.normalize("Scallions"));
        assertEquals("spring onion", IngredientNormalizer.normalize("spring onions"));
        assertEquals("cheese", IngredientNormalizer.normalize("Cheddar cheese"));
        assertEquals("maize meal", IngredientNormalizer.normalize("Mealie meal"));
        assertEquals("yoghurt", IngredientNormalizer.normalize("Greek yogurt"));
        assertEquals("tomato", IngredientNormalizer.normalize("cherry tomatoes"));
        assertEquals("garlic", IngredientNormalizer.normalize("cloves of garlic"));
    }

    @Test
    public void wordsThatAreAlreadySingularAreKept() {
        assertEquals("hummus", IngredientNormalizer.normalize("Hummus"));
        assertEquals("asparagus", IngredientNormalizer.normalize("asparagus"));
        assertEquals("rice", IngredientNormalizer.normalize("rice"));
    }

    @Test
    public void accentsAndBlankInput() {
        assertEquals("jalapeno", IngredientNormalizer.normalize("Jalapeños"));
        assertEquals("", IngredientNormalizer.normalize("   "));
        assertEquals("", IngredientNormalizer.normalize(null));
    }
}
