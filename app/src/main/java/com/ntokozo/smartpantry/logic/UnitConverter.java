package com.ntokozo.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts quantities to a common base unit so different units can be compared:
 * mass -> grams, volume -> millilitres, countable items -> pieces.
 * e.g. 1 kg == 1000 g, 2 tbsp == 30 ml.
 */
public final class UnitConverter {

    public enum Dimension { MASS, VOLUME, COUNT, OTHER }

    /** A quantity expressed in its dimension's base unit. */
    public static final class Amount {
        public final Dimension dimension;
        public final double value;
        /** The normalised unit text (only needed to compare OTHER / unknown units). */
        public final String unitKey;

        Amount(Dimension dimension, double value, String unitKey) {
            this.dimension = dimension;
            this.value = value;
            this.unitKey = unitKey;
        }

        /** Two amounts can be compared only if they measure the same kind of thing. */
        public boolean isComparableWith(Amount other) {
            if (dimension != other.dimension) {
                return false;
            }
            return dimension != Dimension.OTHER || unitKey.equals(other.unitKey);
        }
    }

    private static final Map<String, Dimension> DIMENSIONS = new HashMap<>();
    private static final Map<String, Double> FACTORS = new HashMap<>();

    static {
        register(Dimension.MASS, 1, "g", "gr", "gram", "grams");
        register(Dimension.MASS, 1000, "kg", "kgs", "kilo", "kilos", "kilogram", "kilograms");
        register(Dimension.MASS, 0.001, "mg", "milligram", "milligrams");

        register(Dimension.VOLUME, 1, "ml", "millilitre", "millilitres", "milliliter", "milliliters");
        register(Dimension.VOLUME, 1000, "l", "lt", "litre", "litres", "liter", "liters");
        register(Dimension.VOLUME, 5, "tsp", "teaspoon", "teaspoons");
        register(Dimension.VOLUME, 15, "tbsp", "tbs", "tablespoon", "tablespoons");
        register(Dimension.VOLUME, 250, "cup", "cups");

        register(Dimension.COUNT, 1, "", "pc", "pcs", "piece", "pieces", "x", "each", "ea",
                "whole", "item", "items", "unit", "units");
    }

    private UnitConverter() {
    }

    private static void register(Dimension dimension, double factor, String... names) {
        for (String name : names) {
            DIMENSIONS.put(name, dimension);
            FACTORS.put(name, factor);
        }
    }

    public static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        String key = unit.trim().toLowerCase(Locale.ROOT);
        while (key.endsWith(".")) {
            key = key.substring(0, key.length() - 1);
        }
        return key;
    }

    public static Amount toBase(double quantity, String unit) {
        String key = normalizeUnit(unit);
        Dimension dimension = DIMENSIONS.get(key);
        if (dimension == null) {
            return new Amount(Dimension.OTHER, quantity, key);
        }
        Double factor = FACTORS.get(key);
        return new Amount(dimension, quantity * (factor != null ? factor : 1), key);
    }
}
