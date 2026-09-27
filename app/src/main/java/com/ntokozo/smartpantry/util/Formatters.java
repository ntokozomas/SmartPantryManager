package com.ntokozo.smartpantry.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Small helpers for showing numbers and dates nicely. */
public final class Formatters {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault());

    private Formatters() {
    }

    /** 3.0 -> "3", 1.5 -> "1.5", 0.333 -> "0.33" */
    public static String quantity(double quantity) {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return new DecimalFormat("0.##").format(quantity);
    }

    public static String date(long epochDay) {
        return LocalDate.ofEpochDay(epochDay).format(DATE_FORMAT);
    }

    /** Negative = already expired, 0 = expires today. */
    public static long daysUntil(long epochDay) {
        return epochDay - LocalDate.now().toEpochDay();
    }
}
