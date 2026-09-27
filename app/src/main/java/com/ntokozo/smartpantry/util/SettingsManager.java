package com.ntokozo.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores the user's simple app preferences (switches on the Settings screen)
 * in SharedPreferences. Pantry data itself lives in the Room database.
 */
public class SettingsManager {

    /** Items expiring within this many days get highlighted. */
    public static final int EXPIRY_WARNING_DAYS = 3;

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_HIGHLIGHT_EXPIRING = "highlight_expiring";
    private static final String KEY_SHOW_ALMOST_THERE = "show_almost_there";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryHighlightEnabled() {
        return prefs.getBoolean(KEY_HIGHLIGHT_EXPIRING, true);
    }

    public void setExpiryHighlightEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_HIGHLIGHT_EXPIRING, enabled).apply();
    }

    public boolean isAlmostThereEnabled() {
        return prefs.getBoolean(KEY_SHOW_ALMOST_THERE, true);
    }

    public void setAlmostThereEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SHOW_ALMOST_THERE, enabled).apply();
    }
}
