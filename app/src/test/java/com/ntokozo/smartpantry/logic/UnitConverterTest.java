package com.ntokozo.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnitConverterTest {

    private static final double DELTA = 1e-9;

    @Test
    public void massConvertsToGrams() {
        assertEquals(1000, UnitConverter.toBase(1, "kg").value, DELTA);
        assertEquals(250, UnitConverter.toBase(250, "g").value, DELTA);
    }

    @Test
    public void volumeConvertsToMillilitres() {
        assertEquals(1500, UnitConverter.toBase(1.5, "L").value, DELTA);
        assertEquals(30, UnitConverter.toBase(2, "tbsp").value, DELTA);
        assertEquals(250, UnitConverter.toBase(1, "cup").value, DELTA);
    }

    @Test
    public void countUnitsAreEquivalent() {
        UnitConverter.Amount pieces = UnitConverter.toBase(3, "Pieces");
        UnitConverter.Amount pcs = UnitConverter.toBase(3, "pcs");
        assertEquals(UnitConverter.Dimension.COUNT, pieces.dimension);
        assertTrue(pieces.isComparableWith(pcs));
    }

    @Test
    public void differentDimensionsAreNotComparable() {
        assertFalse(UnitConverter.toBase(1, "kg").isComparableWith(UnitConverter.toBase(1, "L")));
        assertFalse(UnitConverter.toBase(1, "pcs").isComparableWith(UnitConverter.toBase(1, "g")));
    }

    @Test
    public void unknownUnitsOnlyMatchTheSameUnit() {
        UnitConverter.Amount pinch = UnitConverter.toBase(1, "pinch");
        assertEquals(UnitConverter.Dimension.OTHER, pinch.dimension);
        assertTrue(pinch.isComparableWith(UnitConverter.toBase(2, "Pinch")));
        assertFalse(pinch.isComparableWith(UnitConverter.toBase(2, "handful")));
    }
}
